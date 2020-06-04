package core;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class PaymentPanel extends JPanel {
    private int admissionId;
    private int studentId;

    JTextField name;
    JTextField lName1;
    JTextField lName2;
    JTextField registration;
    JTextField gradeField;
    JTextField level;
    JTextField cost;
    JTextField percent;
    JTextField flatAmount;
    JTextField discount;
    JTextArea details;
    JTextField totalAmount;
    JButton nextButton = new JButton("Siguiente");
    JButton cancelButton = new JButton("Cancelar");
    String[] levels = {
            "Primaria",
            "Secundaria",
            "Preparatoria"
    };
    int[] prices = {
            1500,
            2000,
            2500,
            3000
    };

    public PaymentPanel(MainWindow mainWindow, int idAdmission) {
        setAdmissionId(idAdmission);
        initElements();
        setContents();
        disableComponents();
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nextLogic(mainWindow);
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

    public void nextLogic(MainWindow mainWindow) {
        //Todo confirm
        try {
            SqlService.registerPayment(admissionId,studentId, Double.parseDouble(totalAmount.getText()),details.getText());
            // TODO: 03/06/2020 Doesnt update table
            mainWindow.changeAlumnos();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            mainWindow.rollback();
        }
    }

    public PaymentPanel() {

    }

    public PaymentPanel(MainWindow mainWindow, int idAdmission, int idSShip) {
        this(mainWindow,idAdmission);
        calculateDiscount(idSShip);
    }


    private void calculateDiscount(int idSShip){
        ArrayList<String> scholarShip = SqlService.getScholarship(idSShip);
        // TODO: 03/06/2020 assert flatDiscount and percent are non negative, percent is between 0,100
        int cost = Integer.parseInt(this.cost.getText());
        double flatDiscount = Double.parseDouble(scholarShip.get(4));
        double percent = Double.parseDouble(scholarShip.get(3));
        double d = (cost - flatDiscount);
        d -= d * (percent / 100);
        this.percent.setText(scholarShip.get((3)));
        this.flatAmount.setText(scholarShip.get(4));

        this.discount.setText(String.valueOf(cost - d));
        this.totalAmount.setText(String.valueOf(d));
    }

    private void disableComponents(){
        name.setEnabled(false);
        lName1.setEnabled(false);
        lName2.setEnabled(false);
        registration.setEnabled(false);
        level.setEnabled(false);
        gradeField.setEnabled(false);
        cost.setEnabled(false);
        percent.setEnabled(false);
        flatAmount.setEnabled(false);
        discount.setEnabled(false);
        totalAmount.setEnabled(false);
    }
    private void setContents() {
        ArrayList<String> admissionList = SqlService.getAdmission(admissionId);
        studentId = Integer.parseInt(admissionList.get(1));
        ArrayList<String> studentList = SqlService.getAlumno(studentId);
        int grade = Integer.parseInt(admissionList.get(2));
        name.setText(studentList.get(1));
        lName1.setText(studentList.get(7));
        lName2.setText(studentList.get(5));
        registration.setText(studentList.get(6));
        //Todo grado por nivel
        gradeField.setText(String.valueOf(grade));
        if (grade<=6){
            level.setText(levels[0]);
            if (grade<=3){
                cost.setText(String.valueOf(prices[0]));
            }else {
                cost.setText(String.valueOf(prices[1]));
            }
        }else if (grade<=9){
            level.setText(levels[1]);
            cost.setText(String.valueOf(prices[2]));

        }else {
            level.setText(levels[2]);
            cost.setText(String.valueOf(prices[3]));
        }
        //Todo get beca por id
        percent.setText("0");
        flatAmount.setText("0");
        discount.setText("0");
        totalAmount.setText(cost.getText());


    }
    private void initElements() {
        name = new JTextField();
        lName1 = new JTextField();
        lName2 = new JTextField();
        registration = new JTextField();
        level = new JTextField();
        gradeField = new JTextField();
        cost = new JTextField();
        percent = new JTextField();
        flatAmount = new JTextField();
        discount = new JTextField();
        details = new JTextArea();
        details.setLineWrap(true);
        totalAmount = new JTextField();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(name);
        add(lName1);
        add(lName2);
        add(registration);
        add(level);
        add(gradeField);
        add(cost);
        add(percent);
        add(flatAmount);
        add(discount);
        add(details);
        add(totalAmount);
        add(nextButton);
        add(cancelButton);


    }

    public void setAdmissionId(int admissionId) {
        this.admissionId = admissionId;
    }
}
