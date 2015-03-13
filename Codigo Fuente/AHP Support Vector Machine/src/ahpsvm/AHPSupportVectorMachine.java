/**
 * Maquina de Soporte de Vectores para procesar matrices del Proceso Analitico
 * Jerarquico
 * @author Federico Matias Rodriguez y Marianela Labat
 */
package ahpsvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import static org.encog.ml.svm.KernelType.RadialBasisFunction;
import org.encog.ml.svm.SVM;
import static org.encog.ml.svm.SVMType.EpsilonSupportVectorRegression;
import org.encog.ml.svm.training.SVMSearchTrain;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Stopwatch;

public class AHPSupportVectorMachine {

    static final CalculosAHP ahp = new CalculosAHP();
    static final ConexionBD con = new ConexionBD();
    static final DecimalFormat df = new DecimalFormat("#.#########");
    static int ORDEN;
    static final double UMBRAL_ERROR = 0.01;
    static final double GAMMA_BEGIN = 1.0;
    static final double GAMMA_STEP = 1.0;
    static final double GAMMA_END = 100.0;
    static final double CONST_BEGIN = 1.0;
    static final double CONST_STEP = 1.0;
    static final double CONST_END = 100.0;

    static Log svmLog;

    public static void main(String[] args) {

        
        String input = null;
        boolean continuar = false;
        do{
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Ingrese ORDEN de Matriz para entrenar [3-7]: ");
                input = br.readLine();
                if(input.matches("[3-7]")){
                    continuar = true;
                }else{
                    System.out.println("Por favor ingrese un dígito del 3 al 7.");
                }
            } catch (IOException ex) {
                System.out.println("Se produjo un error de entrada/salida: "+ex.getMessage());
            }
        }while(!continuar);
        ORDEN = Integer.valueOf(input);
        /* ConexionBD, es la clase que maneja la conexion con la Base de Datos */
        /* para recuperar las matrices que se usarán para el entrenamiento     */
        con.Connection();
        String timeLog = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        svmLog = new Log("########################################## " + timeLog + " ##########################################");
        svmLog.agregarLinea("Support Vector Machine - Matrices de orden " + ORDEN);
        /* Entrada, elementos de la matriz inconsistente */
        double AHP_INPUT[][], AHP_TRAINING_INPUT[][], AHP_TEST_INPUT[][], AHP_VALIDATION_INPUT[][];
        AHP_INPUT = con.obtenerElementosMatrizAHP(ORDEN, "matrices_inconsistentes");
        con.normalizarArreglo(ORDEN, AHP_INPUT);

        /* Salida deseada, elementos de la matriz correspondiente consistente */
        double AHP_IDEAL[][], AHP_TRAINING_IDEAL[][], AHP_TEST_IDEAL[][], AHP_VALIDATION_IDEAL[][];
        AHP_IDEAL = con.obtenerElementosMatrizAHP(ORDEN, "matrices_consistentes");
        con.normalizarArreglo(ORDEN, AHP_IDEAL);

        /* Separar los datos para Entrenamiento*/
        AHP_TRAINING_INPUT = con.dividirSetElementos(AHP_INPUT, ORDEN, 0, 699, 700);
        AHP_TRAINING_IDEAL = con.dividirSetElementos(AHP_IDEAL, ORDEN, 0, 699, 700);
        svmLog.agregarLinea(" ELEMENTOS DE ENTRENAMIENTO = " + AHP_TRAINING_INPUT.length);

        /* Separar los datos para Validacion*/
        AHP_VALIDATION_INPUT = con.dividirSetElementos(AHP_INPUT, ORDEN, 594, 699, 105);
        AHP_VALIDATION_IDEAL = con.dividirSetElementos(AHP_IDEAL, ORDEN, 594, 699, 105);
        svmLog.agregarLinea(" ELEMENTOS DE VALIDACION = " + AHP_VALIDATION_INPUT.length);

        /* Separar los datos para Prueba*/
        AHP_TEST_INPUT = con.dividirSetElementos(AHP_INPUT, ORDEN, 700, 999, 300);
        AHP_TEST_IDEAL = con.dividirSetElementos(AHP_IDEAL, ORDEN, 700, 999, 300);
        svmLog.agregarLinea(" ELEMENTOS DE TEST = " + AHP_TEST_INPUT.length);

        /* Como regresión en SVM tiene una sola salida tenemos que entrenar
         multiples SVM, una por cada elemento de la matriz triangular superior de AHP
         excluyendo la diagonal principal, por ej. en una matriz de orden 3 son 3 elementos*/
        int inputCount = (ORDEN * (ORDEN - 1)) / 2;
        HashMap<Integer, SVM> multiSVMmap = new HashMap<>();
        Stopwatch temporizadorEntrenamiento = new Stopwatch();
        temporizadorEntrenamiento.start();
        for (int i = 0; i < inputCount; i++) {
            /* Support Vector Machine */
            /* Tipo de SVM = Epsilon Support Vector Regression */
            /* Tipo de Kernel = Radial Basis Function */
            /* SVM svm = (SVM)EncogDirectoryPersistence.loadObject(new File("src/ahpsupportvectormachine/persistencia/orden-"+orden+"-SVM-"+i+".eg")); */
            SVM svm = new SVM(inputCount, EpsilonSupportVectorRegression, RadialBasisFunction);
            /* Crear los datos de entrenamiento */
            BasicMLDataSet setDeEntrenamiento = new BasicMLDataSet(AHP_TRAINING_INPUT, con.extraerColumna(ORDEN, i, AHP_TRAINING_IDEAL));
            /* Entrenar la SVM - SVMSearchTrain es una clase que realiza un entrenamiento de SVM
             mucho mas profundo y preciso que la clase SVMTrain */
            SVMSearchTrain entrenamiento = new SVMSearchTrain(svm, setDeEntrenamiento);

            entrenamiento.setGammaBegin(GAMMA_BEGIN);
            entrenamiento.setGammaStep(GAMMA_STEP);
            entrenamiento.setGammaEnd(GAMMA_END);
            entrenamiento.setConstBegin(CONST_BEGIN);
            entrenamiento.setConstStep(CONST_STEP);
            entrenamiento.setConstEnd(CONST_END);

            svmLog.agregarLinea("------------------------------------------------------------------------------------------");
            svmLog.agregarLinea(" @ SVM Nro. " + (i + 1));
            svmLog.agregarLinea("   * TIPO: Epsilon Support Vector Regression");
            svmLog.agregarLinea("   * KERNEL: Radial Basis Function");
            svmLog.agregarLinea("   * CANTIDAD DE ENTRADAS: " + inputCount);
            svmLog.agregarLinea("   > GAMMA BEGIN : " + entrenamiento.getGammaBegin());
            svmLog.agregarLinea("   > GAMMA STEP  : " + entrenamiento.getGammaStep());
            svmLog.agregarLinea("   > GAMMA END   : " + entrenamiento.getGammaEnd());
            svmLog.agregarLinea("   > CONST BEGIN : " + entrenamiento.getConstBegin());
            svmLog.agregarLinea("   > CONST STEP  : " + entrenamiento.getConstStep());
            svmLog.agregarLinea("   > CONST END   : " + entrenamiento.getConstEnd());
            svmLog.agregarLinea("------------------------------------------------------------------------------------------");
            svmLog.agregarLinea("ENTRENAMIENTO " + (i + 1));
            svmLog.agregarLinea("------------------------------------------------------------------------------------------");
            /* Entrenamiento de SVM 1 */
            int iteracion = 1;
            do {
                entrenamiento.iteration();
                String logEntrenamiento = "SVM " + i + " >>> Iteracion #" + iteracion + "; Error: " + entrenamiento.getError()
                        + " Gamma: " + svm.getParams().gamma + ", C: " + svm.getParams().C + ", Epsilon: " + svm.getParams().eps;
                System.out.println(logEntrenamiento
                        + " Gamma: " + svm.getParams().gamma
                        + " C: " + svm.getParams().C
                        + " Epsilon: " + svm.getParams().eps);
                svmLog.agregarLinea(logEntrenamiento);
                iteracion++;
            } while (entrenamiento.getError() > UMBRAL_ERROR);
            svmLog.agregarLinea("------------------------------------------------------------------------------------------");
            svmLog.agregarLinea("   GAMMA BEST: " + entrenamiento.getBestGamma() + " - CONST BEST: " + entrenamiento.getBestConst());
            svmLog.agregarLinea("   TOTAL ITERACIONES: " + iteracion + " - UMBRAL DE ERROR: " + UMBRAL_ERROR);
            svmLog.agregarLinea("------------------------------------------------------------------------------------------");
            multiSVMmap.put(i, svm);
            //Persistir
            EncogDirectoryPersistence.saveObject(new File("src/ahpsvm/svm/orden-" + ORDEN + "-SVM-" + (i + 1) + ".eg"), svm);
        }
        temporizadorEntrenamiento.stop();
        double tiempoSeg = temporizadorEntrenamiento.getElapsedMilliseconds() / 1000.0;
        String totalTiempo = "Tiempo de Entrenamiento: " + tiempoSeg + " segundos";
        System.out.println(totalTiempo);
        svmLog.agregarLinea(totalTiempo);
        /* Validacion */
        evaluacion("VALIDACION", AHP_VALIDATION_INPUT, AHP_VALIDATION_IDEAL, ORDEN, inputCount, multiSVMmap);
        /* Test */
        evaluacion("TEST", AHP_TEST_INPUT, AHP_TEST_IDEAL, ORDEN, inputCount, multiSVMmap);
        svmLog.cerrarLog();
        Encog.getInstance().shutdown();

    }

    public static void evaluacion(String tipo, double[][] INPUT, double[][] IDEAL, int orden, int inputCount, HashMap<Integer, SVM> SVMmap) {
        /* Validacion de la Maquina de Soporte Vectorial */
        Stopwatch temporizadorEvaluacion = new Stopwatch();
        temporizadorEvaluacion.start();
        svmLog.agregarLinea("################################### " + tipo + " ###################################");
        // Estructura de datos de Prueba
        BasicMLDataSet setEvaluacion = new BasicMLDataSet(INPUT, IDEAL);
        int aciertos = 0;
        int elemento = 1;
        double crMin = 99, crMax = 0, crSum = 0, crProm = 0;
        for (MLDataPair parDeDatos : setEvaluacion) {
            String elementoString = String.format("%03d", elemento);
            String fila = elementoString + " # Entrada: [ ";
            for (int i = 0; i < parDeDatos.getInput().getData().length; i++) {
                fila += con.desnormalizar(parDeDatos.getInput().getData(i)) + " ";
            }
            fila += "] - Ideal: [ ";
            for (int i = 0; i < parDeDatos.getIdeal().getData().length; i++) {
                fila += con.desnormalizar(parDeDatos.getIdeal().getData(i)) + " ";
            }
            /* Calcular los resultados para cada elemento segun la SVM correspondiente */
            /* y componer la salida de los distintos SVM en un solo vector SALIDA[] */
            double SALIDA[] = new double[inputCount];
            for (int i = 0; i < inputCount; i++) {
                final MLData salida = SVMmap.get(i).compute(parDeDatos.getInput());
                if (salida.getData(0) < 0) {
                    salida.setData(0, 0);
                }
                SALIDA[i] = con.desnormalizar(ahp.redondear(salida.getData(0), 9));
            }
            fila += "] - Actual: [ ";
            for (int i = 0; i < SALIDA.length; i++) {
                fila += SALIDA[i] + " ";

            }
            /* Calcular el CR para la matriz */
            double cr = ahp.redondear(ahp.imprimirCR(orden, SALIDA), 9);
            fila += " ] CR = " + cr;
            if (cr < 0.1) {
                aciertos++;
                if (cr > crMax) {
                    crMax = cr;
                }
                if (cr < crMin) {
                    crMin = cr;
                }
            }

            crSum += cr;
            svmLog.agregarLinea(fila);
            elemento++;
        }
        temporizadorEvaluacion.stop();
        crProm = ahp.redondear(crSum / (Double.valueOf(elemento)), 9);
        double precision = (Double.valueOf(aciertos) * 100.0) / Double.valueOf(INPUT.length);
        double tiempoSeg = temporizadorEvaluacion.getElapsedMilliseconds() / 1000.0;
        svmLog.agregarLinea("-----------------------------------------------------------------------------------");
        svmLog.agregarLinea("Aciertos = " + aciertos + " (" + precision + "%) - Tiempo: " + tiempoSeg + " segundos\n");
        svmLog.agregarLinea("################################# FIN " + tipo + " #################################");
        svmLog.agregarLinea("CR Maximo: " + crMax + " CR Minimo: " + crMin + " CR Promedio: " + crProm);
    }

}
