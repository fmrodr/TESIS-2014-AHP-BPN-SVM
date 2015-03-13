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

import Jama.Matrix;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.fraction.Fraction;

public class PanelPonderacionCriterios extends javax.swing.JPanel{
    
    private final Vistas vista;
    private JButton jButtonSiguiente;
    private JButton jButtonCalcular;
    private JLabel jLabelCR;
    private JLabel jLabelSubTitulo;
    private TableModelPersonalizado modelo;
    private JScrollPane jScrollPaneTablaCriterios;
    private JTable jTableCriterios;
    private DecimalFormat df;
    
    public PanelPonderacionCriterios(Vistas vista) {
        this.vista = vista;
        initComponents();
    }  
    
    private void initComponents() {
        setBackground( Color.WHITE );
        setLayout(new MigLayout());
        df = new DecimalFormat("#.####");
        jLabelSubTitulo = new javax.swing.JLabel();
        jLabelSubTitulo.setFont(new java.awt.Font("Arial", 0, 12));
        jLabelSubTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSubTitulo.setText("<html><p style='text-align: center'><strong>Matriz Inicial de Criterios</strong></p><br><p>Matriz inicial que refleja las comparaciones de paridad entre criterios.<br>El tama&ntilde;o de la matriz es (n*n), siendo n la cantidad de criterios.</p><br></html>");
        jButtonSiguiente = new javax.swing.JButton();
        jButtonSiguiente.setText("Siguiente");
        jButtonSiguiente.addActionListener((java.awt.event.ActionEvent evt) -> {                
            //EVALUAR SI EXISTEN VALORES FALTANTES
            if(vista.useController().getProblema().getCriteriaWeight().getCr()<0){
                vista.desplegarMensajeError("<html><p>Disculpe, no ha realizado los calculos correspondientes.<br>Presione el boton \"Calcular\" debajo de la matriz para continuar.</p></html>", "No se puede continuar");
            }else{
                vista.desplegarPanelAlternativeWeight();
            }           
        });                     
        jTableCriterios = new JTable();      
        jScrollPaneTablaCriterios = new JScrollPane();
        jScrollPaneTablaCriterios.setViewportView(jTableCriterios);
        JTable rowTable = new EtiquetaDeFilas(jTableCriterios, false);
        rowTable.setEnabled(false);
        jScrollPaneTablaCriterios.setRowHeaderView(rowTable);
        jScrollPaneTablaCriterios.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader()); 
        //jTableCriterios.setPreferredScrollableViewportSize(jTableCriterios.getPreferredSize());
        
        //FORMATO DE TABLA
        modelo = new TableModelPersonalizado(
                vista.useController().getProblema().getCriteriaWeight().getMainMatrix().getRowDimension(), 
                vista.useController().getProblema().getCriteriaWeight().getMainMatrix().getColumnDimension()
        );       
        jTableCriterios.setModel(modelo);
        jTableCriterios.getTableHeader().setReorderingAllowed(false);    
        jTableCriterios.getTableHeader().setResizingAllowed(false);
        //HEADERS DE LA TABLA
        for(int i=0;i<jTableCriterios.getColumnCount();i++){
             jTableCriterios.getColumnModel().getColumn(i).setHeaderValue(""+(i+1));
             jTableCriterios.getColumnModel().getColumn(i).setPreferredWidth(40); 
        }
        jTableCriterios.setBorder(new LineBorder(Color.BLACK));       
        jTableCriterios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCriterios.setCellSelectionEnabled(true);       
        jLabelCR = new javax.swing.JLabel();
        //REVISAR SI EXISTE UNA MATRIZ YA PONDERADA ANTERIORMENTE
        if(vista.useController().getProblema().getCriteriaWeight().getCr()<0){
            vista.colocarCombosTabla(this, jTableCriterios, modelo, null);
            jLabelCR.setText("<html><br><p>Ratio de Consistencia (CR) = S/C</p><br></html>");
        }else{
            vista.colocarCombosTabla(this, jTableCriterios, modelo, vista.useController().getProblema().getCriteriaWeight().getMainMatrix());
            jLabelCR.setText("<html><br><p>Ratio de Consistencia (CR) = "+
                    df.format(vista.useController().getProblema().getCriteriaWeight().getCr())+
                    "</p><br></html>");
        }
        
        jButtonCalcular = new javax.swing.JButton();
        jButtonCalcular.setText("Calcular");
        jButtonCalcular.addActionListener((java.awt.event.ActionEvent evt) -> {
            //EVALUAR SI EXISTEN VALORES FALTANTES
            if(vista.revisarValoresFaltantesEnTabla(jTableCriterios)){
                vista.desplegarMensajeError("<html><p>Existen valores faltantes en la matriz.<br>No se puede continuar.</p></html>", "Error de Datos");
            }else{
                Matrix matrizPonderacionCriterios = vista.pasarValoresDeJTableAMatriz(jTableCriterios);
                vista.useController().resolverPonderacionCriterios(matrizPonderacionCriterios);
                jLabelCR.setText("<html><br><p>Ratio de Consistencia (CR) = "+df.format(vista.useController().getProblema().getCriteriaWeight().getCr())+"</p><br></html>");
                if(vista.useController().getProblema().getCriteriaWeight().getCr()>0.1){
                    jLabelCR.setForeground(Color.RED);
                    //Options for the combo box dialog
                    String[] metodo = {"Backpropagation Neural Network (Red Neuronal Artificial)", "Support Vector Machines (Maquina de Vectores de Soporte)"};
                    String picked = (String)JOptionPane.showInputDialog(this, "<html><p>El CR de la matriz es mayor o igual a 0.1, puede intentar corregirlo utilizando<br>algun metodo de Inteligencia Artificial listado a continuacion:"
                            , "Correccion de la Inconsistencia de la Matriz", JOptionPane.QUESTION_MESSAGE, null, metodo, metodo[0]);
                    if(picked!=null){
                        Matrix matrizNueva = vista.useController().corregirInconsistenciaMatriz(matrizPonderacionCriterios, picked);
                        //Armado de las matrices AHP dentro de una tabla HTML
                        int columnas = matrizPonderacionCriterios.getColumnDimension();
                        double columnSize = 100.0/(columnas*2.0);
                        String matrizHTML = "<table border='0' align='center'><tr><th bgcolor='#7A0000' color='#FFFFFF' colspan='"+columnas+"'>Matriz Original</th>"
                                + "<th bgcolor='#3399FF' color='#FFFFFF' colspan='"+columnas+"'>Matriz Sugerida</th></tr>";
                        for(int f=0; f<matrizPonderacionCriterios.getRowDimension();f++){
                            matrizHTML += "<tr>";
                            //Elementos de la matriz original
                            for(int c1=0; c1<matrizPonderacionCriterios.getColumnDimension();c1++){
                                Fraction ff = new Fraction(matrizPonderacionCriterios.get(f, c1)); 
                                matrizHTML += "<td width='"+columnSize+"%' bgcolor='#D7B2B2'>"+ff.toString().replaceAll(" ", "")+"</td>";
                            }
                            //Elementos de la matriz corregida
                            for(int c2=0; c2<matrizNueva.getColumnDimension();c2++){
                                Fraction ff = new Fraction(matrizNueva.get(f, c2));
                                matrizHTML += "<td width='"+columnSize+"%' bgcolor='#C2E0FF'>"+ff.toString().replaceAll(" ", "")+"</td>";
                            }
                            matrizHTML += "</tr>";
                        }
                        int sumaTotalColumnas = matrizPonderacionCriterios.getColumnDimension()+matrizNueva.getColumnDimension();
                        double crOriginal = vista.useController().getAhp().calcularCR(vista.useController().getAhp().calcularCI(matrizPonderacionCriterios), vista.useController().getAhp().calcularRI(matrizPonderacionCriterios));
                        double ciNuevo = vista.useController().getAhp().calcularCI(matrizNueva);
                        double riNuevo = vista.useController().getAhp().calcularRI(matrizNueva);
                        double crNuevo = vista.useController().getAhp().calcularCR(ciNuevo, riNuevo);
                        matrizHTML += "<tr><td bgcolor='#7A0000' color='#FFFFFF' colspan='"+columnas+"'><strong>CR</strong> = "+df.format(crOriginal)+"</td><td bgcolor='#3399FF' color='#FFFFFF' colspan='"+columnas+"'><strong>CR</strong> = "+df.format(crNuevo)+"</td></tr>";
                        matrizHTML += "</table>";
                        if(crNuevo<0.1){
                            int confirmNewMatrix = JOptionPane.showConfirmDialog(null, "<html><p>Metodo elegido: "+picked+"</p><br>"+matrizHTML+"<br><p>&iquest;Desea corregir el CR de la matriz con éste método?</p></html>", "Matriz Sugerida", JOptionPane.OK_CANCEL_OPTION);
                            if(confirmNewMatrix == JOptionPane.OK_OPTION){
                                if(picked.equalsIgnoreCase("Backpropagation Neural Network (Red Neuronal Artificial)")){
                                    vista.useController().getProblema().getCriteriaWeight().setMetodoMLusado(1);
                                }else{
                                    vista.useController().getProblema().getCriteriaWeight().setMetodoMLusado(2);
                                }
                                vista.useController().crearHistorico(matrizPonderacionCriterios, crOriginal, 1, vista.useController().getProblema().getCriteriaWeight().getMetodoMLusado(), 0);
                                vista.useController().getProblema().getCriteriaWeight().setMainMatrix(matrizNueva);
                                vista.useController().getProblema().getCriteriaWeight().setCi(ciNuevo);
                                vista.useController().getProblema().getCriteriaWeight().setRi(riNuevo);
                                vista.useController().getProblema().getCriteriaWeight().setCr(crNuevo);                               
                                vista.desplegarPanelCriteriaWeight();
                            }else{
                                vista.desplegarMensajeError("Disculpe, pero el metodo elegido no pudo corregir el CR de la Matriz.", "Error de Metodo");
                            } 
                        }
                        
                    }                   
                }
            }
        }); 
        int width = 45*jTableCriterios.getColumnCount();
        int height = 16*jTableCriterios.getRowCount();
        jTableCriterios.setPreferredScrollableViewportSize(new Dimension(width, height));
        add(jLabelSubTitulo, "wrap, align center");
        add(jScrollPaneTablaCriterios, "wrap, align center");
        add(jLabelCR, "wrap, align right");
        add(jButtonCalcular, "split 2, align center");
        add(jButtonSiguiente, "align right");
    }
    
    
    
}
