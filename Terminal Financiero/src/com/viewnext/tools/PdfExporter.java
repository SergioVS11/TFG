package com.viewnext.tools;
 
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
 
import javax.swing.*;
import java.io.File;
import java.io.IOException;
 
public class PdfExporter {
 
    public void exportarJTableComoPdf(JTable table, String[] encabezado, String fechaTexto, File archivoDestino) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
 
            float margin = 40f;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float y = yStart;
 
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            float fontSize = 10f;
            float rowHeight = 20f;
            float minY = margin + rowHeight;
 
            int numCols = table.getColumnCount();
            float colWidth = tableWidth / numCols;
 
            PDPageContentStream content = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true, true);
 
            // Encabezado principal
            content.beginText();
            content.setFont(boldFont, 12f);
            content.newLineAtOffset(margin, y);
            for (String linea : encabezado) {
                content.showText(linea);
                content.newLineAtOffset(0f, -15f);
                y -= 15f;
            }
            content.setFont(font, fontSize);
            content.showText(fechaTexto);
            content.endText();
            y -= 25f;
 
            // Dibujar encabezado de columnas
            y = drawHeader(content, table, boldFont, fontSize, y, margin, colWidth, rowHeight);
 
            // Dibujar filas
            for (int row = 0; row < table.getRowCount(); row++) {
                if (y < minY) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    y = yStart;
 
                    content = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true, true);
                    y = drawHeader(content, table, boldFont, fontSize, y, margin, colWidth, rowHeight);
                }
 
                if (row % 2 == 0) {
                    content.setNonStrokingColor(0.95f, 0.95f, 0.95f);
                    content.addRect(margin, y, tableWidth, rowHeight);
                    content.fill();
                }
 
                content.setNonStrokingColor(0f);
                for (int col = 0; col < numCols; col++) {
                    float cellX = margin + col * colWidth;
                    Object value = table.getValueAt(row, col);
                    String text = value != null ? value.toString() : "";
                    drawCellText(content, text, cellX, y, colWidth, rowHeight, font, fontSize);
                }
 
                y -= rowHeight;
            }
 
            content.close();
            document.save(archivoDestino);
        }
    }
 
    private float drawHeader(PDPageContentStream content, JTable table, PDFont font, float fontSize, float y, float margin, float colWidth, float rowHeight) throws IOException {
        int numCols = table.getColumnCount();
        content.setNonStrokingColor(0.7f, 0.85f, 1f);
        content.addRect(margin, y, colWidth * numCols, rowHeight);
        content.fill();
        content.setNonStrokingColor(0f);
        for (int col = 0; col < numCols; col++) {
            float cellX = margin + col * colWidth;
            drawCellText(content, table.getColumnName(col), cellX, y, colWidth, rowHeight, font, fontSize);
        }
        return y - rowHeight;
    }
 
    private void drawCellText(PDPageContentStream content, String text, float x, float y, float width, float height, PDFont font, float fontSize) throws IOException {
        content.setStrokingColor(0.15f);
        content.addRect(x, y, width, height);
        content.stroke();
 
        String truncated = truncateText(text, font, fontSize, width - 4f);
        float textX = x + 2f;
        float textY = y + (height - fontSize) / 2f + 2f;
 
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(textX, textY);
        content.showText(truncated);
        content.endText();
    }
 
    private String truncateText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        if (font.getStringWidth(text) / 1000f * fontSize <= maxWidth) {
            return text;
        }
 
        String ellipsis = "...";
        while (font.getStringWidth(text + ellipsis) / 1000f * fontSize > maxWidth && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }
 
        return text + ellipsis;
    }
}
 