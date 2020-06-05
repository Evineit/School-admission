package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

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
    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();


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
        setLayout(gridBagLayout);

        if (grade>=10){
            addToPanel(new JLabel("Selección de extracurricular"),0,GridBagConstraints.REMAINDER,0,0);
            list = new JList<>(extraCurs);
        }else {
            addToPanel(new JLabel("Selección de talleres"),0,GridBagConstraints.REMAINDER,0,0);
            list = new JList<>(talleres);
        }
        addToPanel(new JScrollPane(list),1,GridBagConstraints.REMAINDER,1,1);

        addToPanel(cancelButton,2,1,0,0);
        addToPanel(nextButton,2,1,0,0);

        nextButton.setBackground(Color.white);
        cancelButton.setBackground(Color.white);
    }

    public ExtraCurso() {

    }

    public ExtraCurso(MainWindow mainWindow, int idAdmission) {
        ArrayList<String> admissionList = SqlService.getAdmission(idAdmission);
        // TODO: 04/06/2020 assert not null
        assert admissionList != null;
        setStudentGrade(Integer.parseInt(admissionList.get(2)));
        init(studentGrade);
        int studentID = Integer.parseInt(admissionList.get(1));
        setContents(admissionList);
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (hasChanged(admissionList)){
                    int ans = JOptionPane.showConfirmDialog(null,"Se han detectado cambios, desea conservarlos?",
                            "Cambios detectados",JOptionPane.YES_NO_OPTION);
                    if (ans==JOptionPane.YES_OPTION){
                        SqlService.updateAdmission(idAdmission,studentGrade,list.getSelectedValue());
                    }
                }
                final int idSShip = SqlService.getSSByStudent(studentID);
                if (idSShip==-1){
                    mainWindow.showPayment(idAdmission);
                }
                else {
                    mainWindow.changeBeca(idSShip);
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

    private void setContents(ArrayList<String> extraList) {
        if (studentGrade>=10){
            ArrayList<String> checkList = new ArrayList<>(Arrays.asList(extraCurs));
            int i = checkList.indexOf(extraList.get(3));
            list.setSelectedIndex(i);
        }else {
            ArrayList<String> checkList = new ArrayList<>(Arrays.asList(talleres));
            int i = checkList.indexOf(extraList.get(3));
            list.setSelectedIndex(i);
        }
    }

    private boolean hasChanged(ArrayList<String> extraList) {
        boolean flag= false;
        if (studentGrade>=10){
            ArrayList<String> checkList = new ArrayList<>(Arrays.asList(extraCurs));
            int i = checkList.indexOf(extraList.get(3));
            if (i!=list.getSelectedIndex()){
                flag =true;
            }
        }else {
            ArrayList<String> checkList = new ArrayList<>(Arrays.asList(talleres));
            int i = checkList.indexOf(extraList.get(3));
            if (i!=list.getSelectedIndex()){
                flag =true;
            }
        }
        return flag;
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
    void addToPanel(JComponent component, int gridy, int width, int weightx,int weighty) {
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = width;
        constraints.gridy = gridy;
        add(component, constraints);
    }
}
