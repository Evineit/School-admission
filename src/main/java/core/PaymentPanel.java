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
                //Todo confirm
                try {
                    SqlService.registerPayment(admissionId,studentId, Double.parseDouble(totalAmount.getText()),details.getText());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    try {
                        SqlService.getConnection().rollback();
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            }
        });


    }

    public PaymentPanel() {

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
            cost.setText(String.valueOf(prices[3]));

        }else {
            level.setText(levels[2]);
            cost.setText(String.valueOf(prices[4]));
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
