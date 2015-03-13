/*
 * Software perteneciente al trabajo de Tésis:
 * Analisis Comparativo de Tecnicas de IA para la Correccion de la Inconsistencia
 * de la Matriz del Proceso Analítico Jerarquico
 * Autor: Federico Matias Rodriguez - rfedericomatias@gmail.com
 * Autor: Marianela Daianna Labat - daiannalabat@gmail.com
 * Año: 2014
 * Universidad Gaston Dachary
 */
package persistence;

import Jama.Matrix;
import ahp.AHPmatriz;
import ahp.AHPproblema;
import interfaces.Vistas;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

public final class ConexionBD {

    private Connection conn;
    private final Vistas vista;
    public static HashMap<Integer, String> tipoMetodos;
    public static HashMap<Integer, String> tipoMatrices;

    public ConexionBD(Vistas vista) {
        this.vista = vista;
        levantarBaseDeDatos();
    }

    public void levantarBaseDeDatos() {
        try {
            //Establecer el conector a la base de datos JDBC
            Class.forName("org.sqlite.JDBC");
            //Leer archivo de base de datos donde se encuentran las matrices generadas aleatoriamente
            conn = DriverManager.getConnection("jdbc:sqlite:src/persistence/db_ahp");
            conn.setAutoCommit(true);
            String query = "SELECT id, tipo FROM tipo_matriz;";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            tipoMatrices = new HashMap<>();
            while (rs.next()) {
                tipoMatrices.put(rs.getInt("id"), rs.getString("tipo"));
            }
            query = "SELECT id, metodo FROM tipo_metodo_usado;";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            tipoMetodos = new HashMap<>();
            while (rs.next()) {
                tipoMetodos.put(rs.getInt("id"), rs.getString("metodo"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            vista.desplegarMensajeError("Error de Base de Datos: " + e.getMessage(), "ERROR DE DATOS");
        }
    }

    public LinkedList<AHPproblema> recuperarProyectosExistentes() {
        LinkedList<AHPproblema> listaDeProyectos = new LinkedList<>();
        try {
            String query = "SELECT id, fecha_creacion, criterios, alternativas FROM proyecto ORDER BY id ASC;";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                AHPproblema unProblema = new AHPproblema();
                unProblema.setId(rs.getInt("id"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Calendar fechaCreacion = Calendar.getInstance();
                fechaCreacion.setTime(dateFormat.parse(rs.getString("fecha_creacion")));
                unProblema.setFechaCreacion(fechaCreacion);
                unProblema.setAlternativas(rs.getInt("alternativas"));
                unProblema.setCriterios(rs.getInt("criterios"));
                unProblema.setCriteriaWeight(obtenerElementosMatrizCriterios(unProblema));
                unProblema.setAlternativesWeight(obtenerMatricesAlternativas(unProblema));
                unProblema.setResult(obtenerMatrizResultados(unProblema));
                listaDeProyectos.add(unProblema);
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'recuperarProyectosExistentes': " + ex.getMessage(), "ERROR DE DATOS");
            System.exit(1);
        } catch (ParseException ex) {
            vista.desplegarMensajeError("Error de PARSING 'recuperarProyectosExistentes': " + ex.getMessage(), "ERROR DE PARSING");
            System.exit(1);
        }
        return listaDeProyectos;
    }

    public AHPmatriz obtenerElementosMatrizCriterios(AHPproblema unProblema) {
        int orden = unProblema.getCriterios();
        Matrix matrizAHP = new Matrix(orden, orden);
        AHPmatriz matrizCriterios = new AHPmatriz(matrizAHP);
        try {
            Matrix vectorPrioridades = new Matrix(orden, 1);
            //Parte inicial de la consulta
            String query = "SELECT ";
            switch (orden) {
                //Acorde al orden se obtienen los elementos de la porcion triangular superior de la matriz, sin incluir los 1
                case 3:
                    query += "a12, a13, a23, vp1, vp2, vp3 ";
                    break;
                case 4:
                    query += "a12, a13, a14, a23, a24, a34, vp1, vp2, vp3, vp4 ";
                    break;
                case 5:
                    query += "a12, a13, a14, a15, a23, a24, a25, a34, a35, a45, vp1, vp2, vp3, vp4, vp5 ";
                    break;
                case 6:
                    query += "a12, a13, a14, a15, a16, a23, a24, a25, a26, a34, a35, a36, a45, a46, a56, vp1, vp2, vp3, vp4, vp5, vp6 ";
                    break;
                case 7:
                    query += "a12, a13, a14, a15, a16, a17, a23, a24, a25, a26, a27, a34, a35, a36, a37, a45, a46, a47, a56, a57, a67, vp1, vp2, vp3, vp4, vp5, vp6, vp7 ";
                    break;
                case 8:
                    query += "a12, a13, a14, a15, a16, a17, a18, a23, a24, a25, a26, a27, a28, a34, a35, a36, a37, a38, a45, a46, a47, a48, a56, a57, a58, a67, a68, a78, vp1, vp2, vp3, vp4, vp5, vp6, vp7, vp8 ";
                    break;
            }
            query += ", ci, ri, cr FROM matriz_ponderaciones WHERE (id = " + unProblema.getId() + " AND criterio = 0 );";
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                for (int f = 0; f < orden; f++) {
                    for (int c = 0; c < orden; c++) {
                        if (f == c) {
                            matrizCriterios.getMainMatrix().set(f, c, 1.0);
                        } else if (c > f) {
                            String elemento = "a" + (f + 1) + (c + 1);
                            matrizCriterios.getMainMatrix().set(f, c, rs.getDouble(elemento));
                            double inverse = Math.pow(rs.getDouble(elemento), -1);
                            matrizCriterios.getMainMatrix().set(c, f, inverse);
                        }
                        String elemento = "vp" + (f + 1);
                        vectorPrioridades.set(f, 0, rs.getDouble(elemento));
                    }
                }
                matrizCriterios.setCi(rs.getDouble("ci"));
                matrizCriterios.setCi(rs.getDouble("ri"));
                matrizCriterios.setCr(rs.getDouble("cr"));
                matrizCriterios.setPriorityVector(vectorPrioridades);
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'obtenerElementosMatrizCriterios': " + ex, "ERROR DE DATOS");
        }
        return matrizCriterios;
    }

    public HashMap<Integer, AHPmatriz> obtenerMatricesAlternativas(AHPproblema unProblema) {
        HashMap<Integer, AHPmatriz> mapaPonderacionesAlternativas = new HashMap<>();
        try {
            //creamos una estructura de tipo matriz para tener la lista de todas las matrices con sus valores para procesarla con Encog
            int criterios = unProblema.getCriterios();
            int alternativas = unProblema.getAlternativas();
            for (int crit = 1; crit <= criterios; crit++) {
                int orden = unProblema.getAlternativas();
                Matrix matrizAHP = new Matrix(alternativas, alternativas);
                Matrix vectorPrioridades = new Matrix(orden, 1);
                AHPmatriz matrizCriterios = new AHPmatriz(matrizAHP);
                matrizCriterios.setCr(-1);
                //Parte inicial de la consulta
                String query = "SELECT ";
                switch (orden) {
                    //Acorde al orden se obtienen los elementos de la porcion triangular superior de la matriz, sin incluir los 1
                    case 3:
                        query += "a12, a13, a23, vp1, vp2, vp3 ";
                        break;
                    case 4:
                        query += "a12, a13, a14, a23, a24, a34, vp1, vp2, vp3, vp4 ";
                        break;
                    case 5:
                        query += "a12, a13, a14, a15, a23, a24, a25, a34, a35, a45, vp1, vp2, vp3, vp4, vp5 ";
                        break;
                    case 6:
                        query += "a12, a13, a14, a15, a16, a23, a24, a25, a26, a34, a35, a36, a45, a46, a56, vp1, vp2, vp3, vp4, vp5, vp6 ";
                        break;
                    case 7:
                        query += "a12, a13, a14, a15, a16, a17, a23, a24, a25, a26, a27, a34, a35, a36, a37, a45, a46, a47, a56, a57, a67, vp1, vp2, vp3, vp4, vp5, vp6, vp7 ";
                        break;
                    case 8:
                        query += "a12, a13, a14, a15, a16, a17, a18, a23, a24, a25, a26, a27, a28, a34, a35, a36, a37, a38, a45, a46, a47, a48, a56, a57, a58, a67, a68, a78, vp1, vp2, vp3, vp4, vp5, vp6, vp7, vp8 ";
                        break;
                }
                query += ", ci, ri, cr  FROM matriz_ponderaciones WHERE (id = " + unProblema.getId() + " AND criterio = " + crit + " );";
                Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery(query);
                while (rs.next()) {
                    for (int f = 0; f < orden; f++) {
                        for (int c = 0; c < orden; c++) {
                            if (f == c) {
                                matrizCriterios.getMainMatrix().set(f, c, 1.0);
                            } else if (c > f) {
                                String elemento = "a" + (f + 1) + (c + 1);
                                matrizCriterios.getMainMatrix().set(f, c, rs.getDouble(elemento));
                                double inverse = Math.pow(rs.getDouble(elemento), -1);
                                matrizCriterios.getMainMatrix().set(c, f, inverse);
                            }
                            String elemento = "vp" + (f + 1);
                            vectorPrioridades.set(f, 0, rs.getDouble(elemento));
                        }
                    }
                    matrizCriterios.setCi(rs.getDouble("ci"));
                    matrizCriterios.setCi(rs.getDouble("ri"));
                    matrizCriterios.setCr(rs.getDouble("cr"));
                    matrizCriterios.setPriorityVector(vectorPrioridades);
                }
                mapaPonderacionesAlternativas.put(crit, matrizCriterios);
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'obtenerMatricesAlternativas': " + ex.getMessage(), "ERROR DE DATOS");
        }
        return mapaPonderacionesAlternativas;
    }

    public AHPmatriz obtenerMatrizResultados(AHPproblema unProblema) {
        Matrix unaMatriz = new Matrix(unProblema.getAlternativas(), unProblema.getCriterios());
        AHPmatriz matrizResultados = new AHPmatriz(unaMatriz);
        try {
            Matrix vectorDeResultados = new Matrix(unProblema.getAlternativas(), 1);
            String query = "SELECT ";
            for (int f = 1; f <= unaMatriz.getRowDimension(); f++) {
                for (int c = 1; c <= unaMatriz.getColumnDimension(); c++) {
                    query += "r" + f + c + ", ";
                }
            }
            for (int f = 1; f <= vectorDeResultados.getRowDimension(); f++) {
                String elemento = "vr" + f + ", ";
                if (f == vectorDeResultados.getRowDimension()) {
                    elemento = "vr" + f + " ";
                }
                query += elemento;
            }
            query += " FROM resultados WHERE (id_proyecto = " + unProblema.getId() + ");";
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                for (int f = 0; f < unaMatriz.getRowDimension(); f++) {
                    for (int c = 0; c < unaMatriz.getColumnDimension(); c++) {
                        String elemento = "r" + (f + 1) + (c + 1);
                        unaMatriz.set(f, c, rs.getDouble(elemento));
                    }
                    String elemento = "vr" + (f + 1);
                    vectorDeResultados.set(f, 0, rs.getDouble(elemento));
                }
            }
            matrizResultados.setPriorityVector(vectorDeResultados);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'obtenerMatrizResultados': " + ex.getMessage(), "ERROR DE DATOS");
        }
        return matrizResultados;
    }

    public void guardarNuevoProyecto(AHPproblema unProblema) {
        if (unProblema != null) {
            if (unProblema.getCriteriaWeight().getCr() >= 0) {
                if (buscarExistencia(unProblema, "proyecto")) {
                    actualizarMatrizDeCriterios(unProblema);
                    actualizarMatrizDeAlternativas(unProblema);
                    actualizarMatrizDeResultados(unProblema);
                    //Persistir Historial
                    if (unProblema.getUnHistorial() != null) {
                        if (unProblema.getUnHistorial().getCriteriaWeight() != null) {
                            boolean existenciaHistoricoCriterio = buscarExistenciaHistorico(unProblema.getId(), unProblema.getUnHistorial().getCriteriaWeight(), 0);
                            if (existenciaHistoricoCriterio) {
                                persistirHistorico(unProblema.getId(), unProblema.getUnHistorial().getCriteriaWeight(), 0);
                            } else {
                                actualizarHistorico(unProblema.getId(), unProblema.getUnHistorial().getCriteriaWeight(), 0);
                            }
                        }
                        if (unProblema.getUnHistorial().getAlternativesWeight() != null) {
                            for (int key : unProblema.getUnHistorial().getAlternativesWeight().keySet()) {
                                boolean existenciaHistoricoCriterioAlternativa = buscarExistenciaHistorico(unProblema.getId(), unProblema.getUnHistorial().getAlternativesWeight().get(key), key);
                                if (existenciaHistoricoCriterioAlternativa) {
                                    persistirHistorico(unProblema.getId(), unProblema.getUnHistorial().getAlternativesWeight().get(key), key);
                                } else {
                                    actualizarHistorico(unProblema.getId(), unProblema.getUnHistorial().getAlternativesWeight().get(key), key);
                                }
                            }
                        }
                    }
                    vista.mensajeInformativo("Guardado exitoso", "El proyecto se ha actualizado correctamente.");
                } else {
                    insertarProyecto(unProblema);
                    insertarMatrizCriterios(unProblema);
                    insertarMatrizPonderacionAlternativas(unProblema);
                    if (unProblema.getResult().getPriorityVector() != null) {
                        insertarMatrizResultados(unProblema);
                    }
                    //Persistir Historial
                    if (unProblema.getUnHistorial() != null) {
                        if (unProblema.getUnHistorial().getCriteriaWeight() != null) {
                            persistirHistorico(unProblema.getId(), unProblema.getUnHistorial().getCriteriaWeight(), 0);
                        }
                        if (unProblema.getUnHistorial().getAlternativesWeight() != null) {
                            for (int key : unProblema.getUnHistorial().getAlternativesWeight().keySet()) {
                                boolean existenciaHistoricoCriterioAlternativa = buscarExistenciaHistorico(unProblema.getId(), unProblema.getUnHistorial().getAlternativesWeight().get(key), key);
                                if (existenciaHistoricoCriterioAlternativa) {
                                    persistirHistorico(unProblema.getId(), unProblema.getUnHistorial().getAlternativesWeight().get(key), key);
                                } else {
                                    actualizarHistorico(unProblema.getId(), unProblema.getUnHistorial().getAlternativesWeight().get(key), key);
                                }
                            }
                        }
                    }
                    vista.mensajeInformativo("Guardado exitoso", "El proyecto se ha guardado correctamente.");
                }
            } else {
                vista.desplegarMensajeError("<html><p>No ha sido calculado el Vector de Prioridades</p></html>", "ERROR DE DATOS");
            }
        } else {
            vista.desplegarMensajeError("<html><p>No existe un proyecto para aguardar<br>Haga click en Nuevo para crear un nuevo Proyecto</p></html>", "ERROR DE DATOS");
        }
    }

    public void insertarProyecto(AHPproblema unProblema) {
        try {
            Statement stmt = conn.createStatement();
            //Proyecto
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String fechaCreacion = dateFormat.format(unProblema.getFechaCreacion().getTime());
            String queryInsertProyecto = "INSERT INTO proyecto (id, fecha_creacion, criterios, alternativas) "
                    + "VALUES ( " + unProblema.getId() + ", '" + fechaCreacion + "', " + unProblema.getCriterios() + ", " + unProblema.getAlternativas() + " );";
            stmt.executeUpdate(queryInsertProyecto);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'insertarProyecto': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public void insertarMatrizCriterios(AHPproblema unProblema) {
        try {
            //Matriz de Ponderacion de Criterios
            Statement stmt = conn.createStatement();
            int orden = unProblema.getCriterios();
            String queryInsertMatrizCriterios = generarInsert(unProblema.getId(), orden, unProblema.getCriteriaWeight(), 1, 0);
            stmt.executeUpdate(queryInsertMatrizCriterios);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'insertarMatrizCriterios': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public void insertarMatrizPonderacionAlternativas(AHPproblema unProblema) {
        try {
            //Matriz de Ponderacion de Alternativas por cada Criterio
            Statement stmt = conn.createStatement();
            int orden = unProblema.getAlternativas();
            String queryInsertMatrizAlternativa = "";
            for (int key : unProblema.getAlternativesWeight().keySet()) {
                AHPmatriz ponderacionAlternativa = unProblema.getAlternativesWeight().get(key);
                if (ponderacionAlternativa.getCr() >= 0) {
                    queryInsertMatrizAlternativa = generarInsert(unProblema.getId(), orden, ponderacionAlternativa, 2, key);
                    stmt.executeUpdate(queryInsertMatrizAlternativa);
                }
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'insertarMatrizPonderacionAlternativas': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public void insertarMatrizResultados(AHPproblema unProblema) {
        try {
            //Matriz de Resultados
            Statement stmt = conn.createStatement();
            String queryInsertMatrizResultados = "INSERT INTO resultados (id_proyecto, ";
            for (int f = 0; f < unProblema.getResult().getMainMatrix().getRowDimension(); f++) {
                for (int c = 0; c < unProblema.getResult().getMainMatrix().getColumnDimension(); c++) {
                    queryInsertMatrizResultados += "r" + (f + 1) + "" + (c + 1) + ", ";
                }
            }
            for (int f = 0; f < unProblema.getResult().getPriorityVector().getRowDimension(); f++) {
                queryInsertMatrizResultados += "vr" + (f + 1) + ",";
            }
            queryInsertMatrizResultados += "alternativas, criterios) VALUES ( " + unProblema.getId() + " ,";
            for (int f = 0; f < unProblema.getResult().getMainMatrix().getRowDimension(); f++) {
                for (int c = 0; c < unProblema.getResult().getMainMatrix().getColumnDimension(); c++) {
                    queryInsertMatrizResultados += unProblema.getResult().getMainMatrix().get(f, c) + ",";
                }
            }
            for (int f = 0; f < unProblema.getResult().getPriorityVector().getRowDimension(); f++) {
                queryInsertMatrizResultados += unProblema.getResult().getPriorityVector().get(f, 0) + ",";
            }
            queryInsertMatrizResultados += unProblema.getAlternativas() + " ," + unProblema.getCriterios() + ");";
            stmt.executeUpdate(queryInsertMatrizResultados);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'insertarMatrizResultados': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public void actualizarMatrizDeCriterios(AHPproblema unProblema) {
        try {
            //ACTUALIZAR EL VECTOR DE PRIORIDADES DE LA MATRIZ DE CRITERIOS
            Statement stmt = conn.createStatement();
            String update = "UPDATE matriz_ponderaciones SET ";
            for (int f = 0; f < unProblema.getCriteriaWeight().getPriorityVector().getRowDimension(); f++) {
                String elemento = "vp" + (f + 1) + " = ";
                update += elemento + unProblema.getCriteriaWeight().getPriorityVector().get(f, 0) + ", ";
            }
            update += " ci = " + unProblema.getCriteriaWeight().getCi() + ", ";
            update += " ri = " + unProblema.getCriteriaWeight().getRi() + ", ";
            update += " cr = " + unProblema.getCriteriaWeight().getCr();
            update += " WHERE (id = " + unProblema.getId() + " AND criterio = 0);";
            stmt.executeUpdate(update);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'actualizarMatrizDeCriterios': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public void actualizarMatrizDeAlternativas(AHPproblema unProblema) {
        try {
            //ACTUALIZAR LA MATRIZ DE PONDERACIONES DE ALTERNATIVAS
            Statement stmt = conn.createStatement();
            String update = "";
            for (int key : unProblema.getAlternativesWeight().keySet()) {
                AHPmatriz ponderacionAlternativa = unProblema.getAlternativesWeight().get(key);
                if (ponderacionAlternativa.getCr() >= 0) {
                    update = "UPDATE matriz_ponderaciones SET ";
                    //Matriz de Ponderacion de Alternativas por cada Criterio
                    for (int f = 0; f < ponderacionAlternativa.getMainMatrix().getRowDimension(); f++) {
                        for (int c = 0; c < ponderacionAlternativa.getMainMatrix().getColumnDimension(); c++) {
                            if (f != c && c > f) {
                                String elemento = "a" + (f + 1) + (c + 1) + " = ";
                                update += elemento + ponderacionAlternativa.getMainMatrix().get(f, c) + ", ";
                            }
                        }
                    }
                    //ACTUALIZAR EL VECTOR DE PRIORIDADES DE LA MATRIZ DE CRITERIOS
                    for (int f = 0; f < ponderacionAlternativa.getPriorityVector().getRowDimension(); f++) {
                        String elemento = "vp" + (f + 1) + " = ";
                        update += elemento + ponderacionAlternativa.getPriorityVector().get(f, 0) + ", ";
                    }
                    update += " ci = " + ponderacionAlternativa.getCi() + ", ";
                    update += " ri = " + ponderacionAlternativa.getRi() + ", ";
                    update += " cr = " + ponderacionAlternativa.getCr();
                    update += " WHERE (id = " + unProblema.getId() + " AND criterio = " + key + ");";
                    stmt.executeUpdate(update);
                }
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'actualizarMatrizDeAlternativas': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public void actualizarMatrizDeResultados(AHPproblema unProblema) {
        try {
            //ACTUALIZAR LA MATRIZ DE RESULTADOS
            Statement stmt = conn.createStatement();
            String update = "UPDATE resultados SET ";
            for (int f = 0; f < unProblema.getAlternativas(); f++) {
                for (int c = 0; c < unProblema.getCriterios(); c++) {
                    String elemento = "r" + (f + 1) + (c + 1) + " = ";
                    update += elemento + unProblema.getResult().getMainMatrix().get(f, c) + ", ";
                }
            }
            for (int a = 0; a < unProblema.getAlternativas(); a++) {
                String elemento = "vr" + (a + 1) + " = ";
                update += elemento + unProblema.getResult().getPriorityVector().get(a, 0) + ", ";
            }
            update += " alternativas = " + unProblema.getAlternativas() + ", criterios = " + unProblema.getCriterios() + " ";
            update += " WHERE (id_proyecto = " + unProblema.getId() + ");";
            stmt.executeUpdate(update);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'actualizarMatrizDeResultados': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public boolean buscarExistencia(AHPproblema unProblema, String tabla) {
        boolean exito = false;
        try {
            String query = "SELECT id FROM " + tabla + " WHERE (id = " + unProblema.getId() + ");";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                exito = true;
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'buscarExistencia': " + ex.getMessage(), "ERROR DE DATOS");
        }
        return exito;
    }

    public boolean borrarProyecto(AHPproblema unProblema) {
        boolean exito = false;
        return exito;
    }

    public int obtenerSiguienteId(String tabla) {
        int sigId = -1;
        try {
            try (Statement stmt = conn.createStatement()) {
                String queryLastId = "SELECT count(id) AS amount FROM " + tabla + ";";
                ResultSet rs = stmt.executeQuery(queryLastId);
                int ammount = rs.getInt("amount");
                if (ammount == 0) {
                    sigId = 1;
                } else {
                    queryLastId = "SELECT id FROM " + tabla + ";";
                    rs = stmt.executeQuery(queryLastId);
                    while (rs.next()) {
                        if (rs.getInt("id") > sigId) {
                            sigId = rs.getInt("id");
                        }
                    }
                    sigId = sigId + 1;
                }
            }
            if (sigId < 0) {
                Exception miExcepcion = new Exception("Error de ID");
                throw miExcepcion;
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'obtenerSiguienteId': " + ex.getMessage(), "ERROR DE DATOS");
        } catch (Exception ex) {
            vista.desplegarMensajeError("Error 'obtenerSiguienteId': " + ex.getMessage(), "ERROR DE DATOS");
        }
        return sigId;
    }

    public String obtenerCampos(int orden) {
        String elementosMatriz = " ";
        switch (orden) {
            //Acorde al orden se obtienen los elementos de la porcion triangular superior de la matriz, sin incluir los 1
            case 3:
                elementosMatriz += "a12, a13, a23, vp1, vp2, vp3 ";
                break;
            case 4:
                elementosMatriz += "a12, a13, a14, a23, a24, a34, vp1, vp2, vp3, vp4 ";
                break;
            case 5:
                elementosMatriz += "a12, a13, a14, a15, a23, a24, a25, a34, a35, a45, vp1, vp2, vp3, vp4, vp5 ";
                break;
            case 6:
                elementosMatriz += "a12, a13, a14, a15, a16, a23, a24, a25, a26, a34, a35, a36, a45, a46, a56, vp1, vp2, vp3, vp4, vp5, vp6 ";
                break;
            case 7:
                elementosMatriz += "a12, a13, a14, a15, a16, a17, a23, a24, a25, a26, a27, a34, a35, a36, a37, a45, a46, a47, a56, a57, a67, vp1, vp2, vp3, vp4, vp5, vp6, vp7 ";
                break;
            case 8:
                elementosMatriz += "a12, a13, a14, a15, a16, a17, a18, a23, a24, a25, a26, a27, a28, a34, a35, a36, a37, a38, a45, a46, a47, a48, a56, a57, a58, a67, a68, a78, vp1, vp2, vp3, vp4, vp5, vp6, vp7, vp8 ";
                break;
        }
        return elementosMatriz;
    }

    public String obtenerCamposHistorico(int orden) {
        String elementosMatriz = " ";
        switch (orden) {
            //Acorde al orden se obtienen los elementos de la porcion triangular superior de la matriz, sin incluir los 1
            case 3:
                elementosMatriz += "a12, a13, a23 ";
                break;
            case 4:
                elementosMatriz += "a12, a13, a14, a23, a24, a34 ";
                break;
            case 5:
                elementosMatriz += "a12, a13, a14, a15, a23, a24, a25, a34, a35, a45 ";
                break;
            case 6:
                elementosMatriz += "a12, a13, a14, a15, a16, a23, a24, a25, a26, a34, a35, a36, a45, a46, a56 ";
                break;
            case 7:
                elementosMatriz += "a12, a13, a14, a15, a16, a17, a23, a24, a25, a26, a27, a34, a35, a36, a37, a45, a46, a47, a56, a57, a67 ";
                break;
            case 8:
                elementosMatriz += "a12, a13, a14, a15, a16, a17, a18, a23, a24, a25, a26, a27, a28, a34, a35, a36, a37, a38, a45, a46, a47, a48, a56, a57, a58, a67, a68, a78 ";
                break;
        }
        return elementosMatriz;
    }

    public String generarInsert(int id, int orden, AHPmatriz matriz, int tipo, int criterio) {
        String insert = "INSERT INTO matriz_ponderaciones ( id, ";
        insert += obtenerCampos(orden);
        insert += ", ci, ri, cr, tipo_matriz, criterio) VALUES ( " + id + ",";
        for (int f = 0; f < matriz.getMainMatrix().getRowDimension(); f++) {
            for (int c = 0; c < matriz.getMainMatrix().getColumnDimension(); c++) {
                if (f != c && c > f) {
                    insert += matriz.getMainMatrix().get(f, c) + ", ";
                }
            }
        }
        //Vector de Prioridades de la Matriz de Criterios
        for (int f = 0; f < matriz.getPriorityVector().getRowDimension(); f++) {
            insert += matriz.getPriorityVector().get(f, 0) + ", ";
        }
        insert += matriz.getCi() + ", ";
        insert += matriz.getRi() + ", ";
        insert += matriz.getCr() + ", ";
        insert += tipo + ", ";
        insert += criterio + " );";
        return insert;
    }

    public void persistirHistorico(int id, AHPmatriz matrizHistorica, int criterio) {
        try {
            String insert = "INSERT INTO matriz_ponderaciones_historial ( id, ";
            insert += obtenerCamposHistorico(matrizHistorica.getMainMatrix().getColumnDimension());
            insert += ", cr, tipo_matriz, criterio, tipo_metodo_usado, fecha_creacion) VALUES ( " + id + ",";
            for (int f = 0; f < matrizHistorica.getMainMatrix().getRowDimension(); f++) {
                for (int c = 0; c < matrizHistorica.getMainMatrix().getColumnDimension(); c++) {
                    if (f != c && c > f) {
                        insert += matrizHistorica.getMainMatrix().get(f, c) + ", ";
                    }
                }
            }
            insert += matrizHistorica.getCr() + ", ";
            insert += matrizHistorica.getTipoMatriz() + ", ";
            insert += criterio + ", ";
            insert += matrizHistorica.getMetodoMLusado() + ", ";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String fechaCreacion = dateFormat.format(matrizHistorica.getFechaCreacion().getTime());
            insert += "'" + fechaCreacion + "' );";
            System.out.println(insert);
            Statement stmt = conn.createStatement();
            stmt.execute(insert);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'persistirHistorico': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public void actualizarHistorico(int id, AHPmatriz matrizHistorica, int criterio) {
        try {
            //ACTUALIZAR EL VECTOR DE PRIORIDADES DE LA MATRIZ DE CRITERIOS
            Statement stmt = conn.createStatement();
            String update = "UPDATE matriz_ponderaciones_historial SET ";
            //Matriz de Ponderacion de Alternativas por cada Criterio
            for (int f = 0; f < matrizHistorica.getMainMatrix().getRowDimension(); f++) {
                for (int c = 0; c < matrizHistorica.getMainMatrix().getColumnDimension(); c++) {
                    if (f != c && c > f) {
                        String elemento = "a" + (f + 1) + (c + 1) + " = ";
                        update += elemento + matrizHistorica.getMainMatrix().get(f, c) + ", ";
                    }
                }
            }
            update += " cr = " + matrizHistorica.getCr() + ", ";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String fechaUltimaModifiacion = dateFormat.format(matrizHistorica.getFechaUltimaModificacion().getTime());
            update += " fecha_ultima_modificacion = '" + fechaUltimaModifiacion + "' ";
            update += " WHERE (id = " + id + " AND criterio = " + criterio + ");";
            stmt.executeUpdate(update);
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'actualizarHistorico': " + ex.getMessage(), "ERROR DE DATOS");
        }
    }

    public boolean buscarExistenciaHistorico(int id, AHPmatriz matrizHistorica, int criterio) {
        boolean exito = false;
        try {
            String query = "SELECT id FROM matriz_ponderaciones_historial WHERE (id = " + id + " AND criterio = " + criterio + ");";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                exito = true;
            }
        } catch (SQLException ex) {
            vista.desplegarMensajeError("Error BD 'buscarExistencia': " + ex.getMessage(), "ERROR DE DATOS");
        }
        return exito;
    }

}
