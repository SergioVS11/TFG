package ImpresoraSergio.Impl;


import ImpresoraSergio.Interfaces.PrinterStatus;

public class PrinterStatusImpl implements PrinterStatus {
    private final int status;
    private final String description;

    public PrinterStatusImpl(int status, String description) {
        this.status = status;
        this.description = description;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
