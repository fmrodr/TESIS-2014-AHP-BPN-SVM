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

import ahp.AHPproblema;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.miginfocom.swing.MigLayout;

public class Contenedor extends javax.swing.JFrame{
    
    private JMenuBar jMenuBarPrincipal;
    private JMenu jMenuArchivo;
    private JMenuItem jMenuItemSalir;
    private JMenu jMenuAyuda;
    private JMenuItem jMenuItemAcercaDe;
    private JMenuItem jMenuItemNuevo;
    private JMenuItem jMenuItemRestaurar;
    private JMenuItem jMenuItemGuardar;
    private final Vistas vista;
    private JMenuItem jMenuItemAHP;
    
    public Contenedor(Vistas aView) {
        vista = aView;
        initComponents();
        setLookAndFeel();
    }  
    
    private void initComponents() {
        setLayout(new MigLayout());
        //Setear boton salida de la ventana principal
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
              vista.salirSistema();
            }
        });
        
        getContentPane().setBackground( Color.WHITE );
        
        ImageIcon ii = new ImageIcon(this.getClass().getResource("/resources/ahp_logo.png"));
        setIconImage(ii.getImage());
        
        //Barra de Menus
        jMenuBarPrincipal = new javax.swing.JMenuBar();
        setJMenuBar(jMenuBarPrincipal);
        
        //Menu Archivo
        jMenuArchivo = new javax.swing.JMenu();
        jMenuArchivo.setText("Proyecto AHP");
        jMenuItemNuevo = new javax.swing.JMenuItem();       
        jMenuItemNuevo.setIcon(new ImageIcon(this.getClass().getResource("/resources/proyecto_nuevo.png")));
        jMenuItemNuevo.setText("Nuevo");
        jMenuItemNuevo.addActionListener(this::eventoClickNuevoProyecto);
        jMenuArchivo.add(jMenuItemNuevo);
        jMenuItemRestaurar = new javax.swing.JMenuItem();
        jMenuItemRestaurar.setIcon(new ImageIcon(this.getClass().getResource("/resources/proyecto_restaurar.png")));
        jMenuItemRestaurar.setText("Restaurar");
        jMenuItemRestaurar.addActionListener(this::eventoClickRestaurarProyecto);
        jMenuArchivo.add(jMenuItemRestaurar);
        jMenuItemGuardar = new javax.swing.JMenuItem();
        jMenuItemGuardar.setIcon(new ImageIcon(this.getClass().getResource("/resources/proyecto_guardar.png")));
        jMenuItemGuardar.setText("Guardar");
        jMenuItemGuardar.addActionListener(this::eventoClickGuardarProyecto);
        jMenuArchivo.add(jMenuItemGuardar);
        jMenuItemSalir = new javax.swing.JMenuItem();
        jMenuItemSalir.setIcon(new ImageIcon(this.getClass().getResource("/resources/salir.png")));
        jMenuItemSalir.setText("Salir");
        jMenuItemSalir.addActionListener(this::eventoClickSalir);
        jMenuArchivo.add(jMenuItemSalir);
        jMenuBarPrincipal.add(jMenuArchivo);
        
        //Menu Ayuda
        jMenuAyuda = new javax.swing.JMenu();
        jMenuAyuda.setText("Ayuda");
        jMenuItemAHP = new javax.swing.JMenuItem();
        jMenuItemAHP.setIcon(new ImageIcon(this.getClass().getResource("/resources/ayuda_ahp.png")));
        jMenuItemAHP.setText("Sobre AHP");
        jMenuItemAHP.addActionListener(this::eventoClickAHP);
        jMenuAyuda.add(jMenuItemAHP);
        jMenuItemAcercaDe = new javax.swing.JMenuItem();
        jMenuItemAcercaDe.setIcon(new ImageIcon(this.getClass().getResource("/resources/ayuda_acercade.png")));
        jMenuItemAcercaDe.setText("Acerca de...");
        jMenuItemAcercaDe.addActionListener(this::eventoClickAcercaDe);
        jMenuAyuda.add(jMenuItemAcercaDe);
        jMenuBarPrincipal.add(jMenuAyuda);               
    }
    
    private void eventoClickNuevoProyecto(java.awt.event.ActionEvent evt) {                                                  
        vista.desplegarPanelInitialParameters();
    }
    
    private void eventoClickRestaurarProyecto(java.awt.event.ActionEvent evt) { 
        //Options for the combo box dialog       
        LinkedList<AHPproblema> listaProyectos = vista.useController().getDb().recuperarProyectosExistentes();
        if(!listaProyectos.isEmpty()){
            int size = listaProyectos.size()+1;
            String[] proyectos = new String[size];
            proyectos[0] = "Elija un projecto existente de la lista...";
            listaProyectos.stream().forEach((unProblema) -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String fechaCreacion = dateFormat.format(unProblema.getFechaCreacion().getTime());
                proyectos[unProblema.getId()] = " Proyecto #"+unProblema.getId()+" - Fecha: "+fechaCreacion;
            });
            String eleccion = (String)JOptionPane.showInputDialog(this, "<html><p style='text-align: center; font-weight: bold'>Restaurar Proyecto</p><p>A continuacion puede elegir cargar algun proyecto previamente guardado en el sistema:</p>"
                    , "Restaurar un Proyecto AHP existente", JOptionPane.QUESTION_MESSAGE, null, proyectos, proyectos[0]);
            if(eleccion!=null){
                if (eleccion.contains("#")) {
                    String[]tokens = eleccion.split("#|-");
                    for(AHPproblema unProblema : listaProyectos){
                        if(unProblema.getId()==Integer.parseInt(tokens[1].replace(" ", ""))){
                            vista.useController().setProblema(unProblema);
                            System.out.println("A="+vista.useController().getProblema().getAlternativesWeight().keySet());
                            vista.desplegarPanelCriteriaWeight();
                        }
                    }
                }
            }
        }else{
            vista.mensajeInformativo("No hay proyectos guardados.", "Sin Proyectos");
        }       
    }
    
    private void eventoClickGuardarProyecto(java.awt.event.ActionEvent evt) {   
        vista.useController().getDb().guardarNuevoProyecto(vista.useController().getProblema());
    }
    
    private void eventoClickSalir(java.awt.event.ActionEvent evt) {                                                  
        vista.salirSistema();
    }
    
    private void eventoClickAcercaDe(java.awt.event.ActionEvent evt) {                                                  
    }
    
    private void eventoClickAHP(java.awt.event.ActionEvent evt) {                                                  
    }
    
    private void setLookAndFeel(){
        //Look and Feel
        setLocationRelativeTo(null);
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException | 
                ClassNotFoundException | 
                InstantiationException | 
                IllegalAccessException e) {
           // handle exception
        }
    }
    
}
