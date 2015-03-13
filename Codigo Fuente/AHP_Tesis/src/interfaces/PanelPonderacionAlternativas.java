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
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.fraction.Fraction;

public final class PanelPonderacionAlternativas extends javax.swing.JPanel{
    
    private final Vistas vista;
    private JLabel jLabelSubTitulo;
    private JButton jButtonVolver;
    private JButton jButtonSiguiente;
    private JTabbedPane jTabbedPaneAlternativas;
    private DecimalFormat df;

    public PanelPonderacionAlternativas(Vistas aView) {
        this.vista = aView;
        initComponents();
    }
    
    public void initComponents(){
        setBackground( Color.WHITE );
        setLayout(new MigLayout());
        df = new DecimalFormat("#.####");
        jLabelSubTitulo = new javax.swing.JLabel();
        jLabelSubTitulo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelSubTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSubTitulo.setText("<html><p style='text-align: center'><strong>Matriz de Ponderaci&oacute;n de Alternativas en relaci&oacute;n a los Criterios</strong></p><br><p>&Eacute;sta matriz refleja las comparaciones de paridad entre alternativas segun su desempe&ntilde;o en cada criterio.<br>El tama&ntilde;o de la matriz es (n*n), siendo n la cantidad de alternativas.</p><br></html>");
        jButtonVolver = new javax.swing.JButton();
        jButtonVolver.setText("Volver");
        jButtonVolver.addActionListener((java.awt.event.ActionEvent evt) -> {
            int confirmed = JOptionPane.showConfirmDialog(null,
                      "<html><p>&iquest;Est&aacute; seguro de que quiere volver hacia atr&aacute;s?<br>Puede perder cambios realizados en la ponderaci&oacute;n de las alternativas.</p></html>", "Volver atr&aacute;s",
                 JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) vista.desplegarPanelCriteriaWeight();
        });
        jButtonSiguiente = new javax.swing.JButton();
        jButtonSiguiente.setText("Siguiente");
        jButtonSiguiente.addActionListener((java.awt.event.ActionEvent evt) -> {
            int values = 0;
            for(int i=1; i<=vista.useController().getProblema().getAlternativesWeight().size(); i++){                    
                if(vista.useController().getProblema().getAlternativesWeight().get(i).getCr() > -1){
                    values++;
                }
            }
            if(values == vista.useController().getProblema().getAlternativesWeight().size()){
                vista.useController().componerResultados();
            }else{
                vista.desplegarMensajeError("Existen matrices sin calcular el vector de prioridades.\nRecuerde hacer el calculo por cada alternativa para poder continuar.", "Error de Datos");
            }
        });
        jTabbedPaneAlternativas = new javax.swing.JTabbedPane();
        jTabbedPaneAlternativas.setBackground(new java.awt.Color(255, 255, 255));
        for(int tabla=1;
                tabla<=vista.useController().getProblema().getCriteriaWeight().getMainMatrix().getColumnDimension();
                tabla++){                       
            //CREO UNA TABLA EN BASE A UN CRITERIO P LAS ALTERNATIVAS           
            final JTable jTableAlternativas = new JTable();      
            JScrollPane scrollPane = new JScrollPane(jTableAlternativas);
            JTable rowTable = new EtiquetaDeFilas(jTableAlternativas, true);
            scrollPane.setRowHeaderView(rowTable);
            scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());           
            
            jTableAlternativas.setBorder(new LineBorder(Color.BLACK));       
            jTableAlternativas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jTableAlternativas.setCellSelectionEnabled(true);
            
            TableModelPersonalizado modelo = new TableModelPersonalizado(
                    vista.useController().getProblema().getAlternativas(), 
                    vista.useController().getProblema().getAlternativas()
            );       
            jTableAlternativas.setModel(modelo);
            //QUE NO SE PUEDA REORDENAR O CAMBIAR TAMAÑO
            jTableAlternativas.getTableHeader().setResizingAllowed(false);
            jTableAlternativas.getTableHeader().setReorderingAllowed(false);
            jTableAlternativas.setName(String.valueOf(tabla));
            //HEADERS DE LA TABLA            
            String headers[] = { "A","B","C","D","E" };
            for(int i=0;i<jTableAlternativas.getColumnCount();i++){
                jTableAlternativas.getColumnModel().getColumn(i).setHeaderValue(headers[i]);     
                jTableAlternativas.getColumnModel().getColumn(i).setPreferredWidth(40);              
            }            
            //CREO EL PANEL PARA COLOCAR LA TABLA
            JPanel panel = new JPanel();
            panel.setLayout(new MigLayout());    
            panel.add(scrollPane, "wrap, align center");            
            //EL BOTON
            JButton botonCalcular = new JButton("Calcular");
            final JLabel jLabelCR = new JLabel("");
            //LLAMAR AL PROCESO DE AGREGAR LOS JCOMBOBOX
            int indice = Integer.valueOf(jTableAlternativas.getName());
            if(vista.useController().getProblema().getAlternativesWeight().get(indice).getCr()<0){
                vista.colocarCombosTabla(this, jTableAlternativas, modelo, null);
                jLabelCR.setText("<html><br><p>Ratio de Consistencia (CR) = S/C</p><br></html>");
            }else{
                vista.colocarCombosTabla(this, jTableAlternativas, modelo, vista.useController().getProblema().getAlternativesWeight().get(indice).getMainMatrix());
                jLabelCR.setText("<html><br><p>Ratio de Consistencia (CR) = "+
                        df.format(vista.useController().getProblema().getAlternativesWeight().get(indice).getCr())+
                        "</p><br></html>");
            }
            botonCalcular.addActionListener((ActionEvent e) -> {
                //EVALUAR SI EXISTEN VALORES FALTANTES
                int criterio = Integer.valueOf(jTableAlternativas.getName());
                if(vista.revisarValoresFaltantesEnTabla(jTableAlternativas)){
                    vista.desplegarMensajeError("<html><p>Existen valores faltantes en la matriz.<br>No se puede continuar.</p></html>", "Error de Datos");
                }else{
                    Matrix matrizPonderacionAlternativas = vista.pasarValoresDeJTableAMatriz(jTableAlternativas);
                    vista.useController().resolverPonderacionAlternativa(criterio, matrizPonderacionAlternativas);
                    jLabelCR.setText("<html><br><p>Ratio de Consistencia (CR) = "+df.format(vista.useController().getProblema().getAlternativesWeight().get(criterio).getCr())+"</p><br></html>");
                    if(vista.useController().getProblema().getAlternativesWeight().get(criterio).getCr()>0.1){
                        jLabelCR.setForeground(Color.RED);
                        //Options for the combo box dialog
                        String[] metodo = {"Backpropagation Neural Network (Red Neuronal Artificial)", "Support Vector Machines (Maquina de Vectores de Soporte)"};
                        String picked = (String)JOptionPane.showInputDialog(this, "<html><p>El CR de la matriz es mayor o igual a 0.1, puede intentar corregirlo utilizando<br>algun metodo de Inteligencia Artificial listado a continuacion:"
                                , "Correccion de la Inconsistencia de la Matriz", JOptionPane.QUESTION_MESSAGE, null, metodo, metodo[0]);
                        if(picked!=null){
                            Matrix matrizNueva = vista.useController().corregirInconsistenciaMatriz(matrizPonderacionAlternativas, picked);
                            //Armado de las matrices AHP dentro de una tabla HTML
                            int columnas = matrizPonderacionAlternativas.getColumnDimension();
                            double columnSize = 100.0/(columnas*2.0);
                            String matrizHTML = "<table border='0' align='center'><tr><th bgcolor='#7A0000' color='#FFFFFF' colspan='"+columnas+"'>Matriz Original</th>"
                                    + "<th bgcolor='#3399FF' color='#FFFFFF' colspan='"+columnas+"'>Matriz Sugerida</th></tr>";
                            for(int f=0; f<matrizPonderacionAlternativas.getRowDimension();f++){
                                matrizHTML += "<tr>";
                                //Elementos de la matriz original
                                for(int c1=0; c1<matrizPonderacionAlternativas.getColumnDimension();c1++){
                                    Fraction ff = new Fraction(matrizPonderacionAlternativas.get(f, c1)); 
                                    matrizHTML += "<td width='"+columnSize+"%' bgcolor='#D7B2B2'>"+ff.toString().replaceAll(" ", "")+"</td>";
                                }
                                //Elementos de la matriz corregida
                                for(int c2=0; c2<matrizNueva.getColumnDimension();c2++){
                                    Fraction ff = new Fraction(matrizNueva.get(f, c2));
                                    matrizHTML += "<td width='"+columnSize+"%' bgcolor='#C2E0FF'>"+ff.toString().replaceAll(" ", "")+"</td>";
                                }
                                matrizHTML += "</tr>";
                            }
                            int sumaTotalColumnas = matrizPonderacionAlternativas.getColumnDimension()+matrizNueva.getColumnDimension();
                            double crOriginal = vista.useController().getAhp().calcularCR(vista.useController().getAhp().calcularCI(matrizPonderacionAlternativas), vista.useController().getAhp().calcularRI(matrizPonderacionAlternativas));
                            double ciNuevo = vista.useController().getAhp().calcularCI(matrizNueva);
                            double riNuevo = vista.useController().getAhp().calcularRI(matrizNueva);
                            double crNuevo = vista.useController().getAhp().calcularCR(ciNuevo, riNuevo);                           
                            matrizHTML += "<tr><td bgcolor='#7A0000' color='#FFFFFF' colspan='"+columnas+"'><strong>CR</strong> = "+df.format(crOriginal)+"</td><td bgcolor='#3399FF' color='#FFFFFF' colspan='"+columnas+"'><strong>CR</strong> = "+df.format(crNuevo)+"</td></tr>";
                            matrizHTML += "</table>";
                            if(crNuevo<0.1){
                                int confirmNewMatrix = JOptionPane.showConfirmDialog(null, "<html><p>Metodo elegido: "+picked+"</p><br>"+matrizHTML+"<br><p>&iquest;Desea corregir el CR de la matriz con éste método?</p></html>", "Matriz Sugerida", JOptionPane.OK_CANCEL_OPTION);
                                if(confirmNewMatrix == JOptionPane.OK_OPTION){  
                                    if(picked.equalsIgnoreCase("Backpropagation Neural Network (Red Neuronal Artificial)")){
                                        vista.useController().getProblema().getAlternativesWeight().get(criterio).setMetodoMLusado(1);
                                    }else{
                                        vista.useController().getProblema().getAlternativesWeight().get(criterio).setMetodoMLusado(2);
                                    }
                                    vista.useController().crearHistorico(matrizPonderacionAlternativas, crOriginal, 2, vista.useController().getProblema().getAlternativesWeight().get(criterio).getMetodoMLusado(), criterio);
                                    
                                    vista.useController().getProblema().getAlternativesWeight().get(criterio).setMainMatrix(matrizNueva);
                                    vista.useController().getProblema().getAlternativesWeight().get(criterio).setCi(ciNuevo);
                                    vista.useController().getProblema().getAlternativesWeight().get(criterio).setRi(riNuevo);
                                    vista.useController().getProblema().getAlternativesWeight().get(criterio).setCr(crNuevo);
                                    for(int f=0; f<matrizNueva.getRowDimension(); f++){
                                       for(int c=0; c<matrizNueva.getColumnDimension(); c++){
                                           Fraction fr = new Fraction(matrizNueva.get(f, c));
                                           String valor = "";
                                           if(fr.getDenominator()==1){
                                               valor = String.valueOf(fr.getNumerator());
                                           }else{
                                               valor = fr.toString().replace(" ", "");
                                           }                                       
                                           jTableAlternativas.setValueAt(valor, f, c);
                                       }   
                                    }
                                    jLabelCR.setText("<html><br><p>Ratio de Consistencia (CR) = "+df.format(vista.useController().getProblema().getAlternativesWeight().get(criterio).getCr())+"</p><br></html>");
                                    jLabelCR.setForeground(Color.BLACK);
                                }
                            }else{
                                vista.desplegarMensajeError("Disculpe, pero el metodo elegido no pudo corregir el CR de la Matriz.", "Error de Metodo");
                            }                           
                        }                     
                    }
                }
            });  
            int width = 40*jTableAlternativas.getColumnCount();
            int height = 16*jTableAlternativas.getRowCount();
            jTableAlternativas.setPreferredScrollableViewportSize(new Dimension(width, height));             
            panel.add(botonCalcular, "wrap, align center");
            panel.add(jLabelCR, "align right");
            //AGREGAR PANEL
            this.jTabbedPaneAlternativas.addTab("Criterio #"+tabla, panel);                    
        }
        
        add(jLabelSubTitulo, "wrap, align center");
        add(jTabbedPaneAlternativas, "wrap, align center");
        add(jButtonVolver, "split 2, align center");
        add(jButtonSiguiente, "align right");
    }
}
