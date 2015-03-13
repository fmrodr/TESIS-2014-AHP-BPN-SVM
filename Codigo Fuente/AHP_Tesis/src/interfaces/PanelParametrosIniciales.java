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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class PanelParametrosIniciales  extends javax.swing.JPanel{
    
    private final Vistas vista;
    private JPanel jPanelParametros;
    private JComboBox jComboBoxAlternativas;
    private JComboBox jComboBoxCriterios;
    private JLabel jLabelObjetivo;
    private JLabel jLabelAlternativas;
    private JLabel jLabelCriterios;
    private JTextField jTextFieldObjetivo;
    private JButton jButtonSiguiente;
    private JButton jButtonSalir;
    private JButton jButtonContinuar;

    public PanelParametrosIniciales(Vistas vista) {
        this.vista = vista;
        initComponents();
    }      
    
    private void initComponents() {
        setLayout(new MigLayout());       
        setBackground( Color.WHITE );
        
        jPanelParametros = new javax.swing.JPanel(new MigLayout());
        jPanelParametros.setBorder(javax.swing.BorderFactory.createTitledBorder("Parámetros Iniciales"));
        jPanelParametros.setBackground( Color.WHITE );
        jComboBoxCriterios = new javax.swing.JComboBox();
        jComboBoxCriterios.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3", "4", "5", "6", "7", "8" }));
        jComboBoxAlternativas = new javax.swing.JComboBox();
        jComboBoxAlternativas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3", "4", "5" }));        
        jLabelCriterios = new javax.swing.JLabel();
        jLabelCriterios.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCriterios.setText("Ingrese la cantidad de criterios:");
        jLabelAlternativas = new javax.swing.JLabel();
        jLabelAlternativas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelAlternativas.setText("Ingrese la cantidad de alternativas:");        
        jButtonContinuar = new javax.swing.JButton();
        jButtonContinuar.setText("Continuar");
        jButtonContinuar.addActionListener((java.awt.event.ActionEvent evt) -> {
            int criterios;
            criterios = Integer.valueOf((String)jComboBoxCriterios.getSelectedItem());
            int alternativas;
            alternativas = Integer.valueOf((String)jComboBoxAlternativas.getSelectedItem());
            vista.useController().crearNuevoProyecto(criterios, alternativas);
        });
        add(jPanelParametros, "wrap");
        jPanelParametros.add(jLabelCriterios, "split 2");
        jPanelParametros.add(jComboBoxCriterios, "wrap");
        jPanelParametros.add(jLabelAlternativas, "split 2");
        jPanelParametros.add(jComboBoxAlternativas, "wrap");
        add(jButtonContinuar, "align right");
    }
    
}
