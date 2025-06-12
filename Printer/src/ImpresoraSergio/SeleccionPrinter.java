package ImpresoraSergio;


import ImpresoraSergio.Impl.PrinterReal;
import ImpresoraSergio.Impl.PrinterSimulacion;
import ImpresoraSergio.Interfaces.IPrinter;

public class SeleccionPrinter {

    private static SeleccionPrinter instance;

    public static SeleccionPrinter getInstance() {
        if (instance == null) {
            instance = new SeleccionPrinter();
        }
        return instance;
    }

    public IPrinter createPrinter(String printerType) {
        if ("real".equalsIgnoreCase(printerType)) {
            return new PrinterReal();  // Devuelve la impresora real
        } else if ("simulado".equalsIgnoreCase(printerType)) {
            return new PrinterSimulacion();// Devuelve la impresora simulada
        } else {
            System.err.println("Impresora no reconocida");
            return null;
        }

    }
}

