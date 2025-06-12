package ImpresoraSergio.Impl;

import ImpresoraSergio.DekstopPrinterException;
import ImpresoraSergio.Interfaces.IDesktopPrinterStatusListener;
import ImpresoraSergio.Interfaces.IPrinter;


import javax.swing.*;
import java.awt.print.PrinterException;
import java.io.File;

public class PrinterReal implements IPrinter {
   //Aqui iria el driver de la impresora real
    @Override
    public void print(String URLFile) throws PrinterException {

    }

    @Override
    public void print(File arg0) throws DekstopPrinterException {

    }

    @Override
    public void cancel() throws PrinterException {

    }

    @Override
    public void addStatusListener(IDesktopPrinterStatusListener arg0) {

    }

    @Override
    public void setStatusLabel(JLabel statusLabel) {

    }
}
