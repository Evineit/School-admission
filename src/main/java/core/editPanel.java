package core;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;

public class editPanel extends JPanel {
    private final Connection connection;
    JLabel studentLabel = new JLabel("Datos del estudiante");
    JTextField name;
    JTextField lNameP;
    JTextField lNameM;
    JTextField age;
    JTextField address;
    JComboBox<String> grade;
    JLabel tutor = new JLabel("Tutor");
    JTextField tutorName;
    JTextField tutorLName;
    JTextField tutorRfc;
    JTextField tutorPhone;
    JButton nextButton = new JButton("Siguiente");
    JButton cancelButton = new JButton("Cancelar");
    String[] grades = {
            "1-Primaria",
            "2-Primaria",
            "3-Primaria",
            "4-Primaria",
            "5-Primaria",
            "6-Primaria",
            "1-Secundaria",
            "2-Secundaria",
            "3-Secundaria",
            "1-Preparatoria",
            "2-Preparatoria",
            "3-Preparatoria"
    };

    public editPanel(MainWindow mainWindow) {
        initElements();
        connection = SqlService.getConnection();
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int id = addStudent();
                    mainWindow.changeExtra(id,grade.getSelectedIndex()+1);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    connection.rollback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    private void initElements() {
        name = new JTextField();
        lNameM = new JTextField();
        lNameP = new JTextField();
        age = new JTextField();
        address = new JTextField();
        grade = new JComboBox<>(grades);
        tutorName = new JTextField();
        tutorLName = new JTextField();
        tutorPhone = new JTextField();
        tutorRfc = new JTextField();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(studentLabel);
        add(name);
        add(lNameP);
        add(lNameM);
        add(age);
        add(address);
        add(grade);
        add(tutor);
        add(tutorName);
        add(tutorLName);
        add(tutorRfc);
        add(tutorPhone);
        add(nextButton);
        add(cancelButton);
    }

    private void addTutor() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("Insert into tutores (TUTO_NOMBRE, TUTO_APELLIDO, TUTO_RFC, TUTO_TELEFONO)" +
                "values (?,?,?,?)");
        preparedStatement.setString(1, tutorName.getText());
        preparedStatement.setString(2, tutorLName.getText());
        preparedStatement.setString(3, tutorRfc.getText());
        preparedStatement.setString(4, tutorRfc.getText());
        preparedStatement.executeUpdate();


    }

    public int addStudent() throws SQLException {
        int idStudent=0;
        try {
            final String queryAI = "SELECT `auto_increment` FROM INFORMATION_SCHEMA.TABLES\n" +
                    "WHERE table_name = 'alumnos'";
            ResultSet resultSet = connection.createStatement().executeQuery(queryAI);
            int nextAI = 0;
            while (resultSet.next()) {
                nextAI = resultSet.getInt(1);
            }
            SqlService.startTransaction();
            addTutor();
            PreparedStatement insertStudent = connection.prepareStatement("insert into alumnos (ALUM_NOMBRE, ALUM_EDAD, ALUM_DIRECCION, ID_TUTOR, ALUM_APELLIDO_M, MATRICULA, ALUM_APELLIDO_P) " +
                    "values (?,?,?,last_insert_id(),?,?,?)");
            insertStudent.setString(1, name.getText());
            insertStudent.setString(2, age.getText());
            insertStudent.setString(3, address.getText());
            insertStudent.setString(4, lNameM.getText());
            insertStudent.setInt(5, genMatricula(nextAI));
            insertStudent.setString(6, lNameP.getText());
            insertStudent.executeUpdate();

            final String queryLI = "SELECT last_insert_id()";
            ResultSet resultLI = connection.createStatement().executeQuery(queryLI);
            while (resultLI.next()) {
                idStudent = resultLI.getInt(1);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return idStudent;
    }
    private int genMatricula(int nextAI){
        LocalDate localDate = LocalDate.now();
        int year = Integer.parseInt(String.valueOf(localDate.getYear()).substring(2));
        int month = Integer.parseInt(String.valueOf(localDate.getMonthValue()));
        month*=10_000;
        year*=1_000_000;
        year+=month;
        return year+nextAI;
    }
}
