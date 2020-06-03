package core;

import javax.swing.*;

public class App extends JFrame {
    public App() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-debug")) {
                try {
                    SwingUtilities.invokeLater(new MainWindow()::iniciar);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } else {
            try {
                SwingUtilities.invokeLater(new LoginWindow()::iniciar);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
