/**
 * Maquina de Soporte de Vectores para procesar matrices del Proceso Analitico Jerarquico
 * @author Federico Matias Rodriguez y Marianela Labat
 */

package ahpsvm;

import static ahpsvm.AHPSupportVectorMachine.ORDEN;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    
    private BufferedWriter fileWriter;
    
    public Log(String cabecera){
        File logEjecucion = new File("src/ahpsvm/logs/svm-orden-"+ORDEN+".txt");       
        try {
            fileWriter = new BufferedWriter(new FileWriter(logEjecucion));
            fileWriter.write(cabecera);
            fileWriter.newLine();
        } catch (IOException ex) {
            Logger.getLogger(AHPSupportVectorMachine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void agregarLinea(String linea){
        try {
            fileWriter.write(linea);
            fileWriter.newLine();
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
