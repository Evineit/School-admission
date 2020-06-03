package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginWindow extends JFrame {
    private JPanel mainPanel;
    private JButton login;
    private JComboBox<String> usersCombo = new JComboBox();
    private JLabel passLabel;
    private JPasswordField passField;

    public LoginWindow() {
        iniciarLogin();
    }

    private void iniciarLogin() {
//        Font oldLabelFont = UIManager.getFont("Label.font");
//        UIManager.put("Label.font", new Font("Segoe UI",Font.BOLD,14));
        Image icon = new ImageIcon("src/Resource/dental-ico2.png").getImage();
        setIconImage(icon);
        setMinimumSize(new Dimension(800,600));
        getContentPane().setBackground(Color.decode("#5689C2"));
        setLayout(new GridBagLayout());
        GridBagConstraints mainLimites = new GridBagConstraints();
        mainLimites.fill= GridBagConstraints.VERTICAL;
        mainLimites.weighty=100;
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(true);
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints limites = new GridBagConstraints();
        limites.fill = GridBagConstraints.HORIZONTAL;
        limites.insets = new Insets(10,70,10,70);
        limites.ipadx = 60;
//        setContentPane(mainPanel);
        add(mainPanel,mainLimites);
        login = new JButton("Iniciar Sesión");
        login.setFont(new Font("Segoe UI", Font.BOLD,18));
        login.setBackground(Color.white);
        JLabel usuario = new JLabel("Usuario");
        usuario.setFont(new Font("Segoe UI Semibold", Font.PLAIN,18));
//        for (String user: usuarios.getListUsers()){
//            usersCombo.addItem(user);
//        }
        usersCombo.addItem("Administrador");

        usersCombo.setBackground(Color.white);
        usersCombo.setRenderer(new DefaultListCellRenderer(){
            @Override
            public void paint(Graphics g) {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
                super.paint(g);
            }
        });
        passLabel = new JLabel("Contraseña");
        passLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN,18));
        passField = new JPasswordField();
        passField.requestFocus();
        mainPanel.add(usuario,limites);
        limites.gridy=1;
        mainPanel.add(usersCombo,limites);
        limites.gridy=2;
        mainPanel.add(passLabel,limites);
        limites.gridy=3;
        mainPanel.add(passField,limites);
        limites.gridy=4;
        mainPanel.add(login,limites);

        login.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try{
                    validarLogin();
                }catch (Exception error){
                }
            }
        });
        login.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                login.setBackground(Color.decode("#5689C2"));
                login.setForeground(Color.white);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                login.setBackground(Color.white);
                login.setForeground(Color.black);
            }
        });
        passField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    validarLogin();
                }catch (Exception error){
                }
            }
        });
//        usersCombo.requestFocusInWindow();
        setTitle("Iniciar Sesión");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void iniciar(){
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            this.setLocationByPlatform(true);
        } catch (Throwable ignoreAndContinue) {
        }
        setLocationRelativeTo(null);
        setVisible(true);
        passField.requestFocusInWindow();

    }
    public void validarLogin(){
        String selected = (String) usersCombo.getSelectedItem();
        char[] pass = passField.getPassword();
        if ("1234".equals(new String(pass))){
            SwingUtilities.invokeLater(new MainWindow()::iniciar);
            this.dispose();
        }else{
            JOptionPane.showMessageDialog(null,"Contraseña incorrecta",
                    "Error", JOptionPane.ERROR_MESSAGE);
            passField.setText("");
        }


    }

}
