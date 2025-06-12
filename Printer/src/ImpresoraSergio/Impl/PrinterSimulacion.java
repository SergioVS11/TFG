package ImpresoraSergio.Impl;



import ImpresoraSergio.*;
import ImpresoraSergio.Interfaces.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;



import static ImpresoraSergio.Interfaces.PrinterStatus.PRINTER_STATUS_OTHER;


public class PrinterSimulacion implements IPrinter {
    private PrinterJob printerJob;
    private volatile boolean estaImprimiendo = false;
    private volatile boolean estaCancelado = false;
    private volatile boolean tiempoAcabado = false;
    private JFrame imageFrame;
    private JLabel imageLabel;
    private IDesktopPrinterStatusListener statusListener;
    private JLabel statusLabel;
    private Thread hiloImpresion;


    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;


        // También se lo pasamos al listener
        if (PrinterStatusListener.getInstance() != null) {
            PrinterStatusListener.getInstance().setStatusLabel(statusLabel);
        }
    }



    @Override
    public void print(String URLFile) throws PrinterException {
        if (estaImprimiendo) {
            notifyListener(PRINTER_STATUS_OTHER,"Ya hay una impresión en curso.");
            return;
        }
        estaImprimiendo = true;
        estaCancelado = false;

        // Usar el Singleton para obtener la instancia de PrinterStatusListener
        statusListener = PrinterStatusListener.getInstance();


        Thread hiloTemporizador = new Thread(new Runnable() {
            @Override
            public void run() {
                tiempoAcabado= false;
                try {
                    Thread.sleep(25000);
                    if (!estaCancelado){
                        cancel("Tiempo agotado");
                        tiempoAcabado= true;
                    }
                } catch (InterruptedException | PrinterException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });



        hiloImpresion = new Thread(new Runnable() {
            @Override
            public void run() {
                hiloTemporizador.start();

                try {
                    if (Thread.currentThread().isInterrupted()) return;

                    notifyListener(PrinterStatus.PRINTER_STATUS_OK, "Descargando imagen");

                    URL url = new URL(URLFile);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    BufferedImage image;
                    try {
                        connection.connect();

                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, "Error de red");
                            return;
                        }

                        image = ImageIO.read(connection.getInputStream());
                    } finally {
                        connection.disconnect();
                    }

                    if (Thread.currentThread().isInterrupted()) {
                        notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, "Cancelado durante descarga");
                        return;
                    }

                    if (image == null) {
                        notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, "No se pudo cargar la imagen");
                        return;
                    }

                    mostrarImagen(image);

                    if (Thread.currentThread().isInterrupted()) {
                        notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, "Cancelado antes de imprimir");
                        return;
                    }

                    printerJob = PrinterJob.getPrinterJob();
                    printerJob.setJobName("Impresión de imagen");
                    printerJob.setPrintable(new Printable() {
                        @Override
                        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                            graphics.drawImage(image, 0, 0,
                                    (int) pageFormat.getImageableWidth(),
                                    (int) pageFormat.getImageableHeight(), null);
                            return Printable.PAGE_EXISTS;
                        }
                    });

                    if (printerJob.printDialog()) {
                        if (Thread.currentThread().isInterrupted()) {
                            notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, "Cancelado justo antes de imprimir");
                            return;
                        }

                        notifyListener(PrinterStatus.PRINTER_STATUS_OK, "Imprimiendo");
                        printerJob.print();

                        notifyListener(PrinterStatus.PRINTER_STATUS_OK, "Completado");

                        if (imageFrame != null) {
                            imageFrame.dispose();
                        }

                    } else {
                        estaCancelado = true;
                        hiloTemporizador.interrupt();
                        if (tiempoAcabado) {
                            notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, "Tiempo agotado");
                        } else {
                            notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, "Cancelado por usuario");
                            if (imageFrame != null) {
                                imageFrame.dispose();
                            }
                        }
                    }

                } catch (Exception e) {
                    notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, e.getMessage());
                } finally {
                    estaImprimiendo = false;
                }
            }
        });



        hiloImpresion.start();

    }

    private void mostrarImagen(BufferedImage image) {
        SwingUtilities.invokeLater(() -> {
            if (imageFrame == null) {
                imageFrame = new JFrame("Vista previa");
                imageFrame.setSize(500, 500);
                imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                imageLabel = new JLabel();
                imageFrame.add(new JScrollPane(imageLabel));
            }
            imageLabel.setIcon(new ImageIcon(image));
            imageFrame.setVisible(true);
        });
    }

    public void cancel(String motivo) throws PrinterException {
        if (estaImprimiendo && printerJob != null) {
            estaCancelado = true;
            printerJob.cancel();

            if (hiloImpresion != null && hiloImpresion.isAlive()) {
                hiloImpresion.interrupt();
            }

            if (imageFrame != null) {
                SwingUtilities.invokeLater(() -> {
                    imageFrame.dispose();
                });
            }

            notifyListener(PrinterStatus.PRINTER_STATUS_OTHER, motivo);
        }
    }


    @Override
    public void cancel() throws PrinterException {
        cancel("Cancelado por usuario");
    }



    @Override
    public void print(File file) throws DekstopPrinterException {
        notifyListener(PRINTER_STATUS_OTHER,"Función no implementada.");
    }

    @Override
    public void addStatusListener(IDesktopPrinterStatusListener listener) {
        //  Usamos el Singleton, no es necesario modificar nada aquí
    }

    private void notifyListener(int statusCode, String description) {
        if (statusListener != null) {
            // Crear un nuevo PrinterStatus con el statusCode y la description
            PrinterStatus status = new PrinterStatusImpl(statusCode, description);
            statusListener.statusUpdate(status);  // Llamo con una única instancia de PrinterStatus
        }


    }



}