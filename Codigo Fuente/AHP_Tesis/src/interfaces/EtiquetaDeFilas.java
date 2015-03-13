package interfaces;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/*
 *	Use a JTable as a renderer for row numbers of a given main table.
 *  This table must be added to the row header of the scrollpane that
 *  contains the main table.
 */
public class EtiquetaDeFilas extends JTable implements ChangeListener, PropertyChangeListener{
    
    private final JTable main;

    public EtiquetaDeFilas(JTable table, boolean isAlt){
            main = table;
            main.addPropertyChangeListener( this );
            setFocusable( false );
            setAutoCreateColumnsFromModel( false );
            setSelectionModel( main.getSelectionModel() );

            TableColumn column = new TableColumn();
            column.setHeaderValue(" ");
            addColumn( column );
            column.setCellRenderer(new RowNumberRenderer(isAlt));
            getColumnModel().getColumn(0).setPreferredWidth(40);
            setPreferredScrollableViewportSize(getPreferredSize());
    }

    @Override
    public void addNotify(){
        super.addNotify();
        Component c = getParent();
        //  Keep scrolling of the row table in sync with the main table.
        if (c instanceof JViewport){
            JViewport viewport = (JViewport)c;
            viewport.addChangeListener( this );
        }
    }

    /*
     *  Delegate method to main table
     */
    @Override
    public int getRowCount(){
        return main.getRowCount();
    }

    @Override
    public int getRowHeight(int row){
        int rowHeight = main.getRowHeight(row);
        if (rowHeight != super.getRowHeight(row)){
                super.setRowHeight(row, rowHeight);
        }
        return rowHeight;
    }

    /*
     *  No model is being used for this table so just use the row number
     *  as the value of the cell.
     */
    @Override
    public Object getValueAt(int row, int column){
        return Integer.toString(row + 1);
    }

    /*
     *  Don't edit data in the main TableModel by mistake
     */
    @Override
    public boolean isCellEditable(int row, int column){
        return false;
    }

    /*
     *  Do nothing since the table ignores the model
     */
    @Override
    public void setValueAt(Object value, int row, int column) {}
//
//  Implement the ChangeListener
//
    @Override
    public void stateChanged(ChangeEvent e){
        //  Keep the scrolling of the row table in sync with main table
        JViewport viewport = (JViewport) e.getSource();
        JScrollPane scrollPane = (JScrollPane)viewport.getParent();
        scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
    }
//
//  Implement the PropertyChangeListener
//
    @Override
    public void propertyChange(PropertyChangeEvent e){
        //  Keep the row table in sync with the main table
        if ("selectionModel".equals(e.getPropertyName())){
            setSelectionModel( main.getSelectionModel() );
        }
        if ("rowHeight".equals(e.getPropertyName())){
            repaint();
        }
    }

    /*
     *  Attempt to mimic the table header renderer
     */
    private static class RowNumberRenderer extends DefaultTableCellRenderer
    {
        private boolean isAlternatives;
        
        public RowNumberRenderer(boolean isAlt){
            isAlternatives = isAlt;
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            if (table != null){
                JTableHeader header = table.getTableHeader();
                if (header != null){
                    //setForeground(header.getForeground());
                    //setBackground(header.getBackground());
                    //setFont(header.getFont());
                }
            }
            
            /*
            if (isSelected){
                setFont( getFont().deriveFont(Font.BOLD) );
            }
            */
            
            String newValue = "";
            if(isAlternatives){
                String headers[] = { "A","B","C","D","E" };
                int index = Integer.valueOf(value.toString())-1;
                newValue = headers[index];
            }else{
                newValue = value.toString();
            }
            setText((value == null) ? "" : newValue);
            //setBorder(UIManager.getBorder("TableHeader.cellBorder"));

            return this;
        }
    }
}
