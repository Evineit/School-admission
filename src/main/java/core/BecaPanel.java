package core;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.NumberFormat;
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
    JFormattedTextField flatAmount;
    NumberFormat percentFormat;
    NumberFormat flatFormat;
    JFormattedTextField discount;
    JFormattedTextField totalAmount;
    JCheckBox bestowCBox;
    JButton nextButton = new JButton("Siguiente");
    JButton cancelButton = new JButton("Cancelar");

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
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
                if (!validateTextFields()) {
                    JOptionPane.showMessageDialog(null,"No se puede continuar, revise los campos",
                            "Información",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (!cost.equals(totalAmount)&& bestowCBox.isSelected() && isDiscountValid()) {
                    try {
                        int idSShip = SqlService.registerScholarship(idAdmission, idStudent, Integer.parseInt(percent.getText()), Double.parseDouble(flatAmount.getText()));
                        mainWindow.changePayment(idAdmission, idSShip);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
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
        bestowCBox.addActionListener(e -> {
            if (bestowCBox.isSelected()){
                percent.setEnabled(true);
                flatAmount.setEnabled(true);
                calculateDiscount();
            }else if (!bestowCBox.isSelected()){
                percent.setEnabled(false);
                flatAmount.setEnabled(false);
                totalAmount.setText(cost.getText());
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
        bestowCBox.setEnabled(false);
        calculateDiscount();

        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
    private boolean validateTextFields() {
        if (percent != null){
            if (isEmpty(percent)){
                return false;
            }
        }
        if (flatAmount != null){
            if (isEmpty(percent)){
                return false;
            }
        }
        try{
            Double.parseDouble(flatAmount.getText());
            flatAmount.setBackground(Color.white);
        }catch (NumberFormatException e){
            flatAmount.setBackground(new Color(250,230,230));
            return false;
        }
        return true;

    }
    private boolean isDiscountValid() {
        assert flatAmount != null;
        if (Double.parseDouble(flatAmount.getText())==0 && Double.parseDouble(percent.getText())==0){
            return false;
        }
        return true;
    }
    private boolean isEmpty(Component c){
        JTextField text = (JTextField) c;
        return text.getText().strip().isEmpty();
    }
    private void calculateDiscount() {
        if (flatAmount.getText().strip().isEmpty()) return;
        if (percent.getText().strip().isEmpty()) return;
        try {
            double cost = Double.parseDouble(this.cost.getText());
            double flatDiscount = Double.parseDouble(flatAmount.getText());
            double percent = Double.parseDouble(this.percent.getText());
            double d = (cost - flatDiscount);
            d -= d * (percent / 100);
            discount.setValue(cost - d);
            totalAmount.setValue(d);
        }catch (NumberFormatException e){
//            Toolkit.getDefaultToolkit().beep();

        }
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
        percent.setEnabled(false);
        flatAmount.setEnabled(false);
//        percent.setInputVerifier(new InputVerifier() {
//            @Override
//            public boolean verify(JComponent c) {
//                    boolean verified = false;
//                    JTextField textField = (JTextField) c;
//                    try {
//                        Double.parseDouble(textField.getText());
//                        verified = true;
//                    } catch (NumberFormatException e) {
//                        UIManager.getLookAndFeel().provideErrorFeedback(c);
//                        //Toolkit.getDefaultToolkit().beep();
//                    }
//                    return verified;
//
//            }
//        });
    }

    private void setContents() {
        ArrayList<String> studentList = SqlService.getStudent(idStudent);
        ArrayList<String> admissionList = SqlService.getAdmission(idAdmission);
        assert admissionList != null;
        int grade = Integer.parseInt(admissionList.get(2));
        assert studentList != null;
        name.setText(studentList.get(1));
        lName1.setText(studentList.get(7));
        lName2.setText(studentList.get(5));
        if (grade <= 6) {
            level.setText(levels[0]);
            if (grade <= 3) {
                cost.setText(String.valueOf(prices[0]));
            } else {
                cost.setText(String.valueOf(prices[1]));
            }
        } else if (grade <= 9) {
            grade-=6;
            level.setText(levels[1]);
            cost.setText(String.valueOf(prices[2]));

        } else {
            grade-=9;
            level.setText(levels[2]);
            cost.setText(String.valueOf(prices[3]));
        }
        gradeField.setText(String.valueOf(grade));
        ((AbstractDocument) percent.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
        percent.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent c) {
                boolean verified = false;
                JTextField textField = (JTextField) c;
                try {
                    if (Integer.parseInt(textField.getText())>100){
                        textField.setText("100");
                        UIManager.getLookAndFeel().provideErrorFeedback(c);
                    }
                    verified = true;
                    c.setBackground(Color.white);
                } catch (NumberFormatException e) {
                    c.setBackground(new Color(255,230,230));
                    UIManager.getLookAndFeel().provideErrorFeedback(c);
                    //Toolkit.getDefaultToolkit().beep();
                }
                return verified;
            }
        });
        percent.setText("0");
        flatAmount.setText("0.00");
        flatAmount.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent c) {
                boolean verified = false;
                JFormattedTextField textField = (JFormattedTextField) c;
                try {
                    if (Double.parseDouble(textField.getText())>Integer.parseInt(cost.getText())){
                        textField.setValue(Double.parseDouble(cost.getText()));
                        UIManager.getLookAndFeel().provideErrorFeedback(c);
                    }
                    verified = true;
                    c.setBackground(Color.white);
                } catch (NumberFormatException e) {
                    c.setBackground(new Color(255,230,230));
                    UIManager.getLookAndFeel().provideErrorFeedback(c);
                    //Toolkit.getDefaultToolkit().beep();
                }
                return verified;
            }
        });
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
        setFormat();
        percent = new JTextField();
        flatAmount = new JFormattedTextField(flatFormat);
        discount = new JFormattedTextField(flatFormat);
        totalAmount = new JFormattedTextField(flatFormat);
        bestowCBox = new JCheckBox("Otorgar beca de inscripción");

//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(gridBagLayout);
        addToPanel(new JLabel("Nombre:"),1,1,0);
        addToPanel(new JLabel("Apellido Paterno:"),2,1,0);
        addToPanel(new JLabel("Apellido Materno:"),3,1,0);
        addToPanel(new JLabel("Grado:"),4,1,0);
        addToPanel(new JLabel("Nivel:"),5,1,0);
        addToPanel(new JLabel("Costo:"),6,1,0);
//        addToPanel(new JLabel(":"),1,1,0);
        addToPanel(new JLabel("Beca en porcentaje:"),8,1,0);
        addToPanel(new JLabel("Beca en cantidad :"),9,1,0);
        addToPanel(new JLabel("Descuento:"),10,1,0);
        addToPanel(new JLabel("Cantidad Total:"),11,1,0);

        addToPanel(name,1,1,0);
        addToPanel(lName1,2,1,0);
        addToPanel(lName2,3,1,0);
        addToPanel(gradeField,4,1,0);
        addToPanel(level,5,1,0);
        addToPanel(cost,6,1,0);
        addToPanel(bestowCBox,7,1,0);
        addToPanel(percent,8,1,0);
        addToPanel(flatAmount,9,1,0);
        addToPanel(discount,10,1,0);
        addToPanel(totalAmount,11,1,0);
        addToPanel(cancelButton,12,1,0);
        addToPanel(nextButton,12,1,1);
        nextButton.setBackground(Color.white);
        cancelButton.setBackground(Color.white);
        setOpaque(true);
        setBackground(Color.white);
        bestowCBox.setBackground(Color.white);
        // TODO: 06/06/2020 improve colors while disabled
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
    void addToPanel(JComponent component, int gridy, int width, int weightx) {
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = weightx;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = width;
        constraints.gridy = gridy;
        add(component, constraints);
    }
    private void setFormat() {
        percentFormat = NumberFormat.getNumberInstance();
        flatFormat = NumberFormat.getNumberInstance();
        percentFormat.setGroupingUsed(false);
        percentFormat.setMaximumFractionDigits(0);
        percentFormat.setMaximumIntegerDigits(3);
        flatFormat.setGroupingUsed(false);
        flatFormat.setMinimumFractionDigits(2);
        flatFormat.setMaximumFractionDigits(2);
        flatFormat.setMaximumIntegerDigits(4);
    }
}