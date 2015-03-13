/*
 * Software perteneciente al trabajo de Tésis:
 * Analisis Comparativo de Tecnicas de IA para la Correccion de la Inconsistencia
 * de la Matriz del Proceso Analítico Jerarquico
 * Autor: Federico Matias Rodriguez - rfedericomatias@gmail.com
 * Autor: Marianela Daianna Labat - daiannalabat@gmail.com
 * Año: 2014
 * Universidad Gaston Dachary
 */
package ahp;

import Jama.Matrix;
import interfaces.Vistas;
import java.util.Calendar;
import java.util.HashMap;
import machinelearning.RedNeuronalBackpropagation;
import machinelearning.SupportVectorMachine;
import persistence.ConexionBD;

public final class Controladora {

    private AHPcalculos ahp;
    private AHPproblema problema;
    private Vistas vistas;
    private ConexionBD db;

    public Controladora() {
        init();
    }

    public void init() {
        ahp = new AHPcalculos();
        vistas = new Vistas(this);
        vistas.desplegarFrameContenedor();
        db = new ConexionBD(vistas);
    }

    public void crearNuevoProyecto(int criterios, int alternativas) {
        problema = new AHPproblema();
        problema.setId(db.obtenerSiguienteId("proyecto"));
        problema.setCriterios(criterios);
        problema.setAlternativas(alternativas);
        problema.setFechaCreacion(Calendar.getInstance());
        //Creacion de una matriz de paridad de criterios
        AHPmatriz criteriaMatrix = new AHPmatriz(new Matrix(criterios, criterios));
        criteriaMatrix.setCr(-1.0);
        problema.setCriteriaWeight(criteriaMatrix);
        //Creacion de una matriz de paridad por cada criterio
        HashMap<Integer, AHPmatriz> mapAlternatives = new HashMap<>();
        for (int i = 1; i <= criterios; i++) {
            //Comparativa de paridad de alternativas
            AHPmatriz alternativeMatrix = new AHPmatriz(new Matrix(alternativas, alternativas));
            alternativeMatrix.setCr(-1.0);
            mapAlternatives.put(i, alternativeMatrix);
        }
        problema.setAlternativesWeight(mapAlternatives);
        //Creacion de la matriz de resultados
        AHPmatriz resultMatrix = new AHPmatriz(new Matrix(alternativas, criterios));
        problema.setResult(resultMatrix);
        vistas.desplegarPanelCriteriaWeight();
    }

    public void resolverPonderacionCriterios(Matrix matrizPonderacionCriterios) {
        problema.getCriteriaWeight().setMainMatrix(matrizPonderacionCriterios);
        double lambdaMax = ahp.calcularLambdaMax(problema.getCriteriaWeight().getMainMatrix());
        problema.getCriteriaWeight().setLambdaMax(lambdaMax);
        double ci = ahp.calcularCI(problema.getCriteriaWeight().getMainMatrix());
        problema.getCriteriaWeight().setCi(ci);
        double ri = ahp.calcularRI(problema.getCriteriaWeight().getMainMatrix());
        problema.getCriteriaWeight().setRi(ri);
        double cr = ahp.calcularCR(ci, ri);
        problema.getCriteriaWeight().setCr(cr);
        problema.getCriteriaWeight().setNormalizedMatrix(
                ahp.calcularMatrizNormalizada(problema.getCriteriaWeight().getMainMatrix())
        );
        problema.getCriteriaWeight().setPriorityVector(
                ahp.obtenerVectorPrioridades(problema.getCriteriaWeight().getNormalizedMatrix())
        );
    }

    public void resolverPonderacionAlternativa(int indiceMapa, Matrix ponderacionCriterios) {
        problema.getAlternativesWeight().get(indiceMapa).setMainMatrix(ponderacionCriterios);
        double lambdaMax = ahp.calcularLambdaMax(ponderacionCriterios);
        problema.getAlternativesWeight().get(indiceMapa).setLambdaMax(lambdaMax);
        double ci = ahp.calcularCI(ponderacionCriterios);
        problema.getAlternativesWeight().get(indiceMapa).setCi(ci);
        double ri = ahp.calcularRI(ponderacionCriterios);
        problema.getAlternativesWeight().get(indiceMapa).setRi(ri);
        double cr = ahp.calcularCR(ci, ri);
        problema.getAlternativesWeight().get(indiceMapa).setCr(cr);
        problema.getAlternativesWeight().get(indiceMapa).setNormalizedMatrix(ahp.calcularMatrizNormalizada(ponderacionCriterios));
        problema.getAlternativesWeight().get(indiceMapa).setPriorityVector(ahp.obtenerVectorPrioridades(
                problema.getAlternativesWeight().get(indiceMapa).getNormalizedMatrix()));
    }

    public void componerResultados() {
        int indice = 0;
        //Armar la matriz de resultados globales
        for (AHPmatriz matrizAlternativas : problema.getAlternativesWeight().values()) {
            Matrix vectorPrioridades = matrizAlternativas.getPriorityVector();
            for (int f = 0; f < vectorPrioridades.getRowDimension(); f++) {
                problema.getResult().getMainMatrix().set(f, indice, vectorPrioridades.get(f, 0));
            }
            indice++;
        }

        //Vector de prioridades final
        Matrix vectorPrioridadesFinal = new Matrix(problema.getAlternativesWeight().get(1).getMainMatrix().getColumnDimension(), 1);
        for (int f = 0; f < problema.getResult().getMainMatrix().getRowDimension(); f++) {
            double suma = 0;
            for (int c = 0; c < problema.getResult().getMainMatrix().getColumnDimension(); c++) {
                suma += (problema.getResult().getMainMatrix().get(f, c) * problema.getCriteriaWeight().getPriorityVector().get(c, 0));
            }
            vectorPrioridadesFinal.set(f, 0, suma);
        }
        problema.getResult().setPriorityVector(vectorPrioridadesFinal);
        vistas.desplegarPanelResults();
    }

    public Matrix corregirInconsistenciaMatriz(Matrix unaMatriz, String metodo) {
        Matrix matrizCorregida = null;
        if (metodo.equalsIgnoreCase("Backpropagation Neural Network (Red Neuronal Artificial)")) {
            RedNeuronalBackpropagation metodoBPN = new RedNeuronalBackpropagation();
            matrizCorregida = metodoBPN.generarMatrizConsistente(unaMatriz);
        }
        if (metodo.equalsIgnoreCase("Support Vector Machines (Maquina de Vectores de Soporte)")) {
            SupportVectorMachine metodoSVM = new SupportVectorMachine();
            matrizCorregida = metodoSVM.generarMatrizConsistente(unaMatriz);
        }
        return matrizCorregida;
    }

    public void crearHistorico(Matrix unaMatriz, double cr, int tipoMatriz, int metodoUsado, int criterio) {
        AHPhistorial unHistorial = problema.getUnHistorial();
        AHPmatriz AHPmatrizHistorica = new AHPmatriz(unaMatriz);
        AHPmatrizHistorica.setCr(cr);
        if (unHistorial == null) {
            unHistorial = new AHPhistorial(problema.getId());
            AHPmatrizHistorica.setFechaCreacion(Calendar.getInstance());
        }
        AHPmatrizHistorica.setFechaUltimaModificacion(Calendar.getInstance());
        AHPmatrizHistorica.setTipoMatriz(tipoMatriz);
        AHPmatrizHistorica.setMetodoMLusado(metodoUsado);
        switch (tipoMatriz) {
            case 1:
                unHistorial.setCriteriaWeight(AHPmatrizHistorica);
                break;
            case 2:
                if (unHistorial.getAlternativesWeight() == null) {
                    HashMap<Integer, AHPmatriz> alternativeMap = new HashMap<>();
                    unHistorial.setAlternativesWeight(alternativeMap);
                }
                unHistorial.getAlternativesWeight().put(criterio, AHPmatrizHistorica);
                break;
        }
        problema.setUnHistorial(unHistorial);
    }

    public AHPproblema getProblema() {
        return problema;
    }

    public void setProblema(AHPproblema problema) {
        this.problema = problema;
    }

    public AHPcalculos getAhp() {
        return ahp;
    }

    public void setAhp(AHPcalculos ahp) {
        this.ahp = ahp;
    }

    public ConexionBD getDb() {
        return db;
    }

}
