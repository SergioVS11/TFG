package com.viewnext.tools;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
 
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
 
public class ExcelExporter {
 
    public static void exportTableToExcel(JTable table, Component parentComponent) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
 
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(parentComponent, "No hay datos para exportar.", "Exportar a Excel", JOptionPane.WARNING_MESSAGE);
                return;
            }
 
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar como");
            fileChooser.setSelectedFile(new File("resultados.xlsx"));
 
            int seleccion = fileChooser.showSaveDialog(parentComponent);
            if (seleccion != JFileChooser.APPROVE_OPTION) {
                return;
            }
 
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
 
            long start = System.currentTimeMillis(); // Medición de tiempo
 
            Workbook workbook = new SXSSFWorkbook(); // Workbook optimizado para streaming
            Sheet sheet = workbook.createSheet("Resultados");
 
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
            }
 
            for (int row = 0; row < model.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    Cell cell = dataRow.createCell(col);
 
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }
            }
 
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
 
            ((SXSSFWorkbook) workbook).dispose(); // Libera archivos temporales
            workbook.close();
 
            long end = System.currentTimeMillis();
            System.out.println("Tiempo de exportación: " + (end - start) + " ms");
 
            JOptionPane.showMessageDialog(parentComponent, "Archivo Excel exportado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentComponent, "Error al exportar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            Logger.logError("Error al exportar a Excel", ex);
        }
    }
}
 
 