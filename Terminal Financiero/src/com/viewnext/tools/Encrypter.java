package com.viewnext.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Encrypter {
    private static String key;

    public static void loadKey(String resourcePath) {
        // Usamos getResourceAsStream() para leer el archivo dentro del .jar
        try (InputStream inputStream = Encrypter.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("El archivo " + resourcePath + " no se encontró.");
            }
            // Leer la primera línea del archivo como clave
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                key = reader.readLine(); // Cargar la clave en la variable estática
                if (key == null || key.isEmpty()) {
                    throw new RuntimeException("La clave está vacía.");
                }
                System.out.println("Clave cargada correctamente.");
            }
        } catch (IOException e) {
            Logger.logError("Error al leer la clave", e);
            throw new RuntimeException("Error al leer la clave", e);
        }
    }

    public static String encript(String text) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedText = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedText);
        } catch (Exception e) {
            Logger.logError("Error en la encriptación", e);
            throw new RuntimeException("Error en la encriptación", e);
        }
    }

    public static String desencript(String encryptedText) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedText = cipher.doFinal(decodedBytes);
            return new String(decryptedText);
        } catch (Exception e) {
            Logger.logError("Error en la desencriptación",e);
            throw new RuntimeException("Error en la desencriptación", e);
        }
    }

}
