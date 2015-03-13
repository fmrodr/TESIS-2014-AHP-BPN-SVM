/**
 * Red Neuronal para procesar matrices del Proceso Analitico Jerarquico
 *
 * @author Federico Matias Rodriguez y Marianela Labat
 */
package ahpbpn;

import java.math.BigDecimal;
import java.math.RoundingMode;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class CalculosAHP {

    public Matrix armarMatrizAHP(int orden, double[] matrizParcial) {
        Matrix matrizAHP = new Matrix(orden, orden);
        int elemento = 0;
        for (int f = 0; f < orden; f++) {
            for (int c = 0; c < orden; c++) {
                /* La siguiente condición permite recorrer solamente la diagonal superior de la matriz */
                if (c >= f) {
                    /* La siguiente condicion se usa para setear la diagional principal con valores iguales a 1 */
                    if (f == c) {
                        matrizAHP.set(f, c, 1);
                    } else {
                        matrizAHP.set(f, c, matrizParcial[elemento]);
                        matrizAHP.set(c, f, Math.pow(matrizParcial[elemento], -1));
                        elemento++;
                    }
                }
            }
        }
        return matrizAHP;
    }

    public double calcularLambdaMax(Matrix unaMatriz) {
        double lambdaMax = 0;
        //UTILIZANDO LIBRERIA MATEMATICA JAMA
        EigenvalueDecomposition eigenvalores = unaMatriz.eig();
        for (int i = 0; i < eigenvalores.getRealEigenvalues().length; i++) {
            if (lambdaMax == 0) {
                lambdaMax = eigenvalores.getRealEigenvalues()[i];
            } else if (lambdaMax < eigenvalores.getRealEigenvalues()[i]) {
                lambdaMax = eigenvalores.getRealEigenvalues()[i];
            }
        }
        return lambdaMax;
    }

    public double calcularCI(Matrix unaMatriz) {
        double orden = unaMatriz.getColumnDimension();
        double mayorEigenvalor = calcularLambdaMax(unaMatriz);
        double ci = (mayorEigenvalor - orden) / (orden - 1);
        return ci;
    }

    public double calcularRI(Matrix unaMatriz) {
        //RI
        final double randomIndex[][] = {
            {2, 3, 4, 5, 6, 7, 8, 9, 10},
            {0, 0.58, 0.9, 1.12, 1.24, 1.32, 1.41, 1.45, 1.51}
        };
        //ENCONTRAR RI
        double ri = 0;
        for (int i = 0; i < 9; i++) {
            if (unaMatriz.getColumnDimension() == randomIndex[0][i]) {
                ri = randomIndex[1][i];
            }
        }
        return ri;
    }

    public double calcularCR(double ci, double ri) {
        double cr = (ci / ri);
        return cr;
    }

    public double imprimirCR(int orden, double[] matrizParcial) {
        Matrix unaMatrizAHP = armarMatrizAHP(orden, matrizParcial);
        double ci = calcularCI(unaMatrizAHP);
        double ri = calcularRI(unaMatrizAHP);
        double cr = calcularCR(ci, ri);
        return cr;
    }

    public double redondear(double valor, int lugares) {
        BigDecimal bd = new BigDecimal(valor);
        bd = bd.setScale(lugares, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
