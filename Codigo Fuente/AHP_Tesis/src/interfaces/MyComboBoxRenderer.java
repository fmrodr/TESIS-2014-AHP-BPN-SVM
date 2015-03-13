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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionFormat;

public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
    public MyComboBoxRenderer(String[] items) {
        super(items);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);
            //setForeground(table.getSelectionForeground());
            //super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);
            //setForeground(table.getForeground());
            //setBackground(table.getBackground());
        }       
        setSelectedItem(value);
        return this;
    }
}
