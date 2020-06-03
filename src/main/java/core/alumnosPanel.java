package core;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class alumnosPanel extends JPanel {
    DefaultTableModel miModelo;
    private JTable table1 = new JTable();
    JTextField searchField = new JTextField();
    Connection con = SqlService.getConnection();

    JButton addButton = new JButton("Agregar");
    private MainWindow parentPanel;


    public alumnosPanel(MainWindow mainWindow) {
        parentPanel = mainWindow;
        setLayout(new BorderLayout());
        JPanel temporal = new JPanel();
        temporal.add(addButton);
        temporal.add(new JButton("Editar"));
        temporal.add(new JButton("Borrar"));
        temporal.add(new JTextField(10));
        temporal.add(new JButton("Buscar"));
        temporal.add(new JComboBox<String>());
        add(temporal,BorderLayout.NORTH);
        add(new JScrollPane(table1),BorderLayout.CENTER);
        initTabla();

        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentPanel.addAlumno();
            }
        });
    }

    private void initTabla() {
        initTabla("");
    }
    private void initTabla(String id){
        miModelo = new DefaultTableModel();
        miModelo.addColumn("Id_granjero");
        miModelo.addColumn("Nombre");
        miModelo.addColumn("Teléfono");
        miModelo.addColumn("Correo");
        miModelo.addColumn("Facebook");
        miModelo.addColumn("Cultivo");
        table1.setModel(miModelo);

        String[] datos = new String[6];

//        String sql ="Select * from empleado";
        String sql;
        if (id.equals("")){
            sql ="Select * from alumnos";
            sortTable();
        }else{
            sql = "Select * from alumnos where ID_ALUMNO = '" + id + "'";
        }

        try{
            Statement sentencia= con.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            while(rs.next()){
                datos[0]=rs.getString(1);
                datos[1]=rs.getString(2);
                datos[2]=rs.getString(3);
                datos[3]=rs.getString(4);
                datos[4]=rs.getString(5);
                datos[5]=rs.getString(6);
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        }catch(SQLException e ){
            e.printStackTrace();
        }
    }

    private void sortTable() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table1.getModel());
        sorter.setRowFilter(RowFilter.regexFilter("(?i)"+searchField.getText()));
        table1.setRowSorter(sorter);
    }

}
