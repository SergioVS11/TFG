package com.viewnext.tools;

import com.viewnext.configuration.models.UserProperties;
import org.w3c.dom.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XmlBuilder {

    private static final String CONFIG_FILE_NAME = "config.xml";
    private static final File CONFIG_FILE = new File("resources" + File.separator + CONFIG_FILE_NAME);

    public static void escribirXML(String nombreUser, UserProperties user) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            if (CONFIG_FILE.exists()) {
                doc = builder.parse(CONFIG_FILE);
                doc.getDocumentElement().normalize();
            } else {
                doc = builder.newDocument();
                Element root = doc.createElement("users");
                doc.appendChild(root);
            }

            Element root = doc.getDocumentElement();

            // Buscar si ya existe un <user name="...">
            NodeList userNodes = root.getElementsByTagName("user");
            Element existingUser = null;

            for (int i = 0; i < userNodes.getLength(); i++) {
                Element userElement = (Element) userNodes.item(i);
                if (nombreUser.equals(userElement.getAttribute("name"))) {
                    existingUser = userElement;
                    break;
                }
            }

            if (existingUser != null) {
                root.removeChild(existingUser);
            }

            Element userElement = doc.createElement("user");
            userElement.setAttribute("name", nombreUser);

            Element userConfig = doc.createElement("userConfig");
            userElement.appendChild(userConfig);

            for (Method method : UserProperties.class.getDeclaredMethods()) {
                if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                    String nombreCampo = method.getName().substring(3);
                    nombreCampo = Character.toLowerCase(nombreCampo.charAt(0)) + nombreCampo.substring(1);

                    Object valor = method.invoke(user);
                    if (valor != null) {
                        String texto = valor.toString();
                        if ("userName".equals(nombreCampo) || "userPassword".equals(nombreCampo)) {
                            texto = Encrypter.encript(texto);
                        }
                        Element campo = doc.createElement(nombreCampo);
                        campo.appendChild(doc.createTextNode(texto));
                        userConfig.appendChild(campo);
                    }
                }
            }

            root.appendChild(userElement);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            CONFIG_FILE.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(fos);
                transformer.transform(source, result);
            }

        } catch (Exception e) {
            Logger.logError("Error al guardar la configuración", e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error a la hora de guardar.");
        }
    }

    public static UserProperties leerXML(String nombreUser) {
        if (!CONFIG_FILE.exists()) {
            System.out.println("Archivo de configuración no encontrado");
            return null;
        }

        try (InputStream is = new FileInputStream(CONFIG_FILE)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList userNodes = doc.getElementsByTagName("user");

            for (int i = 0; i < userNodes.getLength(); i++) {
                Element userElement = (Element) userNodes.item(i);
                if (nombreUser.equals(userElement.getAttribute("name"))) {
                    Element configElement = (Element) userElement.getElementsByTagName("userConfig").item(0);
                    NodeList elementos = configElement.getChildNodes();
                    UserProperties user = new UserProperties();

                    for (int j = 0; j < elementos.getLength(); j++) {
                        Node nodo = elementos.item(j);
                        if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                            String nombreCampo = nodo.getNodeName();
                            String valorTexto = nodo.getTextContent();

                            if ("userName".equals(nombreCampo) || "userPassword".equals(nombreCampo)) {
                                valorTexto = Encrypter.desencript(valorTexto);
                            }

                            String setterName = "set" + Character.toUpperCase(nombreCampo.charAt(0)) + nombreCampo.substring(1);
                            for (Method method : UserProperties.class.getDeclaredMethods()) {
                                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                                    Class<?> tipo = method.getParameterTypes()[0];
                                    Object valor = valorTexto;
                                    if (tipo == int.class || tipo == Integer.class) {
                                        valor = Integer.parseInt(valorTexto);
                                    }
                                    method.invoke(user, valor);
                                    break;
                                }
                            }
                        }
                    }
                    return user;
                }
            }

            System.out.println("Usuario no encontrado.");
            return null;

        } catch (Exception e) {
            Logger.logError(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String[] listarNombresUsuarios() {
        if (!CONFIG_FILE.exists()) {
            return new String[0];
        }

        try (InputStream is = new FileInputStream(CONFIG_FILE)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList userNodes = doc.getElementsByTagName("user");
            String[] nombres = new String[userNodes.getLength()];

            for (int i = 0; i < userNodes.getLength(); i++) {
                Element userElement = (Element) userNodes.item(i);
                nombres[i] = userElement.getAttribute("name");
            }

            return nombres;

        } catch (Exception e) {
            e.printStackTrace();
            Logger.logError(e);
            return new String[0];
        }
    }
}
