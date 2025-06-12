package com.viewnext.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static FileHandler fileHandler;

    static {
        try {
            // Ruta al directorio del jar
            String basePath = new File(".").getCanonicalPath(); // Directorio donde se ejecuta el .jar
            File logDir = new File(basePath, "logs");

            if (!logDir.exists()) {
                logDir.mkdirs(); // Crea la carpeta si no existe
            }

            // Ruta del archivo log rotativo
            File logFile = new File(logDir, "PaginaError%g.log");

            fileHandler = new FileHandler(logFile.getAbsolutePath(), 1024 * 1024, 3, false);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // Evita que se imprima en consola
        } catch (IOException e) {
            System.err.println("No se pudo inicializar el logger: " + e.getMessage());
        }
    }

    public static void logError(String mensaje, Throwable throwable) {
        logger.log(Level.SEVERE, mensaje, throwable);
    }

    public static void logError(Throwable throwable) {
        logger.log(Level.SEVERE, "Excepción capturada:", throwable);
    }
}
