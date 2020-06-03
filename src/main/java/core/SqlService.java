/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author kevin
 */
public class SqlService {

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            System.out.println("\n\n***** MySQL JDBC Connection Testing *****");
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/priv_school", "root", "");
            } catch (Exception ex) {
                System.err.println("Cannot connect to database server");
                ex.printStackTrace();
//        } finally {
//            if (conn != null) {
//                try {
//                    System.out.println("\n***** Let terminate the Connection *****");
//                    conn.close();
//                    System.out.println("\nDatabase connection terminated...");
//                } catch (Exception ex) {
//                    System.out.println("Error in connection termination!");
//                }
//            }
//        }
            }
        }

        return connection;
    }

    public static void startTransaction() throws SQLException {
        if (connection != null) {
            connection.createStatement().executeQuery("START TRANSACTION ");
        }
    }

    public static ArrayList<String> getAlumno(int id) {
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM alumnos WHERE ID_ALUMNO = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, id);
            ResultSet resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
                list.add(resultSet.getString(2));
                list.add(resultSet.getString(3));
                list.add(resultSet.getString(4));
                list.add(resultSet.getString(5));
                list.add(resultSet.getString(6));
                list.add(resultSet.getString(7));
                list.add(resultSet.getString(8));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ArrayList<String> getAdmission(int id) {
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM inscripciones WHERE ID_INSCRIPCION = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, id);
            ResultSet resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
                list.add(resultSet.getString(2));
                list.add(resultSet.getString(3));
                final int grade = Integer.parseInt(list.get(2));
                if (grade >= 10){
                    list.add(resultSet.getString(4));
                } else if (grade >= 7) {
                    list.add(resultSet.getString(5));
                }
                list.add(resultSet.getString(6));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static int registerScholarship(){
        return 0;
    }

    public static void registerPayment(int admissionId, int studentId, double amount,String details) throws SQLException {
        PreparedStatement insertStudent = connection.prepareStatement("insert into pagos_incripciones (ID_INSCRIPCION, ID_ALUMNO, CANTIDAD_PAGO, OTROS_DETALLES) " +
                "values (?,?,?,?)");
        insertStudent.setInt(1, admissionId);
        insertStudent.setInt(2, studentId);
        insertStudent.setBigDecimal(3, BigDecimal.valueOf(amount));
        insertStudent.setString(4, details);
        insertStudent.executeUpdate();
        connection.commit();
        connection.setAutoCommit(true);

    }
    public static int getLastID(){
        int reto = 0;
        try {
            final String queryLI = "SELECT last_insert_id()";
            ResultSet resultLI = connection.createStatement().executeQuery(queryLI);
            while (resultLI.next()) {
                reto =  resultLI.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return reto;
    }
}
