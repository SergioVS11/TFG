package ImpresoraSergio.Interfaces;

import ImpresoraSergio.DekstopPrinterException;

import javax.swing.*;
import java.awt.print.PrinterException;

public abstract interface IPrinter {
    // Imprime una imagen a partir de una URL
    public abstract void print(java.lang.String URLFile) throws PrinterException;
    // Imprime una imagen a partir de Fichero
    public abstract void print(java.io.File arg0) throws DekstopPrinterException;
    // Cancela la impresión
    public abstract void cancel() throws PrinterException;
    // Añadir al Listener del dispositivo
    public abstract void addStatusListener(IDesktopPrinterStatusListener arg0);

    void setStatusLabel(JLabel statusLabel);
}

