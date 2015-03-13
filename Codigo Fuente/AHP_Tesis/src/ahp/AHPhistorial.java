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

import java.util.HashMap;

public class AHPhistorial {

    private int id;
    private AHPmatriz criteriaWeight;
    private HashMap<Integer, AHPmatriz> alternativesWeight;

    public AHPhistorial(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

}
