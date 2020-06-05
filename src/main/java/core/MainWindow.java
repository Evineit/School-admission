package core;

import gui.LeftPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;


public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private ContentPanel contentPanel;
    private alumnosPanel alumnos;
    private BecaPanel becaPanel;
    private editPanel editAlumno;
    private CardLayout cl;
    private LeftPanel leftPanel;
    private GridBagConstraints leftLimit;
    private GridBagConstraints rightLimit;
    ExtraCurso extraPanel;
    PaymentPanel payPanel;



    //TODO: Validar todo
    public MainWindow() {
        alumnos = new alumnosPanel(this);
        extraPanel = new ExtraCurso();
        becaPanel = new BecaPanel();
        payPanel = new PaymentPanel();
        editAlumno = new editPanel();
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
        iniciarUI();
    }
    private void iniciarUI(){
//        Image icon = new ImageIcon("src/Resource/dental-ico2.png").getImage();
//        setIconImage(icon);
        setMinimumSize(new Dimension(1000,600));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        leftLimit = new GridBagConstraints();
        leftLimit.fill = GridBagConstraints.BOTH;
        leftLimit.weighty = 100;
        leftLimit.gridwidth=1;
        leftLimit.weightx = 0;
        leftLimit.ipadx=10;

        rightLimit = new GridBagConstraints();
        rightLimit.fill = GridBagConstraints.BOTH;
        rightLimit.gridx = 1;
        rightLimit.weighty =100;
        rightLimit.gridwidth=3;
        rightLimit.weightx = 100;
        rightLimit.insets = new Insets(1,1,1,1);

        leftPanel = new LeftPanel();

        leftPanel.addSwitch("Alumnos");
//        leftPanel.addSwitch("Becas");

        mainPanel.add(leftPanel,leftLimit);
        contentPanel = new ContentPanel();
        contentPanel.setLayout(new CardLayout());
        mainPanel.add(this.contentPanel,rightLimit);
        contentPanel.add(alumnos,"Card1");
        contentPanel.add(editAlumno,"Card2");
        contentPanel.add(extraPanel,"Card3");
        contentPanel.add(becaPanel,"Card4");
        contentPanel.add(payPanel,"Card5");

//        contentPanel.add(becas,"Card3");
        cl = (CardLayout) contentPanel.getLayout();
        leftPanel.switches.get(0).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(contentPanel, "Card1");
                leftPanel.changeFocus(0);
            }
        });
//        leftPanel.switches.get(1).addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                cl.show(contentPanel, "Card3");
//                leftPanel.changeFocus(1);
//            }
//        });
        setTitle("Admisión escolar");
        setContentPane(mainPanel);
        setVisible(true);

    }
    public void iniciar(){
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        try {
//
//            this.setLocationByPlatform(true);
//        } catch (Throwable ignoreAndContinue) {
//        }
        leftPanel.changeFocus(0);
//        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void addAlumno(){
        editAlumno = new editPanel(this);
        contentPanel.add(editAlumno,"Card2");
        cl.show(contentPanel, "Card2");
    }
    public void editAlumno(int idStudent) {
        editAlumno = new editPanel(this,idStudent);
        contentPanel.add(editAlumno,"Card2");
        cl.show(contentPanel, "Card2");
    }
    public void changeAlumnos(){
        alumnos.initTabla();
        cl.show(contentPanel, "Card1");
    }
    public void changeExtra(int id, int grade){
        extraPanel = new ExtraCurso(this,id,grade);
        contentPanel.add(extraPanel,"Card3");
        cl.show(contentPanel, "Card3");
    }

    public void changeExtra(int idAdmission) {
        extraPanel = new ExtraCurso(this,idAdmission);
        contentPanel.add(extraPanel,"Card3");
        cl.show(contentPanel, "Card3");
    }

    public void changeBeca(int idStudent, int idInscrip){
        becaPanel = new BecaPanel(this,idStudent,idInscrip);
        contentPanel.add(becaPanel,"Card4");
        cl.show(contentPanel, "Card4");
    }

    public void changeBeca(int idBeca) {
        becaPanel = new BecaPanel(this,idBeca);
        contentPanel.add(becaPanel,"Card4");
        cl.show(contentPanel, "Card4");
    }


    public void changePayment(int idAdmission) {
        payPanel = new PaymentPanel(this,idAdmission);
        contentPanel.add(payPanel,"Card5");
        cl.show(contentPanel, "Card5");
    }
    public void showPayment(int idAdmission) {
        payPanel = new PaymentPanel(this,idAdmission){
            @Override
            public void nextLogic(MainWindow mainWindow) {
                this.print();
                mainWindow.changeAlumnos();

            }
        };
        contentPanel.add(payPanel,"Card5");
        cl.show(contentPanel, "Card5");
    }

    public void showPayment(int idAdmission, int idBeca) {
        payPanel = new PaymentPanel(this,idAdmission,idBeca){
            @Override
            public void nextLogic(MainWindow mainWindow) {
                mainWindow.changeAlumnos();
            }
        };
        contentPanel.add(payPanel,"Card5");
        cl.show(contentPanel, "Card5");
    }


    public void changePayment(int idAdmission, int idSShip) {
        payPanel = new PaymentPanel(this,idAdmission,idSShip);
        contentPanel.add(payPanel,"Card5");
        cl.show(contentPanel, "Card5");
    }

    public void rollback() {
        try {
            SqlService.getConnection().rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            changeAlumnos();
        }
    }
}
