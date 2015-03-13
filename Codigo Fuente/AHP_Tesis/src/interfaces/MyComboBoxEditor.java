/*
 * Software perteneciente al trabajo de T�sis:
 * Analisis Comparativo de Tecnicas de IA para la Correccion de la Inconsistencia
 * de la Matriz del Proceso Anal�tico Jerarquico
 * Autor: Federico Matias Rodriguez - rfedericomatias@gmail.com
 * Autor: Marianela Daianna Labat - daiannalabat@gmail.com
 * A�o: 2014
 * Universidad Gaston Dachary
 */

package interfaces;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class MyComboBoxEditor extends DefaultCellEditor {
    public MyComboBoxEditor(JComboBox comboBox) {
        super(comboBox);
    }
}
