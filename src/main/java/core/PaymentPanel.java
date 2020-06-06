package core;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;

public class PaymentPanel extends JPanel implements Printable {
    private int admissionId;
    private int studentId;


    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    JTextField name;
    JTextField registry;
    JTextField tutorId;
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
    public static int[] prices = {
            1500,
            2000,
            2500,
            3000
    };

    public PaymentPanel(MainWindow mainWindow, int idAdmission) {
        setAdmissionId(idAdmission);
        initElements();
        setContents();
        enableComponents(false);
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nextLogic(mainWindow);
            }
        });
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cancelLogic(mainWindow);
            }
        });


    }

    public void cancelLogic(MainWindow mainWindow) {
        int i = JOptionPane.showConfirmDialog(null,"¿Esta seguro de que desea cancelar el proceso de inscripción?",
                "Cancelar inscripción",JOptionPane.YES_NO_OPTION);
        if (i == JOptionPane.YES_OPTION){
            mainWindow.rollback();
        }
    }

    public void print(){
        nextButton.setVisible(false);
        cancelButton.setVisible(false);
        enableComponents(true);
        setOpaque(true);
        setBackground(Color.white);
        Container c = this;
        BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        c.paint(im.getGraphics());
        try {
            ImageIO.write(im, "PNG", new File(registry.getText()+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(null,"Desea imprimir un comprobante","Imprimir",JOptionPane.YES_NO_OPTION)){
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            boolean ok = job.printDialog();
            if (ok) {
                try {
                    job.print();
                } catch (PrinterException ex) {
                    /* The job did not successfully complete */
                }
            }
        }
    }


    public void nextLogic(MainWindow mainWindow) {
        try {
            int ans = JOptionPane.showConfirmDialog(null,"¿Esta seguro de que desea guardar la " +
                            "inscripción del alumno con los datos ingresados?",
                    "Cambios detectados",JOptionPane.YES_NO_OPTION);
            if (ans==JOptionPane.YES_OPTION){
                SqlService.registerPayment(admissionId,studentId, Double.parseDouble(totalAmount.getText()),details.getText());
                print();
                mainWindow.changeAlumnos();
                // TODO: 05/06/2020 table doesnt update
            }
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
        // TODO: 05/06/2020 if checkbox is not selected remove discount on total amount
        // TODO: 06/06/2020 fix showing non formatted text
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

    private void enableComponents(boolean bool){
        name.setEnabled(bool);
        registry.setEnabled(bool);
        tutorId.setEnabled(bool);
        level.setEnabled(bool);
        gradeField.setEnabled(bool);
        cost.setEnabled(bool);
        percent.setEnabled(bool);
        flatAmount.setEnabled(bool);
        discount.setEnabled(bool);
        totalAmount.setEnabled(bool);
    }
    private void setContents() {
        ArrayList<String> admissionList = SqlService.getAdmission(admissionId);
        studentId = Integer.parseInt(admissionList.get(1));
        ArrayList<String> studentList = SqlService.getStudent(studentId);
        int grade = Integer.parseInt(admissionList.get(2));
        name.setText(studentList.get(1)+" "+studentList.get(2)+" "+ studentList.get(3));
        registry.setText(studentList.get(7));
        tutorId.setText(studentList.get(6));
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
        registry = new JTextField();
        tutorId = new JTextField();
        level = new JTextField();
        gradeField = new JTextField();
        cost = new JTextField();
        percent = new JTextField();
        flatAmount = new JTextField();
        discount = new JTextField();
        details = new JTextArea();
        details.setLineWrap(true);
        totalAmount = new JTextField();


        setLayout(gridBagLayout);
        addToPanel(new JLabel("Pago de inscripción"),0,1,0);
        addToPanel(new JLabel("Nombre:"),1,1,0);
        addToPanel(new JLabel("Matricula:"),2,1,0);
        addToPanel(new JLabel("Nivel:"),3,1,0);
        addToPanel(new JLabel("Grado:"),4,1,0);
        addToPanel(new JLabel("Costo:"),5,1,0);
        addToPanel(new JLabel("Beca en porcentaje:"),6,1,0);
        addToPanel(new JLabel("Beca en cantidad :"),7,1,0);
        addToPanel(new JLabel("Descuento:"),8,1,0);
        addToPanel(new JLabel("Información adicional:"),9,1,0);
        addToPanel(new JLabel("Cantidad Total:"),10,1,0);

        addToPanel(name,1,1,0);
        addToPanel(registry,2,1,0);
        addToPanel(level,3,1,0);
        addToPanel(gradeField,4,1,0);
        addToPanel(cost,5,1,0);
        addToPanel(percent,6,1,0);
        addToPanel(flatAmount,7,1,0);
        addToPanel(discount,8,1,0);
        addToPanel(details,9,1,0);
        addToPanel(totalAmount,10,1,0);
        addToPanel(cancelButton,11,1,0);
        addToPanel(nextButton,11,1,1);

        nextButton.setBackground(Color.white);
        cancelButton.setBackground(Color.white);
        setOpaque(true);
        setBackground(Color.white);

    }

    public void setAdmissionId(int admissionId) {
        this.admissionId = admissionId;
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

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        /* Now print the window and its visible contents */
        this.printAll(graphics);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
}
