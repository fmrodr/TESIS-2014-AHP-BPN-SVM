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

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import static edu.umbc.cs.maple.utils.JamaUtils.colsum;
import static edu.umbc.cs.maple.utils.JamaUtils.rowsum;

public class AHPcalculos {

    public static int SAATY_SCALE[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};

    public Matrix calcularMatrizNormalizada(Matrix unaMatriz) {
        //NORMALIZAR LA MATRIZ
        Matrix sumaColumnasMatriz = colsum(unaMatriz);
        Matrix matrizNormalizada = new Matrix(unaMatriz.getColumnDimension(),
                unaMatriz.getRowDimension());
        double valorNormalizado;
        for (int c = 0; c < unaMatriz.getColumnDimension(); c++) {
            for (int f = 0; f < unaMatriz.getRowDimension(); f++) {
                valorNormalizado = (unaMatriz.get(f, c) / sumaColumnasMatriz.get(0, c));
                matrizNormalizada.set(f, c, valorNormalizado);
            }
        }
        return matrizNormalizada;
    }

    public Matrix obtenerVectorPrioridades(Matrix matrizNormalizada) {
        //CONSTRUIR EL VECTOR DE PRIORIDADES
        Matrix vectorPrioridades = rowsum(matrizNormalizada);
        for (int f = 0; f < vectorPrioridades.getRowDimension(); f++) {
            double sumaPrioridadesCelda = (vectorPrioridades.get(f, 0) / matrizNormalizada.getColumnDimension());
            vectorPrioridades.set(f, 0, sumaPrioridadesCelda);
        }
        return vectorPrioridades;
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
        double mayorEigenvalor = this.calcularLambdaMax(unaMatriz);
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

    public void imprimirMatriz(Matrix unaMatriz) {
        for (int f = 0; f < unaMatriz.getRowDimension(); f++) {
            System.out.print("[");
            for (int c = 0; c < unaMatriz.getColumnDimension(); c++) {
                System.out.print(" " + unaMatriz.get(f, c) + " ");
            }
            System.out.print("]\n");
        }
    }

    public double obtenerValorEscalaSaaty(int index) {
        if (index == 1) {
            return 0.111111111;
        } else if (index == 2) {
            return 0.125;
        } else if (index == 3) {
            return 0.142857143;
        } else if (index == 4) {
            return 0.166666667;
        } else if (index == 5) {
            return 0.2;
        } else if (index == 6) {
            return 0.25;
        } else if (index == 7) {
            return 0.333333333;
        } else if (index == 8) {
            return 0.5;
        } else if (index == 9) {
            return 1.0;
        } else if (index == 10) {
            return 2.0;
        } else if (index == 11) {
            return 3.0;
        } else if (index == 12) {
            return 4.0;
        } else if (index == 13) {
            return 5.0;
        } else if (index == 14) {
            return 6.0;
        } else if (index == 15) {
            return 7.0;
        } else if (index == 16) {
            return 8.0;
        } else if (index == 17) {
            return 9.0;
        }
        return 0;
    }

    public int obtenerIndiceEscalaSaaty(double valor) {
        if (valor == 0.111111111) {
            return SAATY_SCALE[0];
        } else if (valor == 0.125) {
            return SAATY_SCALE[1];
        } else if (valor == 0.142857143) {
            return SAATY_SCALE[2];
        } else if (valor == 0.166666667) {
            return SAATY_SCALE[3];
        } else if (valor == 0.2) {
            return SAATY_SCALE[4];
        } else if (valor == 0.25) {
            return SAATY_SCALE[5];
        } else if (valor == 0.333333333) {
            return SAATY_SCALE[6];
        } else if (valor == 0.5) {
            return SAATY_SCALE[7];
        } else if (valor == 1.0) {
            return SAATY_SCALE[8];
        } else if (valor == 2.0) {
            return SAATY_SCALE[9];
        } else if (valor == 3.0) {
            return SAATY_SCALE[10];
        } else if (valor == 4.0) {
            return SAATY_SCALE[11];
        } else if (valor == 5.0) {
            return SAATY_SCALE[12];
        } else if (valor == 6.0) {
            return SAATY_SCALE[13];
        } else if (valor == 7.0) {
            return SAATY_SCALE[14];
        } else if (valor == 8.0) {
            return SAATY_SCALE[15];
        } else if (valor == 9.0) {
            return SAATY_SCALE[16];
        }
        return 0;
    }

    public Matrix armarMatrizDesdeVector(int orden, double[] matrizParcial) {
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

}
