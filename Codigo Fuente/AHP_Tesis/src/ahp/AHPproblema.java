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

import java.util.Calendar;
import java.util.HashMap;

public class AHPproblema {

    private int id;
    private int criterios;
    private int alternativas;
    private Calendar fechaCreacion;
    private AHPmatriz criteriaWeight;
    private HashMap<Integer, AHPmatriz> alternativesWeight;
    private AHPmatriz result;
    private AHPhistorial unHistorial;

    public AHPproblema() {
    }

    public AHPmatriz getCriteriaWeight() {
        return criteriaWeight;
    }

    public void setCriteriaWeight(AHPmatriz criteriaWeight) {
        this.criteriaWeight = criteriaWeight;
    }

    public HashMap<Integer, AHPmatriz> getAlternativesWeight() {
        return alternativesWeight;
    }

    public void setAlternativesWeight(HashMap<Integer, AHPmatriz> alternativesWeight) {
        this.alternativesWeight = alternativesWeight;
    }

    public AHPmatriz getResult() {
        return result;
    }

    public void setResult(AHPmatriz result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Calendar fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public AHPhistorial getUnHistorial() {
        return unHistorial;
    }

    public void setUnHistorial(AHPhistorial unHistorial) {
        this.unHistorial = unHistorial;
    }

    public int getCriterios() {
        return criterios;
    }

    public void setCriterios(int criterios) {
        this.criterios = criterios;
    }

    public int getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(int alternativas) {
        this.alternativas = alternativas;
    }

}
