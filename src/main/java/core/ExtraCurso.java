package core;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//todo primaria
public class ExtraCurso extends JPanel {
    String extraCurs[] = {"Ajedrez","Futbol"};
    String talleres[] = {"Electricidad","Carpintería"};
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
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (grade>=10){
            add(new JLabel("Selección de extracurricular"));
            list = new JList<>(extraCurs);
        }else {
            add(new JLabel("Selección de talleres"));
            list = new JList<>(extraCurs);
        }
        add(new JScrollPane(list));

        add(nextButton);
        add(cancelButton);
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                insertInscripcion();
                mainWindow.changeBeca(studentId,admissionId);
            }
        });
    }

    public ExtraCurso() {

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
