/*
 * Software perteneciente al trabajo de T�sis:
 * Analisis Comparativo de Tecnicas de IA para la Correccion de la Inconsistencia
 * de la Matriz del Proceso Anal�tico Jerarquico
 * Autor: Federico Matias Rodriguez - rfedericomatias@gmail.com
 * Autor: Marianela Daianna Labat - daiannalabat@gmail.com
 * A�o: 2014
 * Universidad Gaston Dachary
 */

package ahp;

import Jama.Matrix;
import edu.umbc.cs.maple.utils.JamaUtils;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.math3.fraction.FractionFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AHPcalculosTest {
    private final AHPcalculos ahpCalc;
    private AHPproblema ahpProblem;
    private final FractionFormat ff;
    private final DecimalFormat df;
    
    public AHPcalculosTest() {
        ff = new FractionFormat();
        ahpCalc = new AHPcalculos();
        df = new DecimalFormat("#.###");
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {     
        ahpProblem = new AHPproblema();
        //Matriz de ponderacion de criterios de prueba
        Matrix matrizCriterios = new Matrix(4,4);
        matrizCriterios.set(0, 0, 1.0);
        matrizCriterios.set(0, 1, ff.parse("1/3").doubleValue());
        matrizCriterios.set(0, 2, 5.0);
        matrizCriterios.set(0, 3, 1.0);
        matrizCriterios.set(1, 0, 3.0);
        matrizCriterios.set(1, 1, 1.0);
        matrizCriterios.set(1, 2, 5.0);
        matrizCriterios.set(1, 3, 1.0);
        matrizCriterios.set(2, 0, ff.parse("1/5").doubleValue());
        matrizCriterios.set(2, 1, ff.parse("1/5").doubleValue());
        matrizCriterios.set(2, 2, 1.0);
        matrizCriterios.set(2, 3, ff.parse("1/5").doubleValue());
        matrizCriterios.set(3, 0, 1.0);
        matrizCriterios.set(3, 1, 1.0);
        matrizCriterios.set(3, 2, 5.0);
        matrizCriterios.set(3, 3, 1.0);
        AHPmatriz ahpMatrix = new AHPmatriz(matrizCriterios);
        ahpProblem.setCriteriaWeight(ahpMatrix);
        
        HashMap<Integer, AHPmatriz> listaAlternativas = new HashMap<>();
        Matrix matrizAlternativa = new Matrix(3,3);
        matrizAlternativa.set(0, 0, 1);
        matrizAlternativa.set(0, 1, 5);
        matrizAlternativa.set(0, 2, 9);
        matrizAlternativa.set(1, 0, ff.parse("1/5").doubleValue());
        matrizAlternativa.set(1, 1, 1);
        matrizAlternativa.set(1, 2, 3);
        matrizAlternativa.set(2, 0, ff.parse("1/9").doubleValue());
        matrizAlternativa.set(2, 1, ff.parse("1/3").doubleValue());
        matrizAlternativa.set(2, 2, 1);
        ahpMatrix = new AHPmatriz(matrizAlternativa);
        listaAlternativas.put(1, ahpMatrix);        
        
        matrizAlternativa = new Matrix(3,3);
        matrizAlternativa.set(0, 0, 1);
        matrizAlternativa.set(0, 1, 1);
        matrizAlternativa.set(0, 2, 5);
        matrizAlternativa.set(1, 0, 1);
        matrizAlternativa.set(1, 1, 1);
        matrizAlternativa.set(1, 2, 3);
        matrizAlternativa.set(2, 0, ff.parse("1/5").doubleValue());
        matrizAlternativa.set(2, 1, ff.parse("1/3").doubleValue());
        matrizAlternativa.set(2, 2, 1);
        ahpMatrix = new AHPmatriz(matrizAlternativa);
        listaAlternativas.put(2, ahpMatrix);
        
        matrizAlternativa = new Matrix(3,3);
        matrizAlternativa.set(0, 0, 1);
        matrizAlternativa.set(0, 1, ff.parse("1/3").doubleValue());
        matrizAlternativa.set(0, 2, ff.parse("1/9").doubleValue());
        matrizAlternativa.set(1, 0, 3);
        matrizAlternativa.set(1, 1, 1);
        matrizAlternativa.set(1, 2, ff.parse("1/3").doubleValue());
        matrizAlternativa.set(2, 0, 9);
        matrizAlternativa.set(2, 1, 3);
        matrizAlternativa.set(2, 2, 1);
        ahpMatrix = new AHPmatriz(matrizAlternativa);
        listaAlternativas.put(3, ahpMatrix);
        
        matrizAlternativa = new Matrix(3,3);
        matrizAlternativa.set(0, 0, 1);
        matrizAlternativa.set(0, 1, ff.parse("1/9").doubleValue());
        matrizAlternativa.set(0, 2, ff.parse("1/5").doubleValue());
        matrizAlternativa.set(1, 0, 9);
        matrizAlternativa.set(1, 1, 1);
        matrizAlternativa.set(1, 2, 2);
        matrizAlternativa.set(2, 0, 5);
        matrizAlternativa.set(2, 1, ff.parse("1/2").doubleValue());
        matrizAlternativa.set(2, 2, 1);
        ahpMatrix = new AHPmatriz(matrizAlternativa);
        listaAlternativas.put(4, ahpMatrix);
        
        ahpProblem.setAlternativesWeight(listaAlternativas);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of obtenerEscalaSaaty method, of class AHPcalculos.
     */
    @Test
    public void testObtenerEscalaSaaty() {
        
    }

    /**
     * Test of calcularMatrizNormalizada method, of class AHPcalculos.
     */
    @Test
    public void testCalcularMatrizNormalizada() {
        
    }

    /**
     * Test of obtenerVectorPrioridades method, of class AHPcalculos.
     */
    @Test
    public void testObtenerVectorPrioridades() {
        System.out.println("obtenerVectorPrioridades");
        Matrix vectorPrioridadesVerdadero;
        vectorPrioridadesVerdadero = new Matrix(ahpProblem.getCriteriaWeight().getMainMatrix().getColumnDimension(), 1);
        vectorPrioridadesVerdadero.set(0, 0, 0.232);
        vectorPrioridadesVerdadero.set(1, 0, 0.402);
        vectorPrioridadesVerdadero.set(2, 0, 0.061);
        vectorPrioridadesVerdadero.set(3, 0, 0.305);
        Matrix matrizNormalizada = ahpCalc.calcularMatrizNormalizada(ahpProblem.getCriteriaWeight().getMainMatrix());
        Matrix vectorPrioridadesNuevo = ahpCalc.obtenerVectorPrioridades(matrizNormalizada);
        double ri = ahpCalc.calcularRI(ahpProblem.getCriteriaWeight().getMainMatrix());
        double ci = ahpCalc.calcularCI(ahpProblem.getCriteriaWeight().getMainMatrix());
        double cr = ahpCalc.calcularCR(ci, ri);
        //System.out.print("VP POSTA [");
        for(int i=0; i<vectorPrioridadesVerdadero.getRowDimension(); i++){
            //System.out.print(" "+vectorPrioridadesVerdadero.get(i, 0)+" ");
        }
        //System.out.print("] CR = 0.055\n");
        //System.out.print("VP NUEVO [");
        for(int i=0; i<vectorPrioridadesNuevo.getRowDimension(); i++){
            //System.out.print(" "+df.format(vectorPrioridadesNuevo.get(i, 0))+" ");
        }
        //System.out.print("] CR = "+df.format(cr)+"\n");
    }

    /**
     * Test of calcularLambdaMax method, of class AHPcalculos.
     */
    @Test
    public void testCalcularLambdaMax() {
        
    }

    /**
     * Test of calcularCI method, of class AHPcalculos.
     */
    @Test
    public void testCalcularCI() {
        
    }

    /**
     * Test of calcularRI method, of class AHPcalculos.
     */
    @Test
    public void testCalcularRI() {
        
    }

    /**
     * Test of calcularCR method, of class AHPcalculos.
     */
    @Test
    public void testCalcularCR() {
        
    }
    
    /**
     * Test of AHP Result.
     */
    @Test
    public void testAHPresult() {
        
        Matrix matrizNormalizada = ahpCalc.calcularMatrizNormalizada(ahpProblem.getCriteriaWeight().getMainMatrix());
        Matrix vectorPrioridadesNuevo = ahpCalc.obtenerVectorPrioridades(matrizNormalizada);
        double ri = ahpCalc.calcularRI(ahpProblem.getCriteriaWeight().getMainMatrix());
        double ci = ahpCalc.calcularCI(ahpProblem.getCriteriaWeight().getMainMatrix());
        double cr = ahpCalc.calcularCR(ci, ri);
        ahpProblem.getCriteriaWeight().setNormalizedMatrix(matrizNormalizada);
        ahpProblem.getCriteriaWeight().setPriorityVector(vectorPrioridadesNuevo);
        ahpProblem.getCriteriaWeight().setRi(ri);
        ahpProblem.getCriteriaWeight().setCi(ci);
        ahpProblem.getCriteriaWeight().setCr(cr);
        
        for(AHPmatriz matrizAlternativa: ahpProblem.getAlternativesWeight().values()){
            matrizNormalizada = ahpCalc.calcularMatrizNormalizada(matrizAlternativa.getMainMatrix());
            vectorPrioridadesNuevo = ahpCalc.obtenerVectorPrioridades(matrizNormalizada);
            ri = ahpCalc.calcularRI(matrizAlternativa.getMainMatrix());
            ci = ahpCalc.calcularCI(matrizAlternativa.getMainMatrix());
            cr = ahpCalc.calcularCR(ci, ri);
            matrizAlternativa.setNormalizedMatrix(matrizNormalizada);
            matrizAlternativa.setPriorityVector(vectorPrioridadesNuevo);
            matrizAlternativa.setRi(ri);
            matrizAlternativa.setCi(ci);
            matrizAlternativa.setCr(cr);
        }
        
        JamaUtils utilidades = new JamaUtils();
        
        AHPmatriz matrizResultados = new AHPmatriz(new Matrix(3,4));
        ahpProblem.setResult(matrizResultados);
        
        int indice = 0;
        for(AHPmatriz matrizAlternativas : ahpProblem.getAlternativesWeight().values()){
            Matrix vectorPrioridades = matrizAlternativas.getPriorityVector();
            //System.out.print("Criterio "+indice+" VP[");
            for(int f=0; f<vectorPrioridades.getRowDimension();f++){
                //System.out.print(" "+vectorPrioridades.get(f, 0)+" ");
                ahpProblem.getResult().getMainMatrix().set(f, indice, vectorPrioridades.get(f, 0));               
            }
            //System.out.print("]\n");
            indice++;
        }
        
        System.out.print("VPC [");
        for(int f=0;f<ahpProblem.getCriteriaWeight().getPriorityVector().getRowDimension();f++){
            System.out.print(" "+ahpProblem.getCriteriaWeight().getPriorityVector().get(f, 0)+" ");
        }
        System.out.print("]\n");
        
        
        Matrix vectorFinalPonderaciones = new  Matrix(ahpProblem.getAlternativesWeight().get(0).getMainMatrix().getColumnDimension(), 1);
        for(int f=0;f<ahpProblem.getResult().getMainMatrix().getRowDimension();f++){
            System.out.print("[");
            double suma = 0;
            for(int c=0;c<ahpProblem.getResult().getMainMatrix().getColumnDimension();c++){
                System.out.print(" "+ahpProblem.getResult().getMainMatrix().get(f, c)+" ");
                suma += (ahpProblem.getResult().getMainMatrix().get(f, c)*ahpProblem.getCriteriaWeight().getPriorityVector().get(c, 0));
            }
            System.out.print("]\n");
            vectorFinalPonderaciones.set(f, 0, suma);
        }
        
        ahpProblem.getResult().setPriorityVector(vectorFinalPonderaciones);
        
        System.out.print("VPF [");
        for(int f=0;f<ahpProblem.getResult().getPriorityVector().getRowDimension();f++){
            System.out.print(" "+ahpProblem.getResult().getPriorityVector().get(f, 0)+" ");
        }
        System.out.print("]\n");             
        
    }
    
}
