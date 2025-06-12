package ImpresoraSergio;

import ImpresoraSergio.Impl.PrinterStatusListener;
import ImpresoraSergio.Interfaces.IPrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;

public class GUI {
    private final IPrinter logicaPrinter;

    public GUI(String printerType) {
        this.logicaPrinter = SeleccionPrinter.getInstance().createPrinter(printerType);

    // Crear el marco de la GUI
        JFrame frame = new JFrame("Impresora de Sergio");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());

        JTextField urlField = new JTextField(20);
        JLabel urlLabel = new JLabel("Ingrese la URL:");
        JButton printButton = new JButton("Imprimir");
        JButton cancelButton = new JButton("Cancelar");
        JLabel statusLabel = new JLabel("Estado: Esperando...");

        frame.add(urlLabel);
        frame.add(urlField);
        frame.add(printButton);
        frame.add(cancelButton);
        frame.add(statusLabel);

        // Enlazar el JLabel con la lógica de impresión
        logicaPrinter.setStatusLabel(statusLabel);

        // Obtener el único PrinterStatusListener
        PrinterStatusListener statusListener = PrinterStatusListener.getInstance();

        //  la lógica para el botón de imprimir
        printButton.addActionListener(e -> {
            String url = urlField.getText();
            try {
                logicaPrinter.print(url);
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        });

        //  la lógica para el botón de cancelar
        cancelButton.addActionListener(e -> {
            try {
                logicaPrinter.cancel();

            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        });

        // Establecer el PrinterStatusListener para manejar los eventos de estado
        logicaPrinter.addStatusListener(statusListener);

        frame.setVisible(true);
    }
}
