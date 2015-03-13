/**
 * Red Neuronal para procesar matrices del Proceso Analitico Jerarquico
 *
 * @author Federico Matias Rodriguez y Marianela Labat
 */
package ahpbpn;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Stopwatch;

public class AHPNeuralNetwork {

    static int ORDEN; //Elegir el orden de la matriz a procesar
    static int NEURONAS_ENTRADA;
    static int NEURONAS_OCULTAS = 80;
    static int NEURONAS_SALIDA;
    static final double LEARNING_RATE = 0.0001;
    static final double MOMENTUM = 0.001;
    static final double UMBRAL_ERROR = 0.01;
    static final ConexionBD con = new ConexionBD();
    static final CalculosAHP ahp = new CalculosAHP();
    static final DecimalFormat df = new DecimalFormat("#.#########");
    static Log bpnLog;

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
        NEURONAS_ENTRADA = (ORDEN * (ORDEN - 1)) / 2;
        NEURONAS_OCULTAS = 80;
        NEURONAS_SALIDA = (ORDEN * (ORDEN - 1)) / 2;
        //ConexionBD nos sirve para traer las matrices que vamos a procesar        
        con.Connection();
        String timeLog = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        bpnLog = new Log("########################################## " + timeLog + " ##########################################");
        bpnLog.agregarLinea("Multilayer Perceptron Backpropagation - Matrices de orden " + ORDEN);
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
        bpnLog.agregarLinea(" ELEMENTOS DE ENTRENAMIENTO = " + AHP_TRAINING_INPUT.length);
        /* Separar los datos para Validacion*/
        AHP_VALIDATION_INPUT = con.dividirSetElementos(AHP_INPUT, ORDEN, 594, 699, 105);
        AHP_VALIDATION_IDEAL = con.dividirSetElementos(AHP_IDEAL, ORDEN, 594, 699, 105);
        bpnLog.agregarLinea(" ELEMENTOS DE VALIDACION = " + AHP_VALIDATION_INPUT.length);
        /* Separar los datos para Prueba*/
        AHP_TEST_INPUT = con.dividirSetElementos(AHP_INPUT, ORDEN, 700, 999, 300);
        AHP_TEST_IDEAL = con.dividirSetElementos(AHP_IDEAL, ORDEN, 700, 999, 300);
        bpnLog.agregarLinea(" ELEMENTOS DE TEST = " + AHP_TEST_INPUT.length);
        // Crear una red neuronal con Encog sin usar Factory
        BasicNetwork redNeuronal = new BasicNetwork();
        /* Capa de Entrada */
        redNeuronal.addLayer(new BasicLayer(null, true, NEURONAS_ENTRADA));
        /* Capa Oculta */
        redNeuronal.addLayer(new BasicLayer(new ActivationSigmoid(), true, NEURONAS_OCULTAS));
        /* Capa de Salida */
        redNeuronal.addLayer(new BasicLayer(new ActivationSigmoid(), false, NEURONAS_SALIDA));
        redNeuronal.getStructure().finalizeStructure();
        redNeuronal.reset();
        new ConsistentRandomizer(-1, 1, 500).randomize(redNeuronal);
        // Crear datos de entrenamiento
        MLDataSet setEntrenamiento = new BasicMLDataSet(AHP_TRAINING_INPUT, AHP_TRAINING_IDEAL);
        /* Entrenar la red neuronal con Backpropagation*/
        /* Parametros: La red, el set de entrenamiento, el learning rate y el momentum */
        Backpropagation entrenamiento = new Backpropagation(redNeuronal, setEntrenamiento, LEARNING_RATE, MOMENTUM);
        
        bpnLog.agregarLinea("------------------------------------------------------------------------------------------");
        bpnLog.agregarLinea(" @ PERCEPTRON MULTICAPA ");
        bpnLog.agregarLinea("   * UMBRAL DE ERROR : " + UMBRAL_ERROR);
        bpnLog.agregarLinea("   * NEURONAS DE ENTRADA : " + NEURONAS_ENTRADA);
        bpnLog.agregarLinea("   * NEURONAS OCULTAS    : " + NEURONAS_OCULTAS);
        bpnLog.agregarLinea("   * NEURONAS DE SALIDA  : " + NEURONAS_SALIDA);
        bpnLog.agregarLinea("   * FACTOR DE APRENDIZAJE : " + LEARNING_RATE);
        bpnLog.agregarLinea("   * MOMENTUM : " + MOMENTUM);
        bpnLog.agregarLinea("------------------------------------------------------------------------------------------");
        
        Stopwatch temporizadorEntrenamiento = new Stopwatch();
        temporizadorEntrenamiento.start();
        int epoch = 1;
        double errorAnterior = 0;
        do {
            entrenamiento.iteration();
            if (errorAnterior != 0 && entrenamiento.getError() > errorAnterior) {
                System.out.println("OSCILO EN ITERACION #" + epoch + " - ERROR: " + entrenamiento.getError());
                System.exit(0);
            }
            errorAnterior = entrenamiento.getError();
            if (epoch == 1 || epoch % 1000 == 0) {
                System.out.println("Iteracion #" + epoch + " Error:" + entrenamiento.getError());
                String logEntrenamiento = "   Iteracion #" + epoch + " Error:" + entrenamiento.getError();
                bpnLog.agregarLinea(logEntrenamiento);
            }
            epoch++;
        } while (entrenamiento.getError() > UMBRAL_ERROR);
        String logEntrenamiento = "Ultima Iteracion #" + epoch + " Error:" + entrenamiento.getError();
        bpnLog.agregarLinea(logEntrenamiento);
        entrenamiento.finishTraining();
        temporizadorEntrenamiento.stop();
        double tiempoSeg = ahp.redondear(temporizadorEntrenamiento.getElapsedMilliseconds() / 1000.0, 2);
        String totalTiempo = "Tiempo de Entrenamiento: " + tiempoSeg + " segundos";
        System.out.println(totalTiempo);
        bpnLog.agregarLinea("------------------------------------------------------------------------------------------");
        bpnLog.agregarLinea("   TOTAL ITERACIONES: " + epoch + " " + totalTiempo);
        bpnLog.agregarLinea("------------------------------------------------------------------------------------------");
        //Persistir
        EncogDirectoryPersistence.saveObject(new File("src/ahpbpn/bpn/bpn-orden-" + ORDEN + ".eg"), redNeuronal);
        /* Validacion */
        evaluacion("VALIDACION", AHP_VALIDATION_INPUT, AHP_VALIDATION_IDEAL, ORDEN, NEURONAS_ENTRADA, redNeuronal);
        /* Test */
        evaluacion("TEST", AHP_TEST_INPUT, AHP_TEST_IDEAL, ORDEN, NEURONAS_ENTRADA, redNeuronal);
        bpnLog.cerrarLog();
        Encog.getInstance().shutdown();
    }

    public static void evaluacion(String tipo, double[][] INPUT, double[][] IDEAL, int orden, int inputCount, BasicNetwork redNeuronal) {
        /* Validacion del Perceptron Multicapa con Backpropagation */
        Stopwatch temporizadorEvaluacion = new Stopwatch();
        temporizadorEvaluacion.start();
        bpnLog.agregarLinea("################################### " + tipo + " ###################################");
        // Estructura de datos de Prueba
        BasicMLDataSet setEvaluacion = new BasicMLDataSet(INPUT, IDEAL);
        int aciertos = 0;
        int elemento = 1;
        double crMax = 0, crMin = 99, crProm = 0, crSum = 0;
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
            fila += "] - Actual: [ ";
            double[] matrizParcial = new double[inputCount];
            for (int i = 0; i < inputCount; i++) {
                double valorReal = con.desnormalizar(redNeuronal.compute(parDeDatos.getInput()).getData(i));
                matrizParcial[i] = valorReal;
                System.out.print(valorReal + " ");
                fila += matrizParcial[i] + " ";
            }
            /* Calcular el CR para la matriz */
            double cr = ahp.redondear(ahp.imprimirCR(orden, matrizParcial), 9);
            fila += " ] CR = " + cr;
            crSum += cr;
            if (cr < 0.1) {
                aciertos++;
                if (cr > crMax) {
                    crMax = cr;
                }
                if (cr < crMin) {
                    crMin = cr;
                }
            }
            bpnLog.agregarLinea(fila);
            elemento++;
        }
        temporizadorEvaluacion.stop();
        crProm = ahp.redondear(crSum / Double.valueOf(elemento), 9);
        double precision = ahp.redondear((Double.valueOf(aciertos) * 100.0) / Double.valueOf(INPUT.length), 2);
        double tiempoSeg = ahp.redondear(temporizadorEvaluacion.getElapsedMilliseconds() / 1000.0, 2);
        bpnLog.agregarLinea("-----------------------------------------------------------------------------------");
        bpnLog.agregarLinea("Aciertos = " + aciertos + " (" + precision + "%) - Tiempo: " + tiempoSeg + " segundos\n");
        bpnLog.agregarLinea("CR Max = " + crMax + " - CR Min: " + crMin + " - CR Prom: " + crProm + "\n");
        bpnLog.agregarLinea("################################# FIN " + tipo + " #################################");
    }

}
