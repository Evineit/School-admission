package core;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

//todo primaria
public class ExtraCurso extends JPanel {
    String[] extraCurs = {"Ajedrez","Futbol"};
    String[] talleres = {"Electricidad","Carpintería"};
    JButton nextButton = new JButton("Siguiente");
    JButton cancelButton = new JButton("Cancelar");
    private Connection connection = SqlService.getConnection();
    private int studentId;
    private int studentGrade;
    JList<String> list;
    private int admissionId;


    public ExtraCurso(MainWindow mainWindow, int id, int grade) {
        setStudentId(id);
        setStudentGrade(grade);
        init(grade);
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                insertInscripcion();
                mainWindow.changeBeca(studentId,admissionId);
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

    private void init(int grade) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (grade>=10){
            add(new JLabel("Selección de extracurricular"));
            list = new JList<>(extraCurs);
        }else {
            add(new JLabel("Selección de talleres"));
            list = new JList<>(talleres);
        }
        add(new JScrollPane(list));

        add(nextButton);
        add(cancelButton);
    }

    public ExtraCurso() {

    }

    public ExtraCurso(MainWindow mainWindow, int idAdmission) {
        ArrayList<String> list = SqlService.getAdmission(idAdmission);

        init(SqlService.getGrade(Integer.parseInt(list.get(2))));

        int studentID = Integer.parseInt(list.get(1));
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (hasChanged()){

                }else {
                    final int idSShip = SqlService.getSSByStudent(studentID);
                    if (idSShip==-1){
                        // TODO: 04/06/2020 finish this
//                        mainWindow.changePayment();
                    }else {
                        mainWindow.changePayment(idAdmission,idSShip);
                    }
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

    private boolean hasChanged() {
        // TODO: 03/06/2020 finish this add logic
        return false;
    }


    private void insertInscripcion() {
        try {
            String query;
            if (studentGrade>=10){
                query = "Insert Into inscripciones (ID_ALUMNO, GRADO, EXTRACLASE, INSC_FECHA)" +
                        "values (?,?,?,now())";
            }else {
                query = "Insert Into inscripciones (ID_ALUMNO, GRADO, TALLER, INSC_FECHA)" +
                        "values (?,?,?,now())";
            }
            PreparedStatement insertInsc = connection.prepareStatement(query);
            insertInsc.setInt(1, studentId);
            insertInsc.setInt(2, studentGrade);
            insertInsc.setString(3,list.getSelectedValue());
            insertInsc.executeUpdate();
            admissionId = SqlService.getLastID();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setStudentGrade(int studentGrade) {
        this.studentGrade = studentGrade;
    }
}
