package core;

import javax.swing.*;
import java.awt.*;
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

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();

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
                    if (!validateTextFields()) {
                        JOptionPane.showMessageDialog(null,"No se puede continuar, existen campos vacíos",
                                "Información",JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
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
                    mainWindow.changeAlumnos();
                }
            }
        });
    }

    private boolean validateTextFields() {
        for (Component c :
                getComponents()) {
            if (c instanceof JTextField){
                if (isEmpty(c)){
                    return false;
                }
            }
        }
        return true;

    }
    private boolean isEmpty(Component c){
        JTextField text = (JTextField) c;
        return text.getText().strip().isEmpty();
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
        // TODO: 06/06/2020 disable grade and level 
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (hasChanged()){
                    if (!validateTextFields()) {
                        JOptionPane.showMessageDialog(null,"No se puede continuar, existen campos vacíos",
                                "Información",JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
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
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = JOptionPane.showConfirmDialog(null,"¿Esta seguro de que desea salir?",
                        "Cancelar inscripción",JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION){
                    mainWindow.changeAlumnos();
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
        setOpaque(true);
        setBackground(Color.WHITE);
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
        JLabel nameLabel = new JLabel("Nombre:");
        JLabel ape1Label = new JLabel("Apellido Paterno:");
        JLabel ape2Label = new JLabel("Apellido Materno:");
        JLabel ageLabel = new JLabel("Edad:");
        JLabel gradeLabel = new JLabel("Grado:");
        JLabel addressLabel = new JLabel("Dirección");
        JLabel tutor1Label= new JLabel("Nombre:");
        JLabel tutor2Label = new JLabel("Apellido:");
        JLabel tutor3Label = new JLabel("RFC:");
        JLabel tutor4Label = new JLabel("Teléfono:");

        setLayout(gridBagLayout);
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addToPanel(studentLabel,0,GridBagConstraints.REMAINDER,0);
        addToPanel(nameLabel,1,1,0);
        addToPanel(name,1,1,1);
        addToPanel(ape1Label,2,1,0);
        addToPanel(lNameP,2,1,0);
        addToPanel(ape2Label,3,1,0);
        addToPanel(lNameM,3,1,0);
        addToPanel(ageLabel,4,1,0);
        addToPanel(age,4,1,0);
        addToPanel(addressLabel,5,1,0);
        addToPanel(address,5,1,0);
        addToPanel(gradeLabel,6,1,0);
        addToPanel(grade,6,1,0);
        addToPanel(tutor,7,GridBagConstraints.REMAINDER,0);
        addToPanel(tutor1Label,8,1,0);
        addToPanel(tutorName,8,1,0);
        addToPanel(tutor2Label,9,1,0);
        addToPanel(tutorLName,9,1,0);
        addToPanel(tutor3Label,10,1,0);
        addToPanel(tutorRfc,10,1,0);
        addToPanel(tutor4Label,11,1,0);
        addToPanel(tutorPhone,11,1,0);
        addToPanel(cancelButton,12,1,0);
        addToPanel(nextButton,12,1,0);

        grade.setBackground(Color.white);
        grade.setRenderer(new DefaultListCellRenderer(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
        });
        nextButton.setBackground(Color.white);
        cancelButton.setBackground(Color.white);
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
    //TODO  Move to SQLService
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
    void addToPanel(JComponent component, int gridy, int width, int weightx) {
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = weightx;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = width;
        constraints.gridy = gridy;
        add(component, constraints);
    }
}
