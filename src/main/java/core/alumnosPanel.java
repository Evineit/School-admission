package core;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class alumnosPanel extends JPanel {
    private final JButton editButton = new JButton("Editar");
    private final JButton deleteButton = new JButton("Borrar");
    private final JButton buscarButton = new JButton("Buscar");
    private final JTextField searchField = new JTextField(11);

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    DefaultTableModel miModelo;
    private JTable table1 = new JTable(){
        @Override public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    Connection con = SqlService.getConnection();
    JButton addButton = new JButton("Agregar");
    private MainWindow mainWindow;
    String[] sortingMode = {
            "Información General",
            "Información Tutores",
            "Estudiantes Primaria",
            "Estudiantes Secundaria",
            "Estudiantes Preparatoria",
            "Becas",
            "Pagos"
    };
    private final JComboBox<String> modeComboBox = new JComboBox<>(sortingMode);


    public alumnosPanel(MainWindow mainWindow) {

        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        JPanel temporal = new JPanel();
        temporal.setLayout(gridBagLayout);
        addToPanel(temporal,addButton,1,1,0);
        addToPanel(temporal,editButton,1,1,0);
        addToPanel(temporal,deleteButton,1,1,0);
        addToPanel(temporal,searchField,1,1,1);
        addToPanel(temporal,buscarButton,1,1,0);
        addToPanel(temporal, modeComboBox,1,1,0,1);
        add(temporal, BorderLayout.NORTH);
        add(new JScrollPane(table1), BorderLayout.CENTER);
        initTabla();
        addButton.setBackground(Color.white);
        editButton.setBackground(Color.white);
        buscarButton.setBackground(Color.white);
        deleteButton.setBackground(Color.white);
        table1.getTableHeader().setFont(new Font("Dialog",Font.BOLD,12));
        table1.getTableHeader().setBackground(Color.white);
//        searchField.setPreferredSize(new Dimension(searchField.getWidth(),deleteButton.getHeight()));
        modeComboBox.setBackground(Color.white);
        modeComboBox.setRenderer(new DefaultListCellRenderer(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
        });
        searchField.requestFocusInWindow();
        table1.setDragEnabled(false);
        table1.setShowVerticalLines(false);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setRowHeight(30);

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
        modeComboBox.addItemListener(e -> changeViews());
        buscarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sortTable();
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()== KeyEvent.VK_ENTER){
                    sortTable();
                }
            }
        });
        
    }

    private void changeViews() {
        final int index = modeComboBox.getSelectedIndex();
        if (index ==0){
            initTabla();
        }else if (index ==1){
            showParentsInfo();
        }else if (index ==2){
            showElementary();
        }else if (index ==3){
            showMiddleSchool();
        }else if (index==4){
            showHighSchool();
        }else if (index==5){
            showScholarship();
        }else if (index==6){
            showPayments();
        }
    }


    public void initTabla() {
        initTabla("");
    }

    private void initTabla(String id) {
        // TODO: 04/06/2020 agregar grado
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
    void showParentsInfo(){
        miModelo = new DefaultTableModel();

        miModelo.addColumn("Id Alumno");
        miModelo.addColumn("Id Tutor");
        miModelo.addColumn("Nombre");
        miModelo.addColumn("Apellido");
        miModelo.addColumn("RFC");
        miModelo.addColumn("Teléfono");
        table1.setModel(miModelo);
        sortTable();
        String[] datos = new String[miModelo.getColumnCount()];

        // TODO: 04/06/2020 move to service
//        String sql ="Select * from empleado";
        String sql;
        sql = "Select ID_ALUMNO,tutores.ID_TUTOR,TUTO_NOMBRE,TUTO_APELLIDO,TUTO_RFC,TUTO_TELEFONO" +
                " from tutores,alumnos where alumnos.ID_TUTOR = tutores.ID_TUTOR";
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
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void showMiddleSchool(){
        miModelo = new DefaultTableModel();

        miModelo.addColumn("Id Alumno");
        miModelo.addColumn("Matricula");
        miModelo.addColumn("Grado");
        miModelo.addColumn("Taller");
        miModelo.addColumn("Fecha de Inscripción");
        table1.setModel(miModelo);
        sortTable();
        String[] datos = new String[miModelo.getColumnCount()];

        // TODO: 04/06/2020 move to service
//        String sql ="Select * from empleado";
        String sql;
        sql = "Select alumnos.ID_ALUMNO,MATRICULA,GRADO,TALLER,INSC_FECHA" +
                " from inscripciones,alumnos where alumnos.ID_ALUMNO = inscripciones.ID_ALUMNO and " +
                "GRADO>=6 and GRADO<=9";
        try {
            Statement sentencia = con.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
                datos[3] = rs.getString(4);
                datos[4] = rs.getString(5);
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void addColumns(DefaultTableModel model,String... args){
        for (String string:
             args) {
            model.addColumn(string);
        }
    }
    void showPayments(){
        miModelo = new DefaultTableModel();
        addColumns(miModelo,"ID Alumno","Grado","Cantidad Pago","Fecha","Otros Detalles");
        table1.setModel(miModelo);
        sortTable();
        String[] datos = new String[miModelo.getColumnCount()];

        // TODO: 04/06/2020 move to service
        String sql = "Select pagos_incripciones.ID_ALUMNO,GRADO,CANTIDAD_PAGO,INSC_FECHA,OTROS_DETALLES" +
                " from pagos_incripciones,inscripciones where pagos_incripciones.ID_ALUMNO=inscripciones.ID_ALUMNO";
        try {
            Statement sentencia = con.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < datos.length; i++) {
                    datos[i] = rs.getString(i+1);
                }
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private double calculateDiscount(double percent, double flat, int grade){// TODO: 03/06/2020 assert flatDiscount and percent are non negative, percent is between 0,100
        double cost;
        int [] prices = PaymentPanel.prices;
        if (grade<=6){
            if (grade<=3){
                cost=prices[0];
            }else {
                cost=prices[1];
            }
        }else if (grade<=9){
            cost=prices[2];

        }else {
            cost=prices[3];
        }
        double d = (cost - flat);
        d -= d * (percent / 100);

        return cost - d;
//        this.totalAmount.setText(String.valueOf(d));
    }
    void showScholarship(){
        miModelo = new DefaultTableModel();
        addColumns(miModelo,"ID Alumno","Grado","Desc en Porcentaje","Descuento fijo","Descuento total");
        table1.setModel(miModelo);
        sortTable();
        String[] datos = new String[miModelo.getColumnCount()];

        // TODO: 04/06/2020 move to service
        // TODO: 04/06/2020 fix the 0 amount scholarships
        String sql = "Select becas.ID_ALUMNO,GRADO,PORCENTAJE,CANTIDAD" +
                " from becas,inscripciones where becas.ID_ALUMNO=inscripciones.ID_ALUMNO and (PORCENTAJE>0 or CANTIDAD>0)";
        try {
            Statement sentencia = con.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            while (rs.next()) {
                for (int i = 0; i < datos.length-1; i++) {
                    datos[i] = rs.getString(i+1);
                }
                datos[datos.length-1] = String.valueOf(calculateDiscount(Double.parseDouble(datos[2]),Double.parseDouble(datos[3]),Integer.parseInt(datos[1])));
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void showElementary(){
        miModelo = new DefaultTableModel();

        miModelo.addColumn("Id Alumno");
        miModelo.addColumn("Matricula");
        miModelo.addColumn("Grado");
        miModelo.addColumn("Fecha de Inscripción");
        table1.setModel(miModelo);
        sortTable();
        String[] datos = new String[miModelo.getColumnCount()];

        // TODO: 04/06/2020 move to service
//        String sql ="Select * from empleado";
        String sql;
        sql = "Select alumnos.ID_ALUMNO,MATRICULA,GRADO,INSC_FECHA" +
                " from inscripciones,alumnos where alumnos.ID_ALUMNO = inscripciones.ID_ALUMNO and " +
                "GRADO<=6";
        try {
            Statement sentencia = con.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
                datos[3] = rs.getString(4);
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void showHighSchool(){
        miModelo = new DefaultTableModel();

        miModelo.addColumn("Id Alumno");
        miModelo.addColumn("Matricula");
        miModelo.addColumn("Grado");
        miModelo.addColumn("Extracurricular");
        miModelo.addColumn("Fecha de Inscripción");
        table1.setModel(miModelo);
        sortTable();
        String[] datos = new String[miModelo.getColumnCount()];

        // TODO: 04/06/2020 move to service
//        String sql ="Select * from empleado";
        String sql;
        sql = "Select alumnos.ID_ALUMNO,MATRICULA,GRADO,EXTRACLASE,INSC_FECHA" +
                " from inscripciones,alumnos where alumnos.ID_ALUMNO = inscripciones.ID_ALUMNO and " +
                "GRADO>=10";
        try {
            Statement sentencia = con.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
                datos[3] = rs.getString(4);
                datos[4] = rs.getString(5);
                miModelo.addRow(datos);
            }
            table1.setModel(miModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sortTable() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table1.getModel());
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText(),0));
        table1.setRowSorter(sorter);
    }
    private void modifyStudent() {
        int row;
        row=table1.getSelectedRow();
        if (row==-1 || modeComboBox.getSelectedIndex()!=0){
            JOptionPane.showMessageDialog(null, "No hay estudiante seleccionado");
        }else{
            mainWindow.editAlumno(Integer.parseInt((String) table1.getValueAt(row,0)));
        }
    }
    private void deleteStudent() {
        int row;
        row=table1.getSelectedRow();
        if (row==-1 || modeComboBox.getSelectedIndex()!=0){
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
    void addToPanel(JPanel panel,JComponent component, int gridy, int width, int weightx,int r) {
        constraints.insets = new Insets(5, 0, 5, r);
        constraints.weightx = weightx;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = width;
        constraints.gridy = gridy;
        panel.add(component, constraints);
    }
    void addToPanel(JPanel panel,JComponent component, int gridy, int width, int weightx) {
        addToPanel(panel,component,gridy,width,weightx,5);
    }


}
