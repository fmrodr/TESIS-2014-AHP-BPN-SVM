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
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public final class PanelResultados extends javax.swing.JPanel{
    
    private final Vistas views;
    private DecimalFormat df;
    private JLabel jLabelCriterios;
    private JLabel jLabelAlternativas;
    private JTextField jTextFieldNroCriterios;
    private JTextField jTextFieldNroAlternativas;
    private JScrollPane jScrollPaneVectorPrioridadesCriterios;
    private JTable jTableVectorPrioridadesCriterios;
    private JScrollPane jScrollPaneMatrizPrioridadesAlternativas;
    private JTable jTableMatrizPrioridadesAlternativas;
    private JLabel jLabelVectorDePrioridades;
    private JLabel jLabelMatrizPrioridadesAlternativas;
    private JLabel jLabelVectorResultados;
    private JScrollPane jScrollPaneVectorResultados;
    private JTable jTableVectorResultados;
    private JPanel jPanelResumenResultados;
    
    public PanelResultados(Vistas aView){
        views = aView;
        initComponents();
    }   
    
    public void initComponents(){
        setBackground( Color.WHITE );
        setLayout(new MigLayout());
        df = new DecimalFormat("#.####");
        
        jPanelResumenResultados = new javax.swing.JPanel(new MigLayout());
        jPanelResumenResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen de los Resultados"));
        
        jLabelCriterios = new javax.swing.JLabel();
        jLabelCriterios.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCriterios.setText("Criterios:");
        jTextFieldNroCriterios = new javax.swing.JTextField();
        jTextFieldNroCriterios.setEditable(false);
        jTextFieldNroCriterios.setText(String.valueOf(views.useController().getProblema().getCriterios()));
        
        jLabelAlternativas = new javax.swing.JLabel();
        jLabelAlternativas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelAlternativas.setText("Alternativas:");       
        jTextFieldNroAlternativas = new javax.swing.JTextField();
        jTextFieldNroAlternativas.setEditable(false);
        jTextFieldNroAlternativas.setText(String.valueOf(views.useController().getProblema().getAlternativas()));
        
        jLabelVectorDePrioridades = new javax.swing.JLabel();
        jLabelVectorDePrioridades.setText("Vector de prioridades de los criterios");
        jScrollPaneVectorPrioridadesCriterios= new javax.swing.JScrollPane();
        jTableVectorPrioridadesCriterios = new javax.swing.JTable();
        jScrollPaneVectorPrioridadesCriterios.setViewportView(jTableVectorPrioridadesCriterios);
        
        jLabelMatrizPrioridadesAlternativas = new javax.swing.JLabel();
        jLabelMatrizPrioridadesAlternativas.setText("Matriz de Prioridades de Alternativas segun Criterios");
        jScrollPaneMatrizPrioridadesAlternativas = new javax.swing.JScrollPane();
        jTableMatrizPrioridadesAlternativas = new javax.swing.JTable();
        jScrollPaneMatrizPrioridadesAlternativas.setViewportView(jTableMatrizPrioridadesAlternativas);
        
        jLabelVectorResultados = new javax.swing.JLabel();
        jLabelVectorResultados.setText("Resultados");
        jScrollPaneVectorResultados = new javax.swing.JScrollPane();
        jTableVectorResultados = new javax.swing.JTable();
        jScrollPaneVectorResultados.setViewportView(jTableVectorResultados);
                
        add(jPanelResumenResultados);
        jPanelResumenResultados.add(jLabelCriterios, "split 5, align left");
        jPanelResumenResultados.add(jTextFieldNroCriterios, "width 30");
        jPanelResumenResultados.add(jLabelAlternativas);
        jPanelResumenResultados.add(jTextFieldNroAlternativas,"wrap, width 30");
        jPanelResumenResultados.add(jLabelVectorDePrioridades, "wrap, align center");
        jPanelResumenResultados.add(jScrollPaneVectorPrioridadesCriterios, "wrap, align center, width 415, height 45");
        jPanelResumenResultados.add(jLabelMatrizPrioridadesAlternativas, "align center");
        jPanelResumenResultados.add(jLabelVectorResultados, "wrap, split 2, align center");
        jPanelResumenResultados.add(jScrollPaneMatrizPrioridadesAlternativas, "align center, width 350, height 148");       
        jPanelResumenResultados.add(jScrollPaneVectorResultados, "wrap, align center, width 54, height 148");
        setValuesJTableMatrizPrioridadesAlternativas();
        setValuesJTableVectorPrioridadesCriterios();
        setValuesJTableResultados();
    }
    
    public void setValuesJTableMatrizPrioridadesAlternativas(){
        //CONSTRUCCION DE LA TABLA
        int alternativas = views.useController().getProblema().getAlternativas();
        int criterios = views.useController().getProblema().getCriterios();
        TableModelPersonalizado modelo = new TableModelPersonalizado(alternativas, criterios);
        jTableMatrizPrioridadesAlternativas.setModel(modelo);
        jTableMatrizPrioridadesAlternativas.setEnabled(false);
        jTableMatrizPrioridadesAlternativas.getTableHeader().setResizingAllowed(false);
        jTableMatrizPrioridadesAlternativas.getTableHeader().setReorderingAllowed(false);
        //HEADERS DE LA TABLA
        for(int i=0;i<jTableMatrizPrioridadesAlternativas.getColumnCount();i++){
            jTableMatrizPrioridadesAlternativas.getColumnModel().getColumn(i).setHeaderValue("Crit #"+(i+1));         
        }
        //CONTENIDO DE LA TABLA
        for(int f = 0; f < alternativas; f++){
           for(int c = 0; c < criterios; c++){             
               modelo.setValueAt(df.format(views.useController().getProblema().getResult().getMainMatrix().get(f, c)), f, c);
           } 
        }
    }
    
    public void setValuesJTableVectorPrioridadesCriterios(){
        //CONSTRUCCION DE LA TABLA
        int criterios = views.useController().getProblema().getCriterios();
        TableModelPersonalizado modelo = new TableModelPersonalizado(1, criterios);
        jTableVectorPrioridadesCriterios.setModel(modelo);
        jTableVectorPrioridadesCriterios.setEnabled(false);
        jTableVectorPrioridadesCriterios.getTableHeader().setResizingAllowed(false);
        jTableVectorPrioridadesCriterios.getTableHeader().setReorderingAllowed(false);
        //HEADERS DE LA TABLA
        for(int i=0;i<jTableVectorPrioridadesCriterios.getColumnCount();i++){
            jTableVectorPrioridadesCriterios.getColumnModel().getColumn(i).setHeaderValue("Crit #"+(i+1));           
        }
        //CONTENIDO DE LA TABLA
        for(int c = 0; c < criterios; c++){
           modelo.setValueAt(df.format(views.useController().getProblema().getCriteriaWeight().getPriorityVector().get(c, 0)), 0, c); 
        }
    }
    
    public void setValuesJTableResultados(){
        //CONSTRUCCION DE LA TABLA
        int alternativas = views.useController().getProblema().getAlternativas();
        TableModelPersonalizado modelo = new TableModelPersonalizado(alternativas, 1);
        jTableVectorResultados.setModel(modelo);
        jTableVectorResultados.setEnabled(false);
        jTableVectorResultados.getTableHeader().setResizingAllowed(false);
        jTableVectorResultados.getTableHeader().setReorderingAllowed(false);
        //HEADERS DE LA TABLA
        jTableVectorResultados.getColumnModel().getColumn(0).setHeaderValue(" ");           
        //CONTENIDO DE LA TABLA
        for(int f = 0; f < alternativas; f++){
           modelo.setValueAt(df.format(views.useController().getProblema().getResult().getPriorityVector().get(f, 0)), f, 0);
        } 
    }
    
}
