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

import ahp.Controladora;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author FedericoMatias
 */
public class PanelResultadosTest {
    
    private Controladora contr;
    private Vistas view;
    private PanelResultados panelResults;
    private JFrame unFrame;
    
    public PanelResultadosTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        while (true) { 
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PanelResultadosTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Test of initComponents method, of class PanelResultados.
     */
    @Test
    public void testInitComponents() {
        System.out.println("initComponents");
        panelResults = new PanelResultados(view);
        unFrame = new JFrame();
        unFrame.setContentPane(panelResults);
        unFrame.setVisible(true);
        panelResults.setVisible(true);        
        unFrame.pack();
        unFrame.setLocationRelativeTo(null);
        unFrame.setResizable(false);       
    }
    
}
