/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

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
    public static void login(String p) throws SQLException {
        if (connection == null) {
            System.out.println("\n\n***** MySQL JDBC Connection Testing *****");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/priv_school", "schoolAdmin", p);
        }
    }

    public static void startTransaction() {
        if (connection != null) {
            try {
                connection.createStatement().executeQuery("START TRANSACTION ");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    public static void updateStudent(int idStudent,String name,String ap1,String ap2,int age,String dir){
        final String query = "update alumnos set ALUM_NOMBRE=?,ALUM_APELLIDO_P=?," +
                "ALUM_APELLIDO_M=?,ALUM_EDAD=?,ALUM_DIRECCION=? WHERE ID_ALUMNO = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, name );
            pStatement.setString(2, ap1);
            pStatement.setString(3, ap2);
            pStatement.setInt(4, age);
            pStatement.setString(5, dir);
            pStatement.setInt(6, idStudent);
            int i = pStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getStudent(int idStudent) {
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM alumnos WHERE ID_ALUMNO = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idStudent);
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
    public static int getIdAdmission(int idStudent){
        final String query = "SELECT * FROM inscripciones WHERE ID_ALUMNO = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idStudent);
            ResultSet resultSet = pStatement.executeQuery();
            int idInsc = 0;
            while (resultSet.next()) {
                idInsc = resultSet.getInt(1);
            }
            return idInsc;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static ArrayList<String> getPayment(int idInscription){
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM pagos_incripciones WHERE ID_INSCRIPCION = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idInscription);
            ResultSet resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
                list.add(resultSet.getString(2));
                list.add(resultSet.getString(3));
                list.add(resultSet.getString(4));
                list.add(resultSet.getString(5));

            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ArrayList<String> getAdmission(int idAdmission) {
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM inscripciones WHERE ID_INSCRIPCION = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idAdmission);
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
    public static ArrayList<String> getScholarship(int idScholarship) {
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM becas WHERE ID_BECA = ?";
        return getStrings(list, idScholarship, query);
    }
    public static int getSSByStudent(int idStudent) {
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM becas WHERE ID_ALUMNO = ?";
        list = getStrings(list, idStudent, query);
        if (list == null || list.isEmpty()){
            return -1;
        }else {
            return Integer.parseInt(list.get(0));
        }
    }
    public static int getGrade(int studentID) {
        final String query = "SELECT * FROM inscripciones WHERE ID_ALUMNO = ?";
        int grade = 0;
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, studentID);
            ResultSet resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                grade = Integer.parseInt(resultSet.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grade;
    }
    public static ArrayList<String> getTutor(int id){
        ArrayList<String> list = new ArrayList<>();
        final String query = "SELECT * FROM tutores WHERE ID_TUTOR = ?";
        return getStrings(list, id, query);
    }

    private static ArrayList<String> getStrings(ArrayList<String> list, int id, String query) {
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, id);
            ResultSet resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
                list.add(resultSet.getString(2));
                list.add(resultSet.getString(3));
                list.add(resultSet.getString(4));
                list.add(resultSet.getString(5));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int registerScholarship(int admissionId, int studentId,int percent, double flat) throws SQLException {
        int idScholarship;
        PreparedStatement insertStudent = connection.prepareStatement("insert into becas (ID_ALUMNO, ID_INSCRIPCION, PORCENTAJE, CANTIDAD) " +
                "values (?,?,?,?)");
        insertStudent.setInt(1, studentId);
        insertStudent.setInt(2, admissionId);
        insertStudent.setInt(3, percent);
        insertStudent.setBigDecimal(4, BigDecimal.valueOf(flat));
        insertStudent.executeUpdate();
        idScholarship = SqlService.getLastID();
        return idScholarship;
    }

    public static void registerPayment(int admissionId, int studentId, double amount,String details) throws SQLException {
        PreparedStatement insertPayment = connection.prepareStatement("insert into pagos_incripciones (ID_INSCRIPCION, ID_ALUMNO, CANTIDAD_PAGO, OTROS_DETALLES) " +
                "values (?,?,?,?)");
        insertPayment.setInt(1, admissionId);
        insertPayment.setInt(2, studentId);
        insertPayment.setBigDecimal(3, BigDecimal.valueOf(amount));
        insertPayment.setString(4, details);
        insertPayment.executeUpdate();
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
    public static int insertInscription(int studentId, int studentGrade) {
        int admissionId = 0;
        try {
            String query;
            query = "Insert Into inscripciones (ID_ALUMNO, GRADO, INSC_FECHA) " +
                    "values (?,?,now())";

            PreparedStatement insertInsc = connection.prepareStatement(query);
            insertInsc.setInt(1, studentId);
            insertInsc.setInt(2, studentGrade);
            insertInsc.executeUpdate();
            admissionId = SqlService.getLastID();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return admissionId;
    }

    public static void updateTutor(int idTutor, String text, String text1, String text2, String text3) {
        final String query = "update tutores set TUTO_NOMBRE=?,TUTO_APELLIDO=?," +
                "TUTO_RFC=?,TUTO_TELEFONO=? WHERE ID_TUTOR = ?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, text );
            pStatement.setString(2, text1);
            pStatement.setString(3, text2);
            pStatement.setString(4, text3);
            pStatement.setInt(5, idTutor);
            int i = pStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void updateAdmission(int idAdmission,int grade,String newValue) {
        String query;
        if (grade >= 10) {
            query = "update inscripciones set EXTRACLASE=?" +
                    " WHERE ID_INSCRIPCION = ?";
        }else {
            query = "update inscripciones set TALLER=?" +
                    " WHERE ID_INSCRIPCION = ?";
        }
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, newValue);
            pStatement.setInt(2, idAdmission);
            int i = pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeStudent(int idStudent) throws SQLException {
        int idAdmission = getIdAdmission(idStudent);
        int idShip = getSSByStudent(idStudent);
        int idParent = Integer.parseInt(Objects.requireNonNull(getStudent(idStudent)).get(6));
        deletePayment(idAdmission);
        if (idShip!=-1) {
            deleteScholarship(idAdmission);
        }
        deleteAdmission(idAdmission);
        deleteStudent(idStudent);
        deleteTutor(idParent);
        getConnection().commit();

    }

    private static void deleteTutor(int idParent) {
        final String query = "delete from tutores where ID_TUTOR=?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idParent );
            int affectedRows = pStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void deleteStudent(int idStudent) {
        final String query = "delete from alumnos where ID_ALUMNO=?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idStudent );
            int affectedRows = pStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAdmission(int idAdmission) {
        final String query = "delete from inscripciones where ID_INSCRIPCION=?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idAdmission );
            int affectedRows = pStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteScholarship(int idAdmission) {
        final String query = "delete from becas where ID_INSCRIPCION=?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idAdmission );
            int affectedRows = pStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void deletePayment(int idAdmission) {
        final String query = "delete from pagos_incripciones where ID_INSCRIPCION=?";
        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, idAdmission );
            int affectedRows = pStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
