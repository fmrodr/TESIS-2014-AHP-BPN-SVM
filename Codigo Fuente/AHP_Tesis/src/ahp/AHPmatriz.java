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
import java.util.Calendar;

public class AHPmatriz {

    private Matrix mainMatrix;
    private Matrix normalizedMatrix;
    private Matrix priorityVector;
    private double lambdaMax;
    private double ci;
    private double ri;
    private double cr;
    private Calendar fechaCreacion;
    private Calendar fechaUltimaModificacion;
    private int tipoMatriz;
    private int metodoMLusado;

    public AHPmatriz(Matrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    public Matrix getMainMatrix() {
        return mainMatrix;
    }

    public void setMainMatrix(Matrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    public Matrix getPriorityVector() {
        return priorityVector;
    }

    public void setPriorityVector(Matrix priorityVector) {
        this.priorityVector = priorityVector;
    }

    public double getLambdaMax() {
        return lambdaMax;
    }

    public void setLambdaMax(double lambdaMax) {
        this.lambdaMax = lambdaMax;
    }

    public double getCi() {
        return ci;
    }

    public void setCi(double ci) {
        this.ci = ci;
    }

    public double getRi() {
        return ri;
    }

    public void setRi(double ri) {
        this.ri = ri;
    }

    public double getCr() {
        return cr;
    }

    public void setCr(double cr) {
        this.cr = cr;
    }

    public Matrix getNormalizedMatrix() {
        return normalizedMatrix;
    }

    public void setNormalizedMatrix(Matrix normalizedMatrix) {
        this.normalizedMatrix = normalizedMatrix;
    }

    public Calendar getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Calendar fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Calendar getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(Calendar fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public int getTipoMatriz() {
        return tipoMatriz;
    }

    public void setTipoMatriz(int tipoMatriz) {
        this.tipoMatriz = tipoMatriz;
    }

    public int getMetodoMLusado() {
        return metodoMLusado;
    }

    public void setMetodoMLusado(int metodoMLusado) {
        this.metodoMLusado = metodoMLusado;
    }

}
