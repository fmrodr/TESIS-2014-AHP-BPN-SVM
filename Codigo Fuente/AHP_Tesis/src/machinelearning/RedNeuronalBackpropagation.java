/*
 * Software perteneciente al trabajo de T�sis:
 * Analisis Comparativo de Tecnicas de IA para la Correccion de la Inconsistencia
 * de la Matriz del Proceso Anal�tico Jerarquico
 * Autor: Federico Matias Rodriguez - rfedericomatias@gmail.com
 * Autor: Marianela Daianna Labat - daiannalabat@gmail.com
 * A�o: 2014
 * Universidad Gaston Dachary
 */

package machinelearning;

import Jama.Matrix;
import ahp.AHPcalculos;
import java.io.File;
import org.encog.Encog;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

public class RedNeuronalBackpropagation {
    
    private final NormalizedField norm;
    private final AHPcalculos ahp;
    
    public RedNeuronalBackpropagation(){
        norm = new NormalizedField(NormalizationAction.Normalize, null,17,1,1,0);
        ahp = new AHPcalculos();
    }
    
    public Matrix generarMatrizConsistente(Matrix matrizOriginal){
        int orden = matrizOriginal.getRowDimension();
        int elementos = (orden*(orden-1))/2;
        /*Levantar Red Neuronal ya entrenada*/
        BasicNetwork redNeuronal = (BasicNetwork)EncogDirectoryPersistence.loadObject(
                new File("src/machinelearning/bpn/bpn-orden-"+orden+".eg"));
        double[] matrizParcial = transformarMatrizEnVector(matrizOriginal);
        /*Normalizar*/
        for(int i=0;i<matrizParcial.length;i++){
            matrizParcial[i] = normalizar(matrizParcial[i]);
        }
        BasicMLData parDeDatos = new BasicMLData(matrizParcial); 
        /*Calcular*/
        double[] matrizSalida = new double[elementos];
        for(int i=0; i<elementos; i++){
                matrizSalida[i] = redNeuronal.compute(parDeDatos).getData(i);
        }
        Encog.getInstance().shutdown(); 
        /*Desnormalizar*/
        for(int i=0;i<matrizSalida.length;i++){
            matrizSalida[i] = desnormalizar(matrizSalida[i]);            
        }
        Matrix matrizConsistente = ahp.armarMatrizDesdeVector(orden, matrizSalida);
        return matrizConsistente;
    }
    
    public double[] transformarMatrizEnVector(Matrix matrizOriginal){
        int orden = matrizOriginal.getRowDimension();
        int elementos = (orden*(orden-1))/2;
        double[] matrizParcial = new double[elementos];
        int index = 0;
        for(int f=0; f<orden; f++){
            for(int c=0; c<orden; c++){ 
                /* La siguiente condición permite recorrer solamente la diagonal superior de la matriz */
                if(c>=f){
                    /* La siguiente condicion se usa para setear la diagional principal con valores iguales a 1 */
                    if(f!=c){
                        matrizParcial[index] = matrizOriginal.get(f, c);
                        index++;
                    }
                }
            }
        }
        return matrizParcial;
    }
    
    public double normalizar(double datoSinNormalizar){
        int indice = ahp.obtenerIndiceEscalaSaaty(datoSinNormalizar);
        double valorNormalizado = norm.normalize(indice);
        return valorNormalizado;
    }
    
    public double desnormalizar(double datoNormalizado){   	
    	int escalaSaaty = (int)norm.deNormalize(datoNormalizado);
    	double valorDesnormalizado = ahp.obtenerValorEscalaSaaty(escalaSaaty);       
        return valorDesnormalizado;
    }
    
}
