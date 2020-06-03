package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ContentPanel extends JPanel {
    public ContentPanel() {
        setLayout(new CardLayout());
//        ClockLabel clock = new ClockLabel();
//        clock.setPreferredSize(new Dimension((int)clock.getPreferredSize().getWidth(),26));
//        add(clock, BorderLayout.NORTH);
//        JPanel centro = new JPanel(new BorderLayout());
//        add(centro);
//        centro.setBackground(Color.white);
//        setOpaque(true);
//        System.out.println(clock.getPreferredSize().toString());

    }

}

class ClockLabel extends JLabel implements ActionListener {

    public ClockLabel() {

        super(LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))+"  ",RIGHT);
        Timer t = new Timer(1000, this);
        t.start();
        setFont(new Font("Sengoe UI", Font.BOLD,18));
    }

    public void actionPerformed(ActionEvent ae) {
        setText(LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) +"  ");
    }
}
