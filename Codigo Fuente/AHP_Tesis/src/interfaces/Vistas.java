/*
* Software perteneciente al trabajo de Tésis:
* Analisis Comparativo de Tecnicas de IA para la Correccion de la Inconsistencia
* de la Matriz del Proceso Analítico Jerarquico
* Autor: Federico Matias Rodriguez - rfedericomatias@gmail.com
* Autor: Marianela Daianna Labat - daiannalabat@gmail.com
* Año: 2014
* Universidad Gaston Dachary
 */

package interfaces;

import Jama.Matrix;
import ahp.AHPcalculos;
import ahp.Controladora;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionFormat;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class Vistas {
    
    private Contenedor frameContenedor;
    private PanelParametrosIniciales panelInitialParameters;
    private final Controladora controller;
    private PanelPonderacionCriterios panelCriteriaWeight;
    private PanelPonderacionAlternativas panelAlternativeWeight;
    private PanelResultados panelResults;
    
    public Vistas(Controladora cont){
        controller = cont;
    }   
    
    public Controladora useController(){
        return controller;
    }
    
    public void empotrarPanel(JFrame unFrame, JPanel unPanel){
        unFrame.setContentPane(unPanel);
        unFrame.setVisible(true);
        unPanel.setVisible(true);        
        unFrame.pack();
        unFrame.setLocationRelativeTo(null);
        unFrame.setResizable(false);
    }
    
    public void desplegarFrameContenedor(){
        frameContenedor = new Contenedor(this);
        frameContenedor.setTitle("TESIS 2014 - Proceso Analitico Jerarquico optimizado con IA");
        frameContenedor.setResizable(false);
        frameContenedor.setSize(500, 400);       
        frameContenedor.setVisible(true);
        frameContenedor.setLocationRelativeTo(null);
    }
    
    public void desplegarPanelInitialParameters(){
        panelInitialParameters = new PanelParametrosIniciales(this);
        empotrarPanel(frameContenedor, panelInitialParameters);
    }
    
    public void desplegarPanelCriteriaWeight(){
        panelCriteriaWeight = new PanelPonderacionCriterios(this);
        empotrarPanel(frameContenedor, panelCriteriaWeight);
        frameContenedor.pack();
        frameContenedor.setLocationRelativeTo(null);
    }
    
    public void desplegarPanelAlternativeWeight(){
        panelAlternativeWeight = new PanelPonderacionAlternativas(this);
        empotrarPanel(frameContenedor, panelAlternativeWeight);
        frameContenedor.pack();
    }
    
    public void desplegarPanelResults(){
        panelResults = new PanelResultados(this);
        empotrarPanel(frameContenedor, panelResults);
    }
    
    public void salirSistema(){
        int confirmed = JOptionPane.showConfirmDialog(null,
                      "<html><p>&iquest;Seguro que desea salir del sistema?</p></html>", "Confirmacion de salida",
                 JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) System.exit(0);
    }
    
    public void mensajeInformativo(String titulo, String mensaje){
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void desplegarMensajeError(String mensaje, String titulo){
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
    
    public void colocarCombosTabla(JPanel container, final JTable tableCriterios, TableModelPersonalizado modelo, Matrix unaMatriz){
        AHPcalculos ahpCalc = new AHPcalculos();
        String saatyScale[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "1/2", "1/3","1/4","1/5","1/6","1/7", "1/8", "1/9" };
        //ARMAR EL COMBOBOX POR CADA CELDA
        for(int i=0; i<tableCriterios.getModel().getColumnCount(); i++){
            for(int j=0; j<tableCriterios.getModel().getRowCount(); j++){
                TableColumn ponderaciones = tableCriterios.getColumnModel().getColumn(i);
                
                JComboBox comboBox = new JComboBox(); 
                comboBox.setBackground(Color.red);
                for(String saatyValue : saatyScale){
                    comboBox.addItem(saatyValue);
                }                
                AutoCompleteDecorator.decorate(comboBox);
                comboBox.setSelectedItem("1");  
                comboBox.addActionListener(
                        (ActionEvent e) -> {
                            JComboBox combo = (JComboBox)e.getSource();
                    if ((String)combo.getSelectedItem() != null) {
                        String selectedItem = (String)combo.getSelectedItem();
                        if(!selectedItem.equalsIgnoreCase("")){
                            FractionFormat ff = new FractionFormat();
                            Fraction f = ff.parse(selectedItem);
                            String itemReciproco = f.reciprocal().toString().replace(" ", "");                      
                            int fila = tableCriterios.getSelectedRow();
                            int columna = tableCriterios.getSelectedColumn();
                            if(fila>-1 && columna>-1){
                                int filaReciproco = columna;
                                int columnaReciproco = fila;
                                tableCriterios.getModel().setValueAt(itemReciproco, filaReciproco, columnaReciproco);
                            } 
                        }                        
                    }
                });
                
                modelo.setCellEditable(i, j, true); 
                if(j<=i){
                   if(i==j) tableCriterios.getModel().setValueAt("1", i, j);
                   //DESHABILITAR EDICION DE LA CELDA EN CASO DE SER DIAGONAL
                   modelo.setCellEditable(i, j, false);                  
                }
                if(j==0 && i==1){
                    tableCriterios.changeSelection(j, i, false, false);
                    tableCriterios.requestFocus();
                }
                
                FractionFormat ff = new FractionFormat();
                if(unaMatriz!=null){
                    Fraction g = new Fraction(unaMatriz.get(j, i));
                    String valor = ""+g.getNumerator();
                    if(g.getDenominator()>1){
                        valor += "/"+g.getDenominator();
                    }
                    tableCriterios.getModel().setValueAt(valor, j, i);
                }
                
                ponderaciones.setCellEditor(new MyComboBoxEditor(comboBox));
                ponderaciones.setCellRenderer(new MyComboBoxRenderer(saatyScale));
            }
            
        }             
    }
    
    public boolean revisarValoresFaltantesEnTabla(JTable jTable){
        boolean valoresFaltantes = false;
        FractionFormat ff = new FractionFormat();
        DefaultTableModel dtm = (DefaultTableModel) jTable.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        for (int i = 0 ; i < nRow ; i++){
            for (int j = 0 ; j < nCol ; j++){
                if(dtm.getValueAt(i,j)!= null){
                    String fraccion = (String)dtm.getValueAt(i,j);
                    Fraction f = ff.parse(fraccion);
                }else{
                    valoresFaltantes = true;
                }         
            }
        }
        return valoresFaltantes;
    }
    
    public Matrix pasarValoresDeJTableAMatriz(JTable jTableCriterios){
        FractionFormat ff = new FractionFormat();
        DefaultTableModel dtm = (DefaultTableModel) jTableCriterios.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Matrix matrizPonderaciones = new Matrix(nRow, nCol);
        for (int i = 0 ; i < nRow ; i++){
            for (int j = 0 ; j < nCol ; j++){
                String fraccion = (String)dtm.getValueAt(i,j);
                Fraction f = ff.parse(fraccion);
                matrizPonderaciones.set(i, j, f.doubleValue());
            }
        }
        return matrizPonderaciones;
    }
}
