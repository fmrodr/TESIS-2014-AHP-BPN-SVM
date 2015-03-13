/**
 * Maquina de Soporte de Vectores para procesar matrices del Proceso Analitico Jerarquico
 * @author Federico Matias Rodriguez y Marianela Labat
 */

package ahpbpn;

import static ahpbpn.AHPNeuralNetwork.ORDEN;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    
    private BufferedWriter fileWriter;
    private final File logEjecucion;
    
    public Log(String cabecera){
        logEjecucion = new File("src/ahpbpn/logs/bpn-orden-"+ORDEN+".txt");       
        try {
            fileWriter = new BufferedWriter(new FileWriter(logEjecucion));
            fileWriter.write(cabecera);
            fileWriter.newLine();
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(AHPNeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void agregarLinea(String linea){
        try {
            fileWriter = new BufferedWriter(new FileWriter(logEjecucion,true));
            fileWriter.write(linea);
            fileWriter.newLine();
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cerrarLog(){
        try {
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
