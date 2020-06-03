package core;

//import core.HiloEsperaNotifica;
import gui.LeftPanel;
import gui.SwitchPanel;
import gui.ui.Button;
import gui.ui.StopwatchPanel;
//import gui.ui.spritePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WindowMain {
    private JPanel mainPanel;
    private ContentPanel content;
    private JPanel stopwatch;
    private JPanel teacher;
    private JPanel fire;
    private JPanel animation;
    private LeftPanel leftPanel1;
    private JTextArea textArea1;
    private Button teachButton;
    private Button setFire;
    private boolean BooleanFire = false;

    public WindowMain() {
        animation.setLayout(new BorderLayout());
//        animation.add(new spritePanel());
        stopwatch.setLayout(new BorderLayout());
        stopwatch.add(new StopwatchPanel());
        leftPanel1.addSwitch("Cronometro");
        leftPanel1.addSwitch("Clase en espera");
        leftPanel1.addSwitch("Alarma fuego");
        leftPanel1.addSwitch("Animación");
        changeFocus(0);

        CardLayout cl = (CardLayout) content.getLayout();
        leftPanel1.switches.get(0).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(content, "Card1");
                changeFocus(0);
            }
        });
        leftPanel1.switches.get(1).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(content, "Card2");
                changeFocus(1);


            }
        });
        leftPanel1.switches.get(2).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(content, "Card3");
                changeFocus(2);


            }
        });
        leftPanel1.switches.get(3).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                cl.show(content, "Card4");
                changeFocus(3);


            }
        });

//        teachButton.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        teachButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textArea1.setText("");
//                new HiloEsperaNotifica().setOut(textArea1);
            }
        });

        new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (BooleanFire)
                    fire.setBackground(Color.red);
                else fire.setBackground(Color.black);
            }
        }).start();
        setFire.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BooleanFire= !BooleanFire;

            }
        });
    }

    private void changeFocus(int i) {
        leftPanel1.switches.forEach(x -> x.setBackground(LeftPanel.BGCOLOR));
        leftPanel1.switches.get(i).setBackground(SwitchPanel.COLORSEL);
    }

    public void iniciar() {
        JFrame frame = new JFrame("Hilosu Test");
        frame.setContentPane(new WindowMain().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new WindowMain()::iniciar);
    }

}

