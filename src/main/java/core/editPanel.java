package core;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class editPanel extends JPanel {
    private final Connection connection = SqlService.getConnection();
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
    private int studentID;

    public editPanel(MainWindow mainWindow) {
        initElements();
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int studentID = addStudent();
                    final int grade = editPanel.this.grade.getSelectedIndex() + 1;
                    if (grade<=6){
                        int inscID = SqlService.insertInscription(studentID,grade);
                        mainWindow.changeBeca(studentID,inscID);
                    }else {
                        mainWindow.changeExtra(studentID, grade);
                    }
                } catch (SQLException throwables) {
                    mainWindow.rollback();
                    throwables.printStackTrace();
                }
            }
        });
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = JOptionPane.showConfirmDialog(null,"¿Esta seguro de que desea cancelar el proceso de inscripción?",
                        "Cancelar inscripción",JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION){
                    mainWindow.rollback();
                }
            }
        });
    }

    public editPanel() {

    }

    public editPanel(MainWindow mainWindow, int studentID) {
        this.studentID = studentID;
        ArrayList<String> studentList = SqlService.getStudent(studentID);
        assert studentList != null;
        final int idTutor = Integer.parseInt(studentList.get(6));
        ArrayList<String> tutorList = SqlService.getTutor(idTutor);
        initElements();
        setContents(studentList,tutorList);
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (hasChanged()){
                    // TODO: 03/06/2020 update this if something change
                    // TODO: 04/06/2020 assert not empty fields
                    int ans = JOptionPane.showConfirmDialog(null,"Se han detectado cambios, desea conservarlos?",
                            "Cambios detectados",JOptionPane.YES_NO_OPTION);
                    if (ans==JOptionPane.YES_OPTION){
                        try {
                            SqlService.startTransaction();
                            SqlService.updateStudent(studentID,name.getText(),lNameP.getText(),
                                    lNameM.getText(), Integer.parseInt(age.getText()),address.getText());
                            SqlService.updateTutor(idTutor,tutorName.getText(),tutorLName.getText(),
                                    tutorRfc.getText(),tutorPhone.getText());
                            SqlService.getConnection().commit();
                            JOptionPane.showMessageDialog(null,"Cambios guardados");
                        } catch (NumberFormatException | SQLException numberFormatException) {
                            numberFormatException.printStackTrace();
                            try {
                                SqlService.getConnection().rollback();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    }
                }
                final int grade = editPanel.this.grade.getSelectedIndex() + 1;
                final int idSShip = SqlService.getSSByStudent(studentID);
                final int idAdmission = SqlService.getIdAdmission(studentID);
                if (grade<=6){
                    if (idSShip==-1){
                        mainWindow.showPayment(idAdmission);
                    }
                    else {
                        mainWindow.changeBeca(idSShip);
                    }

                }else {
                    mainWindow.changeExtra(idAdmission);
                }
            }
        });
    }
    private boolean hasChanged(){
        //todo reduce this
        ArrayList<String> studentList = SqlService.getStudent(studentID);
        assert studentList != null;
        ArrayList<String> tutorList = SqlService.getTutor(Integer.parseInt(studentList.get(6)));
        boolean flag = false;
        if (!name.getText().equals(studentList.get(1))){
            flag=true;
        }else if (!lNameP.getText().equals(studentList.get(2))){
            flag=true;
        }else if (!lNameM.getText().equals(studentList.get(3))){
            flag=true;
        }else if (!age.getText().equals(studentList.get(4))){
            flag=true;
        }else if (!address.getText().equals(studentList.get(5))){
            flag=true;
        }else if (!tutorName.getText().equals(tutorList.get(1))){
            flag=true;
        }else if (!tutorLName.getText().equals(tutorList.get(2))){
            flag=true;
        }else if (!tutorRfc.getText().equals(tutorList.get(3))){
            flag=true;
        }else if (!tutorPhone.getText().equals(tutorList.get(4))){
            flag=true;
        }else if (grade.getSelectedIndex()!=SqlService.getGrade(studentID)){
            flag=true;
        }

        return flag;
    }

    private void setContents(ArrayList<String> studentList, ArrayList<String> tutorList) {

        name.setText(studentList.get(1));
        lNameP.setText(studentList.get(2));
        lNameM.setText(studentList.get(3));
        age.setText(studentList.get(4));
        address.setText(studentList.get(5));
        tutorName.setText(tutorList.get(1));
        tutorLName.setText(tutorList.get(2));
        tutorRfc.setText(tutorList.get(3));
        tutorPhone.setText(tutorList.get(4));
        grade.setSelectedIndex(SqlService.getGrade(studentID));
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
        //TODO Validate Phone
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
        // TODO: 03/06/2020 check for existing parent / reuse parents
        // TODO: 03/06/2020 move to sql service
        PreparedStatement preparedStatement = connection.prepareStatement("Insert into tutores (TUTO_NOMBRE, TUTO_APELLIDO, TUTO_RFC, TUTO_TELEFONO)" +
                "values (?,?,?,?)");
        preparedStatement.setString(1, tutorName.getText());
        preparedStatement.setString(2, tutorLName.getText());
        preparedStatement.setString(3, tutorRfc.getText());
        preparedStatement.setString(4, tutorPhone.getText());
        preparedStatement.executeUpdate();


    }
    //TODO assert fields not empty
    // - Move to SQLService
    //    - Remove Sql exeption
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
            // TODO: 03/06/2020 replace with service fun
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
