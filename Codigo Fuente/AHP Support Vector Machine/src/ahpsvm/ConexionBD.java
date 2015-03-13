/**
 * Maquina de Soporte de Vectores para procesar matrices del Proceso Analitico Jerarquico
 * @author Federico Matias Rodriguez y Marianela Labat
 */

package ahpsvm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

public class ConexionBD {
    
    private Connection conn;
    public static int SAATY_SCALE[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
    //Normalizar valores con un rango entre (1 y 17) a (1 y 0)
    public static NormalizedField norm = new NormalizedField(NormalizationAction.Normalize, null,17,1,1,0);
    
    public void Connection(){                            	
        try {
            //Establecer el conector a la base de datos JDBC
            Class.forName("org.sqlite.JDBC");
            //Leer archivo de base de datos donde se encuentran las matrices generadas aleatoriamente
            conn = DriverManager.getConnection("jdbc:sqlite:src/ahpsvm/matricesahp.s3db");
        } catch (SQLException | ClassNotFoundException e) {
        }                         
    }

    public double obtenerValorEscalaSaaty(int index) {
        if(index == 1){
            return 0.111111111;
        }else if(index == 2){
            return 0.125;
        }else if(index == 3){
            return 0.142857143;
        }else if(index == 4){
            return 0.166666667;
        }else if(index == 5){
            return 0.2;
        }else if(index == 6){
            return 0.25;
        }else if(index == 7){
            return 0.333333333;
        }else if(index == 8){
            return 0.5;
        }else if(index == 9){
            return 1.0;
        }else if(index == 10){
            return 2.0;
        }else if(index == 11){
            return 3.0;
        }else if(index == 12){
            return 4.0;
        }else if(index == 13){
            return 5.0;
        }else if(index == 14){
            return 6.0;
        }else if(index == 15){
            return 7.0;
        }else if(index == 16){
            return 8.0;
        }else if(index == 17){
            return 9.0;
        }        
        return 0;
    }
    
    public int obtenerIndiceEscalaSaaty(double valor){
        if(valor <= 0.111111111){
            return SAATY_SCALE[0];
        }else if(valor == 0.125){
            return SAATY_SCALE[1];
        }else if(valor == 0.142857143){
            return SAATY_SCALE[2];
        }else if(valor == 0.166666667){
            return SAATY_SCALE[3];
        }else if(valor == 0.2){
            return SAATY_SCALE[4];
        }else if(valor == 0.25){
            return SAATY_SCALE[5];
        }else if(valor == 0.333333333){
            return SAATY_SCALE[6];
        }else if(valor == 0.5){
            return SAATY_SCALE[7];
        }else if(valor == 1.0){
            return SAATY_SCALE[8];
        }else if(valor == 2.0){
            return SAATY_SCALE[9];
        }else if(valor == 3.0){
            return SAATY_SCALE[10];
        }else if(valor == 4.0){
            return SAATY_SCALE[11];
        }else if(valor == 5.0){
            return SAATY_SCALE[12];
        }else if(valor == 6.0){
            return SAATY_SCALE[13];
        }else if(valor == 7.0){
            return SAATY_SCALE[14];
        }else if(valor == 8.0){
            return SAATY_SCALE[15];
        }else if(valor == 9.0){
            return SAATY_SCALE[16];
        }
        return 0;
    }
    
    public double[][] obtenerElementosMatrizAHP(int orden, String tabla) {             
        //creamos una estructura de tipo matriz para tener la lista de todas las matrices con sus valores para procesarla con Encog
        int elementos = (orden*(orden-1))/2;
        double[][] matricesAHP = new double[1000][elementos];
        //Parte inicial de la consulta
        String query = "SELECT ";
        switch(orden){
            //Acorde al orden se obtienen los elementos de la porcion triangular superior de la matriz, sin incluir los 1
            case 3: query += "a12, a13, a23 ";
            		break;
            case 4: query += "a12, a13, a14, a23, a24, a34 ";
            		break;
            case 5: query += "a12, a13, a14, a15, a23, a24, a25, a34, a35, a45 ";
            		break;
            case 6: query += "a12, a13, a14, a15, a16, a23, a24, a25, a26, a34, a35, a36, a45, a46, a56 ";
            		break;
            case 7: query += "a12, a13, a14, a15, a16, a17, a23, a24, a25, a26, a27, a34, a35, a36, a37, a45, a46, a47, a56, a57, a67 ";
            		break;
            case 8: query += "a12, a13, a14, a15, a16, a17, a18, a23, a24, a25, a26, a27, a28, a34, a35, a36, a37, a38, a45, a46, a47, a48, a56, a57, a58, a67, a68, a78 ";  
            		break;
        }
        query += " FROM '"+tabla+"' WHERE (orden = "+orden+" ) LIMIT 1000;";   
        //A continuacion se tomaran los elementos de la base de datos
        try {            
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(query);
            int indiceMatrices = 0;              
            while(rs.next()){
              for(int i=0; i<elementos; i++){        
                matricesAHP[indiceMatrices][i] = rs.getDouble(i+1);
              }
              //System.out.print("\n");
              indiceMatrices++;
            }
        } catch (SQLException ex) {
            System.out.println("ERROR DE BASE DE DATOS: "+ex.getMessage());
        }
        return matricesAHP;
    }
      
    
    //Método para poder ver el contenido del arreglo
    public void recorrerArreglo(int orden, double[][] unaMatriz){
        
        int elementos = (orden*(orden-1))/2, cantMatrices = 1;
        for (double[] iterador : unaMatriz) {            
            System.out.print(cantMatrices+"[ ");
            for (int c = 0; c<elementos; c++) {
                System.out.print(iterador[c]+ " ");
            }
            System.out.print("]\n");
            cantMatrices++;
        }
    }          
    
    public double[][] dividirSetElementos(double[][] ARRAY_ORIGEN, int orden, int inicio, int fin, int cantidad) { 
        int elementos = (orden*(orden-1))/2;
        double ARRAY_NUEVO[][] = new double[cantidad][elementos];
        for(int i = 0; i < cantidad; i++){
            System.arraycopy(ARRAY_ORIGEN[inicio], 0, ARRAY_NUEVO[i], 0, elementos);
            inicio++;
        }
        return ARRAY_NUEVO;
    }  
    
    public void normalizarArreglo(int orden, double[][] ARREGLO_AHP){
        int elementos = (orden*(orden-1))/2; 
        for(int f=0;f<ARREGLO_AHP.length;f++){
        	for(int c=0;c<elementos; c++){   
        		int indice = this.obtenerIndiceEscalaSaaty(ARREGLO_AHP[f][c]);
        		double valorNormalizado = norm.normalize(indice);   
        		ARREGLO_AHP[f][c] = valorNormalizado;
        	}
        	//System.out.print("\n ");
        }
    }
    
    public void desnormalizarArreglo(int orden, double[] ARREGLO){
        	for(int i=0;i<ARREGLO.length; i++){  
        		int escalaSaaty = (int)norm.deNormalize(ARREGLO[i]);
        		double valorDesnormalizado = this.obtenerValorEscalaSaaty(escalaSaaty);
        		ARREGLO[i] = valorDesnormalizado;
        	}
        	//System.out.print("\n ");
    }
    
    public double desnormalizar(double datoNormalizado){   	
    	int escalaSaaty = (int)norm.deNormalize(datoNormalizado);
    	double valorDesnormalizado = this.obtenerValorEscalaSaaty(escalaSaaty);       
        return valorDesnormalizado;
    }
    
    public double[][] extraerColumna(int orden, int columna, double[][] ARREGLO){
        double ARRAY_NUEVO[][] = new double[ARREGLO.length][1];
        for(int f=0; f<ARREGLO.length; f++){          
            ARRAY_NUEVO[f][0] = ARREGLO[f][columna];                                     
        }
        return ARRAY_NUEVO;
    }
    
}
