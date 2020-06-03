package core;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtraCurso extends JPanel {
    String data[] = {"Ajedrez","Futbol"};
    JButton nextButton = new JButton("Siguiente");
    JButton cancelButton = new JButton("Cancelar");
    private Connection connection = SqlService.getConnection();
    private int studentId;
    private int studentGrade;
    JList<String> list;
    private int admissionId;


    public ExtraCurso(MainWindow mainWindow) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Extracurricular"));
        list = new JList<>(data);
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

    private void insertInscripcion() {
        try {

            PreparedStatement insertInsc = connection.prepareStatement("Insert Into inscripciones (ID_ALUMNO, GRADO, EXTRACLASE, INSC_FECHA)"+
                    "values (?,?,?,now())");
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
