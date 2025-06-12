package com.viewnext.database.panels;
 
import com.viewnext.database.models.Centro;
import com.viewnext.tools.Conexion;
import com.viewnext.tools.ExcelExporter;
import com.viewnext.tools.Logger;
import com.viewnext.tools.NumericFilter;
import com.viewnext.tools.PdfExporter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;

import java.io.File;

import java.io.IOException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.awt.Cursor;
import javax.swing.SwingWorker;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import java.awt.Dialog;



 
public class ConsultaPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JTextField empresaField;
    private JTextField centroField;
    private JTextField terminalField;
    private JTextField versionField;
    private Conexion conexion;
    private JButton buscarButton;
    private JLabel resultadosLabel;
    private JButton guardarButton;
    private JButton pdfButton;
 
    public ConsultaPanel(Conexion conexion) {
        this.setSize(1250, 500);
        this.setLayout(new BorderLayout());
        this.conexion = conexion;
        this.tabbedPane = new JTabbedPane();
        this.add(this.tabbedPane, "Center");
        this.crearNuevaPestana();
        this.resultadosLabel = new JLabel("Resultados encontrados: 0");
        this.guardarButton = new JButton("Guardar XLSX");
        this.guardarButton.setEnabled(false);
        this.guardarButton.addActionListener(e -> exportarAExcel());
        
        this.pdfButton = new JButton("Guardar PDF");
        this.pdfButton.setEnabled(false);
        this.pdfButton.addActionListener(e -> exportarComoPDF());
        
 
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(this.resultadosLabel);
 
        this.add(bottomPanel, BorderLayout.SOUTH);
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = 17;
        this.empresaField = new JTextField(10);
        this.centroField = new JTextField(10);
        this.terminalField = new JTextField(10);
        this.versionField = new JTextField(10);
        ((AbstractDocument)this.empresaField.getDocument()).setDocumentFilter(new NumericFilter(3));
        ((AbstractDocument)this.centroField.getDocument()).setDocumentFilter(new NumericFilter(4));
        ((AbstractDocument)this.terminalField.getDocument()).setDocumentFilter(new NumericFilter(4));
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Empresa (3 dígitos):"), gbc);
        gbc.gridx = 1;
        filterPanel.add(this.empresaField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(new JLabel("Centro (4 dígitos):"), gbc);
        gbc.gridx = 1;
        filterPanel.add(this.centroField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        filterPanel.add(new JLabel("Terminal (5000–5100):"), gbc);
        gbc.gridx = 1;
        filterPanel.add(this.terminalField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        filterPanel.add(new JLabel("Versión Aplicación (VNNRXXETC):"), gbc);
        gbc.gridx = 1;
        filterPanel.add(this.versionField, gbc);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(0));
        this.buscarButton = new JButton("CONSULTA");
        this.buscarButton.addActionListener(this::buscarDatos);
        buttonPanel.add(this.buscarButton);
        buttonPanel.add(this.guardarButton);
        buttonPanel.add(pdfButton);
        JButton crearTabButton = new JButton("Nueva Pestaña");
        crearTabButton.addActionListener((e) -> this.crearNuevaPestana());
        buttonPanel.add(crearTabButton);
        JButton salirBoton = new JButton("SALIR");
        salirBoton.addActionListener((e) -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
 
        });
        buttonPanel.add(salirBoton);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        filterPanel.add(buttonPanel, gbc);
        this.add(filterPanel, "North");
        this.tabbedPane.addChangeListener(e -> {
            Component selected = this.tabbedPane.getSelectedComponent();
            if (selected instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) selected;
 
                // Cargar filtros guardados
                Centro filtro = (Centro) scrollPane.getClientProperty("filtro");
                if (filtro != null) {
                    this.empresaField.setText(filtro.getEmpresa() != 0 ? String.format("%03d", filtro.getEmpresa()) : "");
                    this.centroField.setText(filtro.getCentro() != 0 ? String.format("%04d", filtro.getCentro()) : "");
                    this.terminalField.setText(filtro.getTerminal() != 0 ? String.valueOf(filtro.getTerminal()) : "");
                    this.versionField.setText(filtro.getVersionTF() != null ? filtro.getVersionTF() : "");
                } else {
                    this.empresaField.setText("");
                    this.centroField.setText("");
                    this.terminalField.setText("");
                    this.versionField.setText("");
                }
 
                // Actualizar label de resultados
                 scrollPane = (JScrollPane) selected;
                Object prop = scrollPane.getClientProperty("resultCount");
                int count = (prop instanceof Integer) ? (Integer) prop : 0;
                String mensajeResultado;
               if(count==1){
            	    mensajeResultado ="Resultado encontrado: ";
               }else {
            	    mensajeResultado = "Resultados encontrados: ";
               }
               
                resultadosLabel.setText(mensajeResultado + count);
                guardarButton.setEnabled(count > 0);
                pdfButton.setEnabled(count > 0);

                // Actualizar el estado del botón "Guardar CSV" dependiendo si hay datos
                JTable table = (JTable) scrollPane.getViewport().getView();
                if (table != null && table.getModel().getRowCount() > 0) {
                    this.guardarButton.setEnabled(true);
                    this.pdfButton.setEnabled(true);
                } else {
                    this.guardarButton.setEnabled(false);
                    this.pdfButton.setEnabled(false);
                }

 
            } else {
                // No hay JScrollPane seleccionado (o pestaña vacía)
                this.empresaField.setText("");
                this.centroField.setText("");
                this.terminalField.setText("");
                this.versionField.setText("");
                this.resultadosLabel.setText("Resultados encontrados: 0");
                this.guardarButton.setEnabled(false);
            }
        });
 
    }
 
    
    
    private void crearNuevaPestana() {
        DefaultTableModel emptyTableModel = new DefaultTableModel();
        emptyTableModel.setColumnIdentifiers(new String[]{"EMPRESA", "CENTROCAJA", "TERMINAL", "VERSIONTF", "IPTERMINAL", "NAMEMAQUINA", "FECHAHORA"});
        JTable emptyTable = new JTable(emptyTableModel);
        JScrollPane emptyScrollPane = new JScrollPane(emptyTable);
 
        JLabel tabResultadosLabel = new JLabel("Resultados encontrados: 0");
        emptyScrollPane.putClientProperty("resultadosLabel", 0);
 
        String tabTitle = "Consulta " + (this.tabbedPane.getTabCount() + 1);
 
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(tabTitle);
        titleLabel.setFont(new Font("Arial", 1, 12));
        titleLabel.setForeground(Color.BLACK);
        tabPanel.add(titleLabel, BorderLayout.CENTER);
 
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(30, 30));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setBackground(new Color(0, 0, 0, 0));
        closeButton.setForeground(Color.RED);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> cerrarPestana(this.tabbedPane.indexOfComponent(emptyScrollPane)));
        tabPanel.add(closeButton, BorderLayout.EAST);
 
        this.tabbedPane.addTab("", emptyScrollPane);
        this.tabbedPane.setTabComponentAt(this.tabbedPane.getTabCount() - 1, tabPanel);
        this.tabbedPane.setSelectedComponent(emptyScrollPane);
    }
 
 
    private void cerrarPestana(int index) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea cerrar esta pestaña?", "Confirmación de cierre", 0);
        if (confirm == 0) {
            this.tabbedPane.removeTabAt(index);
        }
 
    }
 
    private void buscarDatos(ActionEvent e) {
        String empresa = this.empresaField.getText().trim();
        String centro = this.centroField.getText().trim();
        String terminal = this.terminalField.getText().trim();
        String version = this.versionField.getText().trim();
        StringBuilder errores = new StringBuilder();
 
        if (!empresa.isEmpty() && !empresa.matches("\\d{3}")) {
            errores.append("- Empresa debe ser un número de 3 dígitos.\n");
        }
 
        if (!centro.isEmpty() && !centro.matches("\\d{4}")) {
            errores.append("- Centro debe ser un número de 4 dígitos.\n");
        }
 
        if (!terminal.isEmpty()) {
            try {
                int terminalInt = Integer.parseInt(terminal);
                if (terminalInt < 5000 || terminalInt > 5100) {
                    errores.append("- Terminal debe estar entre 5000 y 5100.\n");
                }
            } catch (NumberFormatException ex) {
                Logger.logError("Valor de la terminal no válido.", ex);
                errores.append("- Terminal debe ser un número válido.\n");
            }
        }
 
        if (!version.isEmpty() && !version.matches("^V\\d{2}R\\d{2}E[01]C$")) {
            errores.append("- Versión debe cumplir con el formato VNNRXXETC (ej. V01R04E1C).\n");
        }
 
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this, "Errores de validación:\n" + errores, "Filtros inválidos", 2);
        } else {
            try {
                List<Centro> resultados = this.conexion.buscarCentros(empresa, centro, terminal, version);
                if (resultados.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No se ha encontrado ningún resultado con esos filtros.", "Sin resultados", 1);
 
                    String mensajeResultado = (resultados.size() == 0) ? "Resultado encontrado" : "Resultados encontrados";
                    this.resultadosLabel.setText(mensajeResultado + " " + resultados.size());
                    this.guardarButton.setEnabled(false);
                    this.pdfButton.setEnabled(false);
 
                    JScrollPane activeScrollPane = (JScrollPane) this.tabbedPane.getSelectedComponent();
                    JTable resultTable = (JTable) activeScrollPane.getViewport().getView();
                    DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
                    model.setRowCount(0); // <<< Limpia la tabla
 
                    activeScrollPane.putClientProperty("resultCount", 0);
                    
                    // Actualizar también el label guardado en la pestaña
                    Object prop = activeScrollPane.getClientProperty("resultadosLabel");
                    if (prop instanceof JLabel) {
                        JLabel tabResultadosLabel = (JLabel) prop;
                        mensajeResultado = (resultados.size() == 0) ? "Resultado encontrado" : "Resultados encontrados";
                        tabResultadosLabel.setText(mensajeResultado + " " + resultados.size());
                    }
 
                    return;
                }else {
                    JScrollPane activeScrollPane = (JScrollPane)this.tabbedPane.getSelectedComponent();
                    Centro filtro = new Centro(
                        empresa.isEmpty() ? 0 : Integer.parseInt(empresa),
                        centro.isEmpty() ? 0 : Integer.parseInt(centro),
                        terminal.isEmpty() ? 0 : Integer.parseInt(terminal),
                        version,
                        (String)null,
                        (String)null,
                        (Timestamp)null
                    );
                    activeScrollPane.putClientProperty("filtro", filtro);
 
                    JTable resultTable = (JTable)activeScrollPane.getViewport().getView();
                    DefaultTableModel model = (DefaultTableModel)resultTable.getModel();
                    model.setRowCount(0);
                    
                     activeScrollPane = (JScrollPane)tabbedPane.getSelectedComponent();
                 // cantidad de resultados:
                 int count = resultados.size();
                 // guardo en la pestaña:
                 activeScrollPane.putClientProperty("resultCount", count);
                 // actualizo la etiqueta global y el botón:
                 String mensajeResultado = (count == 1) ? "Resultado encontrado: " : "Resultados encontrados: ";
                 resultadosLabel.setText(mensajeResultado + count);
                 guardarButton.setEnabled(count > 0);
 
 
                    for (Centro centroResult : resultados) {
                        model.addRow(new Object[]{
                            centroResult.getEmpresa(),
                            centroResult.getCentro(),
                            centroResult.getTerminal(),
                            centroResult.getVersionTF(),
                            centroResult.getIpTerminal(),
                            centroResult.getNameMaquina(),
                            centroResult.getFechaHora()
                        });
                    }
                    this.resultadosLabel.setText(mensajeResultado + count);
                    this.guardarButton.setEnabled(true);
                    this.pdfButton.setEnabled(true);
                }
            } catch (SQLException ex) {
                Logger.logError("Error al consultar la base de datos", ex);
                ex.printStackTrace();
            } catch (Exception ex) {
                Logger.logError(ex);
                ex.printStackTrace();
            }
        }
    }
 
    private void exportarAExcel() {
        JScrollPane activeScrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        JTable table = (JTable) activeScrollPane.getViewport().getView();

        Window window = SwingUtilities.getWindowAncestor(this);

        // Cambiar el cursor a modo espera
        window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Crear diálogo de carga
        final JDialog loadingDialog = new JDialog(window, "Exportando", Dialog.ModalityType.APPLICATION_MODAL);
        JLabel label = new JLabel("Creando Excel...", SwingConstants.CENTER);
        loadingDialog.add(label);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(window);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

      
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                ExcelExporter.exportTableToExcel(table, ConsultaPanel.this); // 'this' ahora apunta correctamente al panel
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                window.setCursor(Cursor.getDefaultCursor());
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }


   

    
    
    private void exportarComoPDF() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) selectedComponent;
            JTable table = (JTable) scrollPane.getViewport().getView();
 
            table.revalidate();
            table.repaint();
 
            String sql = conexion.sqlFinal;
            String[] lineasEncabezado = dividirSQLPorLineas(sql, 7);
            String fecha = "Fecha y hora de emisión: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
 
            JTable finalTableToExport;
            if (table.getRowCount() == 0) {
                DefaultTableModel emptyModel = new DefaultTableModel(table.getColumnCount(), 0);
                emptyModel.setColumnIdentifiers(getColumnNames(table));
                finalTableToExport = new JTable(emptyModel);
            } else {
                finalTableToExport = table;
            }
 
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportando");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));
 
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
                }
 
                final File finalSelectedFile = selectedFile;
 
                // Crear un diálogo modal de espera (sin usar Dialog.ModalityType)
                JDialog loadingDialog = new JDialog(SwingUtilities.getWindowAncestor(tabbedPane));
                loadingDialog.setTitle("Por favor, espere...");
                loadingDialog.setModal(true);
                loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                loadingDialog.getContentPane().add(new JLabel("Creando PDF...", SwingConstants.CENTER));
                loadingDialog.setSize(200, 100);
                loadingDialog.setLocationRelativeTo(tabbedPane);
 
                Cursor originalCursor = getCursor();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
 
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            PdfExporter exporter = new PdfExporter();
                            exporter.exportarJTableComoPdf(finalTableToExport, lineasEncabezado, fecha, finalSelectedFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
 
                    @Override
                    protected void done() {
                        loadingDialog.dispose();
                        setCursor(originalCursor);
                        try {
                            get();
                            JOptionPane.showMessageDialog(null,
                                    "PDF exportado correctamente:\n" + finalSelectedFile.getAbsolutePath()
                            );
                        } catch (Exception e) {
                            Logger.logError("Error al exportar a PDF", e);
                            JOptionPane.showMessageDialog(null,
                                    "Error al exportar:\n" + e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };
 
                worker.execute();
                loadingDialog.setVisible(true);
            }
        }
    }
 
 
    
    private String[] getColumnNames(JTable table) {
        String[] columnNames = new String[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            columnNames[i] = table.getColumnName(i);
        }
        return columnNames;
    }
 
    
    private String[] dividirSQLPorLineas(String sql, int palabrasPorLinea) {
        String[] palabras = sql.trim().split("\\s+");
        List<String> lineas = new ArrayList<>();
        StringBuilder linea = new StringBuilder();
 
        for (int i = 0; i < palabras.length; i++) {
            linea.append(palabras[i]).append(" ");
            if ((i + 1) % palabrasPorLinea == 0) {
                lineas.add(linea.toString().trim());
                linea.setLength(0); // reset
            }
        }
 
        if (linea.length() > 0) {
            lineas.add(linea.toString().trim());
        }
        
        return lineas.toArray(new String[0]);
    }
 
    
}
 
 