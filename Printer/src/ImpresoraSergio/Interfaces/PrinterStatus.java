package ImpresoraSergio.Interfaces;

public abstract interface PrinterStatus {
    public static final int PRINTER_STATUS_OK = 0;
    public static final int PRINTER_STATUS_OFFLINE = 1;
    public static final int PRINTER_STATUS_NO_PAPER = 2;
    public static final int PRINTER_STATUS_OTHER = 3;
    // Obtener stado Impresora
    public abstract int getStatus();
    // Obtener descripción del estado
    public abstract String getDescription();
}
