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
    private final JButton editButton = new JButton("Editar");
    private final JButton deleteButton = new JButton("Borrar");
    DefaultTableModel miModelo;
    private JTable table1 = new JTable();
    JTextField searchField = new JTextField();
    Connection con = SqlService.getConnection();
    JButton addButton = new JButton("Agregar");
    private MainWindow mainWindow;
    String[] sortingMode = {
            "Informacion General"
    };
    private final JComboBox<String> comboBox = new JComboBox<>(sortingMode);


    public alumnosPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        JPanel temporal = new JPanel();
        temporal.add(addButton);
        temporal.add(editButton);
        temporal.add(deleteButton);
        temporal.add(new JTextField(10));
        temporal.add(new JButton("Buscar"));
        temporal.add(comboBox);
        add(temporal, BorderLayout.NORTH);
        add(new JScrollPane(table1), BorderLayout.CENTER);
        initTabla();

        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alumnosPanel.this.mainWindow.addAlumno();
            }
        });
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                modifyStudent();
            }
        });
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                deleteStudent();
            }
        });
        
    }



    public void initTabla() {
        initTabla("");
    }

    private void initTabla(String id) {
        miModelo = new DefaultTableModel();
        miModelo.addColumn("Id Alumno");
        miModelo.addColumn("Nombre");
        miModelo.addColumn("Apellido P");
        miModelo.addColumn("Apellido M");
        miModelo.addColumn("Edad");
        miModelo.addColumn("Dirección");
        miModelo.addColumn("Id Tutor");
        miModelo.addColumn("Matricula");
        table1.setModel(miModelo);

        String[] datos = new String[8];

//        String sql ="Select * from empleado";
        String sql;
        if (id.equals("")) {
            sql = "Select * from alumnos";
            sortTable();
        } else {
            sql = "Select * from alumnos where ID_ALUMNO = '" + id + "'";
        }

        try {
            Statement sentencia = con.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
                datos[3] = rs.getString(4);
                datos[4] = rs.getString(5);
                datos[5] = rs.getString(6);
                datos[6] = rs.getString(7);
                datos[7] = rs.getString(8);
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sortTable() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table1.getModel());
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
        table1.setRowSorter(sorter);
    }
    private void modifyStudent() {
        int row;
        row=table1.getSelectedRow();
        if (row==-1 || comboBox.getSelectedIndex()!=0){
            JOptionPane.showMessageDialog(null, "No hay estudiante seleccionado");
        }else{
            mainWindow.editAlumno(Integer.parseInt((String) table1.getValueAt(row,0)));
        }
    }
    private void deleteStudent() {
        int row;
        row=table1.getSelectedRow();
        if (row==-1 || comboBox.getSelectedIndex()!=0){
            JOptionPane.showMessageDialog(null, "No hay estudiante seleccionado");
        }else{
            if (JOptionPane.YES_NO_OPTION==JOptionPane.showConfirmDialog(null,
                    "Se borra el alumno y toda la información relacionada " +
                    "¿Esta seguro de que desea borrar este alumno?","Advertencia",JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE)) {
                try {
                    SqlService.startTransaction();
                    SqlService.removeStudent(Integer.parseInt((String) table1.getValueAt(row,0)));
                    // TODO: 04/06/2020 appears even if it fails 
                    initTabla();
                    JOptionPane.showMessageDialog(null,"Se ha borrado con exito");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    mainWindow.rollback();
                }
            }
        }
    }

}
