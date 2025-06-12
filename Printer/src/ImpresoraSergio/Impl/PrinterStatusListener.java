package ImpresoraSergio.Impl;


import ImpresoraSergio.Interfaces.IDesktopPrinterStatusListener;
import ImpresoraSergio.Interfaces.PrinterStatus;

import javax.swing.*;

public class PrinterStatusListener implements IDesktopPrinterStatusListener {
    //  Crear una instancia estática de la propia clase
    private static PrinterStatusListener instance;
    private JLabel statusLabel;

    public void setStatusLabel(JLabel label) {
        this.statusLabel = label;
    }
    //  Hacer que el constructor sea privado para evitar la creación de más instancias
    private PrinterStatusListener() {

    }

    // Crear un método estático para acceder a la instancia única
    public static PrinterStatusListener getInstance() {
        if (instance == null) {
            instance = new PrinterStatusListener();
        }
        return instance;
    }

    @Override
    public void statusUpdate(PrinterStatus status) {
        int currentStatus = status.getStatus();  // Obtener el estado de la impresora
        String statusDescription = status.getDescription(); // Obtener la descripción del estado

        // Aquí actualizamos el JLabel de la GUI con el estado de la impresora
        switch (currentStatus) {
            case PrinterStatus.PRINTER_STATUS_OK:
                System.out.println("La impresora está lista: " + statusDescription);
                break;

            case PrinterStatus.PRINTER_STATUS_OFFLINE:
                System.out.println("La impresora está fuera de línea: " + statusDescription);
                break;

            case PrinterStatus.PRINTER_STATUS_NO_PAPER:
                System.out.println("La impresora no tiene papel: " + statusDescription);
                break;

            case PrinterStatus.PRINTER_STATUS_OTHER:
                System.out.println("Error desconocido: " + statusDescription);
                break;

            default:
                System.out.println("Estado desconocido de la impresora");
                break;
        }
        if (statusLabel != null){
            SwingUtilities.invokeLater(()-> statusLabel.setText("Estado: " + statusDescription));
        }
    }
}
