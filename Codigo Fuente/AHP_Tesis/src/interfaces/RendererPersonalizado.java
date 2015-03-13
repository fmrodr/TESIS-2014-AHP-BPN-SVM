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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RendererPersonalizado  extends DefaultTableCellRenderer {
    
    Color backgroundColor = getBackground();

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);        
        if(column<=row){
            c.setBackground(Color.LIGHT_GRAY);
        }else{
            c.setBackground(Color.WHITE);
        }
        return c;
    }    
}
