package core;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class BecaPanel extends JPanel {
    private int idStudent;
    private int idAdmission;
    JTextField name;
    JTextField lName1;
    JTextField lName2;
    JTextField gradeField;
    JTextField level;
    JTextField cost;
    JTextField percent;
    JTextField flatAmount;
    JTextField discount;
    JTextField totalAmount;
    JCheckBox bestow;
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
    private int idBeca;

    public BecaPanel() {
    }

    public BecaPanel(MainWindow mainWindow, int idStudent, int idInscrip) {
        this.idStudent = idStudent;
        this.idAdmission = idInscrip;
        initElements();
        setContents();
        disableComponents();
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Todo assert not empty
                //todo register scholarship based on fields
                if (!cost.equals(totalAmount)) {
                    try {
                        int idSShip = SqlService.registerScholarship(idAdmission, idStudent, Integer.parseInt(percent.getText()), Double.parseDouble(flatAmount.getText()));
                        mainWindow.changePayment(idAdmission, idSShip);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        //Todo error rollback
                        mainWindow.rollback();
                    }
                } else {
                    mainWindow.changePayment(idAdmission);
                }
            }
        });
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "¿Esta seguro de que desea cancelar el proceso de inscripción?",
                        "Cancelar inscripción", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) {
                    mainWindow.rollback();
                }
            }
        });
        addChangeListener(percent, e -> calculateDiscount());
        addChangeListener(flatAmount, e -> calculateDiscount());


    }

    public BecaPanel(MainWindow mainWindow, int idBeca) {
        this.idBeca = idBeca;
        ArrayList<String> list = SqlService.getScholarship(idBeca);
        idStudent = Integer.parseInt(list.get(1));
        idAdmission = Integer.parseInt(list.get(2));
        initElements();
        setContents();
        disableComponents();
        percent.setText(list.get(3));
        flatAmount.setText(list.get(4));
        percent.setEnabled(false);
        flatAmount.setEnabled(false);
        bestow.setEnabled(false);
        calculateDiscount();

        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Todo assert not empty
                //todo register scholarship based on fields
                mainWindow.showPayment(idAdmission,idBeca);
            }
        });
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "¿Esta seguro de que desea cancelar el proceso de inscripción?",
                        "Cancelar inscripción", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) {
                    mainWindow.rollback();
                }
            }
        });
    }

    private void calculateDiscount() {
        // TODO: 03/06/2020 assert flatdiscount and percent are non negative, percent is between 0,100
        // TODO: 03/06/2020 double and format on discount and total amount
        // TODO: 03/06/2020 beca checkbox
        double cost = Double.parseDouble(this.cost.getText());
        double flatDiscount = Double.parseDouble(flatAmount.getText());
        double percent = Double.parseDouble(this.percent.getText());
        double d = (cost - flatDiscount);
        d -= d * (percent / 100);
        discount.setText(String.valueOf(cost - d));
        totalAmount.setText(String.valueOf(d));
    }

    private void disableComponents() {
        name.setEnabled(false);
        lName1.setEnabled(false);
        lName2.setEnabled(false);
        level.setEnabled(false);
        cost.setEnabled(false);
        gradeField.setEnabled(false);
        discount.setEnabled(false);
        totalAmount.setEnabled(false);
    }

    private void setContents() {
        ArrayList<String> studentList = SqlService.getAlumno(idStudent);
        ArrayList<String> admissionList = SqlService.getAdmission(idAdmission);
        int grade = Integer.parseInt(admissionList.get(2));
        name.setText(studentList.get(1));
        lName1.setText(studentList.get(7));
        lName2.setText(studentList.get(5));
        gradeField.setText(String.valueOf(grade));
        if (grade <= 6) {
            level.setText(levels[0]);
            if (grade <= 3) {
                cost.setText(String.valueOf(prices[0]));
            } else {
                cost.setText(String.valueOf(prices[1]));
            }
        } else if (grade <= 9) {
            level.setText(levels[1]);
            cost.setText(String.valueOf(prices[2]));

        } else {
            level.setText(levels[2]);
            cost.setText(String.valueOf(prices[3]));
        }
        percent.setText("0");
        flatAmount.setText("0");
        discount.setText("0");
        totalAmount.setText(cost.getText());


    }

    private void initElements() {
        name = new JTextField();
        lName1 = new JTextField();
        lName2 = new JTextField();
        gradeField = new JTextField();
        level = new JTextField();
        cost = new JTextField();
        percent = new JTextField();
        flatAmount = new JTextField();
        discount = new JTextField();
        totalAmount = new JTextField();
        bestow = new JCheckBox("Otorgar beca de inscripción");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(name);
        add(lName1);
        add(lName2);
        add(gradeField);
        add(level);
        add(cost);
        add(bestow);
        add(percent);
        add(flatAmount);
        add(discount);
        add(totalAmount);
        add(nextButton);
        add(cancelButton);


    }

    public void setIdAdmission(int idAdmission) {
        this.idAdmission = idAdmission;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    /**
     * Installs a listener to receive notification when the text of any
     * {@code JTextComponent} is changed. Internally, it installs a
     * {@link DocumentListener} on the text component's {@link Document},
     * and a {@link PropertyChangeListener} on the text component to detect
     * if the {@code Document} itself is replaced.
     *
     * @param text           any text component, such as a {@link JTextField}
     *                       or {@link JTextArea}
     * @param changeListener a listener to receieve {@link ChangeEvent}s
     *                       when the text is changed; the source object for the events
     *                       will be the text component
     * @throws NullPointerException if either parameter is null
     */
    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };
        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document) e.getOldValue();
            Document d2 = (Document) e.getNewValue();
            if (d1 != null) d1.removeDocumentListener(dl);
            if (d2 != null) d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null) d.addDocumentListener(dl);
    }
}
