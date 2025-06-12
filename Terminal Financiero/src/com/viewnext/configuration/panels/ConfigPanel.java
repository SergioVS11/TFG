package com.viewnext.configuration.panels;


import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.viewnext.configuration.models.UserProperties;
import com.viewnext.frames.HomeFrame;
import com.viewnext.tools.Logger;
import com.viewnext.tools.XmlBuilder;

public class ConfigPanel extends JPanel {

    private final Map<String, JComponent> fieldInputs = new HashMap<>();
    private final Map<String, Object> originalValues = new HashMap<>();
    private boolean changed = false;

    public static String nombreSeleccionado;


    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JComboBox<String> nombreCombo;
    private final JTextField nuevoNombreField;
    JLabel nuevoNombreLabel;

    public ConfigPanel(CardLayout cardLayout, JPanel contentPanel, HomeFrame frame) {
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;
        this.setSize(600, 400);

// Panel principal con GridLayout
        JPanel nombrePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        nombrePanel.setBorder(new EmptyBorder(10, 10, 0, 10));

// Fila 1: etiqueta + (combo + botón)
        String[] nombres = XmlBuilder.listarNombresUsuarios();
        String[] nombresConVacio = new String[nombres.length + 1];
        nombresConVacio[0] = ""; // elemento en blanco
        System.arraycopy(nombres, 0, nombresConVacio, 1, nombres.length);
        nombreCombo = new JComboBox<>(nombresConVacio);
        JButton nuevoButton = new JButton("Nuevo");

// Panel combinado para ComboBox + Botón
        JPanel comboConBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        comboConBoton.add(nombreCombo);
        comboConBoton.add(nuevoButton);

        nombrePanel.add(comboConBoton);

// Fila 2: Etiqueta y campo para nuevo nombre (inicialmente ocultos)
        nuevoNombreLabel = new JLabel("Nuevo nombre para guardar:");
        nuevoNombreField = new JTextField();
        if (!configFileExists()) {
            nuevoNombreLabel.setVisible(true);
            nuevoNombreField.setVisible(true);
		}
        nombrePanel.add(nuevoNombreLabel);
        nombrePanel.add(nuevoNombreField);

// Añadir al panel principal
        add(nombrePanel, BorderLayout.NORTH);

// Listener del ComboBox
        nombreCombo.addActionListener(e -> {
            nombreSeleccionado = (String) nombreCombo.getSelectedItem();
            if (nombreSeleccionado == null || nombreSeleccionado.isEmpty()) return;

            // Guardar el usuario como el último seleccionado
            Properties config = new Properties();
            config.setProperty("ultimo_usuario", nombreSeleccionado);
            try (FileOutputStream fos = new FileOutputStream("config.properties")) {
                config.store(fos, "Configuración de la app");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            nombreSeleccionado = (String) nombreCombo.getSelectedItem();
            if (nombreSeleccionado == null || nombreSeleccionado.isEmpty()) return;
            frame.setConfigString(nombreSeleccionado);
            UserProperties propiedadesSeleccionadas = XmlBuilder.leerXML(nombreSeleccionado);
            if (propiedadesSeleccionadas == null) return;

            try {
                for (String fieldName : fieldInputs.keySet()) {
                    Method getter = UserProperties.class.getMethod("get" + capitalize(fieldName));
                    Object value = getter.invoke(propiedadesSeleccionadas);
                    JComponent component = fieldInputs.get(fieldName);

                    if (component instanceof JTextField) {
                        ((JTextField) component).setText(value != null ? value.toString() : "");
                    } else if (component instanceof JPasswordField) {
                        ((JPasswordField) component).setText(value != null ? value.toString() : "");
                    } else if (component instanceof JComboBox<?>) {
                        ((JComboBox<?>) component).setSelectedItem(value);
                    }

                    originalValues.put(fieldName, value);

                }
            } catch (Exception ex) {
                Logger.logError(ex);
                ex.printStackTrace();
            }
        });

// Listener del botón "Nuevo"
        nuevoButton.addActionListener(e -> {
            for (Map.Entry<String, JComponent> entry : fieldInputs.entrySet()) {
                JComponent component = entry.getValue();
                if (component instanceof JTextField) {
                    ((JTextField) component).setText("");
                } else if (component instanceof JPasswordField) {
                    ((JPasswordField) component).setText("");
                } else if (component instanceof JComboBox<?>) {
                    ((JComboBox<?>) component).setSelectedIndex(-1);
                }
            }
            
            nuevoNombreLabel.setVisible(true);
            nuevoNombreField.setVisible(true);

            boolean contieneVacio = false;
            for (int i = 0; i < nombreCombo.getItemCount(); i++) {
                if ("".equals(nombreCombo.getItemAt(i))) {
                    contieneVacio = true;
                    break;
                }
            }
            if (!contieneVacio) {
                nombreCombo.addItem("");
            }
            nombreCombo.setSelectedItem("");

            revalidate();
            repaint();
        });
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            String ultimoUsuario = config.getProperty("ultimo_usuario");
            if (ultimoUsuario != null) {
                nombreCombo.setSelectedItem(ultimoUsuario);
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar config.properties: " + e.getMessage());
        }

        add(nombrePanel, BorderLayout.NORTH);

        UserProperties properties = nombreSeleccionado != null ? XmlBuilder.leerXML(nombreSeleccionado) : null;
        String[] orderedFieldNames = {
                "ipServer",
                "userName",
                "userPassword",
                "port",
                "instanceBBDD"
        };

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(orderedFieldNames.length + 1, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Map<String, Method> getters = new HashMap<>();
        for (Method method : UserProperties.class.getMethods()) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                String methodName = method.getName();
                String fieldName = methodName.substring(3);
                fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                getters.put(fieldName, method);
            }
        }

        for (String fieldName : orderedFieldNames) {
            Method getter = getters.get(fieldName);
            if (getter == null) continue;

            JLabel label = null;
            switch(fieldName) {
                case "ipServer":
                    label = new JLabel("Ip del Servidor");
                    break;
                case "userName":
                    label = new JLabel("Usuario");
                    break;
                case "userPassword":
                    label = new JLabel("Password");
                    break;
                case "port":
                    label = new JLabel("Puerto");
                    break;
                case "instanceBBDD":
                    label = new JLabel("Instancia de la BBDD");
                    break;
                default:
                    label = new JLabel(fieldName);
            }
            JComponent inputField;
            Object value;
            try {
                value = (properties != null) ? getter.invoke(properties) : null;
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logger.logError(e);
                throw new RuntimeException(e);
            }

            if (fieldName.equals("userPassword")) {
                inputField = new JPasswordField(value != null ? value.toString() : "", 20);
            } else if (fieldName.equals("instanceBBDD")) {
                inputField = new JComboBox<>(new String[]{"ORA1", "ORA2","ORCLCDB"});
                ((JComboBox<?>) inputField).setSelectedItem(value != null ? value : "ORA2");
            } else {
                inputField = new JTextField(value != null ? value.toString() : "", 20);
            }

            originalValues.put(fieldName, value);
            fieldInputs.put(fieldName, inputField);
            panel.add(label);
            panel.add(inputField);
        }

        JButton saveButton = new JButton("GUARDAR");
        saveButton.addActionListener(e -> {
            try {
                UserProperties newProperties = new UserProperties();

                for (Map.Entry<String, JComponent> entry : fieldInputs.entrySet()) {
                    String fieldName = entry.getKey();
                    JComponent component = entry.getValue();

                    Object value;
                    if (component instanceof JTextField) {
                        value = ((JTextField) component).getText();
                    } else if (component instanceof JPasswordField) {
                        value = new String(((JPasswordField) component).getPassword());
                    } else if (component instanceof JComboBox) {
                        value = ((JComboBox<?>) component).getSelectedItem();
                    } else {
                        continue;
                    }

                    // Validaciones básicas
                    if (fieldName.equals("ipServer") && !value.toString().matches("^\\d{1,3}(\\.\\d{1,3}){3}$")) {
                        JOptionPane.showMessageDialog(this, "IP no válida.");
                        return;
                    }
                    if ((fieldName.equals("userName") || fieldName.equals("userPassword"))
                            && !value.toString().matches("[a-zA-Z0-9]+")) {
                        JOptionPane.showMessageDialog(this, "Usuario o contraseña inválidos.");
                        return;
                    }

                    // Setter dinámico
                    String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    try {
                        Method setter = null;
                        for (Method m : UserProperties.class.getMethods()) {
                            if (m.getName().equals(setterName) && m.getParameterCount() == 1) {
                                setter = m;
                                break;
                            }
                        }

                        if (setter == null) continue;

                        Class<?> paramType = setter.getParameterTypes()[0];
                        if (paramType == int.class || paramType == Integer.class) {
                            int parsed = value.toString().matches("\\d{4}") ? Integer.parseInt(value.toString()) : 1521;
                            setter.invoke(newProperties, parsed);
                        } else {
                            setter.invoke(newProperties, paramType.cast(value));
                        }
                        if (value != null) {
                            originalValues.put(fieldName, value.toString());
						}
                        
                        frame.setConfigString(nombreSeleccionado);
                    } catch (Exception ex) {
                        Logger.logError(ex);
                        ex.printStackTrace();
                    }
                }

                // Obtener nombre de guardado: del campo o del comboBox
                String nuevoNombre = !nuevoNombreField.getText().isEmpty() ? nuevoNombreField.getText().trim()
                        : (String) nombreCombo.getSelectedItem();

                if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debes introducir o seleccionar un nombre para guardar la configuración.");
                    return;
                }
                
             // Guardar configuración
                XmlBuilder.escribirXML(nuevoNombre, newProperties);

                // Añadir al combo si no está
                boolean yaExiste = false;
                for (int i = 0; i < nombreCombo.getItemCount(); i++) {
                    if (nuevoNombre.equals(nombreCombo.getItemAt(i))) {
                        yaExiste = true;
                        break;
                    }
                }
                if (!yaExiste) {
                    nombreCombo.addItem(nuevoNombre);
                }
                
                frame.setConfigString(nuevoNombre);

                // GUARDAR EL ÚLTIMO USUARIO EN config.properties
                config.setProperty("ultimo_usuario", nuevoNombre); // o nombreSeleccionado si lo prefieres
                try (FileOutputStream fos = new FileOutputStream("config.properties")) {
                    config.store(fos, "Configuración de la app");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                changed = false;
                nuevoNombreLabel.setVisible(false);
                nuevoNombreField.setVisible(false);
                JOptionPane.showMessageDialog(this, "Configuración guardada correctamente.");
                cardLayout.show(contentPanel, "consulta");

                this.resetCampos(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la configuración.");
                Logger.logError(ex);
                ex.printStackTrace();
            }
        });


        JButton cancelButton = new JButton("CANCELAR");
        cancelButton.addActionListener(e -> {
            // Validar que haya un nombre seleccionado
            String seleccionado = (String) nombreCombo.getSelectedItem();
            if (seleccionado == null || seleccionado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debes seleccionar una configuración antes de cancelar.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return; // No continúa si no hay selección válida
            }

            hasChanges();
            if (changed) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Hay cambios no guardados. ¿Estás seguro de que quieres cancelar?",
                        "Cancelar sin guardar",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    cardLayout.show(contentPanel, "consulta");
                    this.resetCampos();
                }
            } else {
                cardLayout.show(contentPanel, "consulta");
                this.resetCampos();
            }
        });
        
        panel.add(saveButton);
        panel.add(cancelButton);

        add(panel);
        setVisible(true);
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    private void hasChanges() {
        changed = false; // reinicia el flag antes de empezar

        for (Map.Entry<String, JComponent> entry : fieldInputs.entrySet()) {
            String fieldName = entry.getKey();
            JComponent component = entry.getValue();
            Object originalValue = originalValues.get(fieldName);

            Object currentValue = null;

            if (component instanceof JTextField) {
                currentValue = ((JTextField) component).getText();
            } else if (component instanceof JPasswordField) {
                currentValue = new String(((JPasswordField) component).getPassword());
            } else if (component instanceof JComboBox<?>) {
                currentValue = ((JComboBox<?>) component).getSelectedItem();
            }

            // Compara teniendo en cuenta los nulls
            if (currentValue == null && originalValue != null ||
                currentValue != null && !currentValue.toString().equals(originalValue.toString())) {
                changed = true;
                break; // ya ha cambiado, no hace falta seguir
            }
        }
    }

    
    public void resetCampos() {
        nombreCombo.setSelectedIndex(0); // selecciona el elemento vacío

        // Limpia todos los campos del formulario
        for (String fieldName : fieldInputs.keySet()) {
            JComponent component = fieldInputs.get(fieldName);

            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JPasswordField) {
                ((JPasswordField) component).setText("");
            } else if (component instanceof JComboBox<?>) {
                ((JComboBox<?>) component).setSelectedIndex(1);

            nuevoNombreField.setText("");
            nuevoNombreField.setVisible(false);
            nuevoNombreLabel.setVisible(false);
           
            originalValues.put(fieldName, null);
        }
    }
}
    
    private boolean configFileExists() {
        File configFile = new File("resources" + File.separator + "config.xml");
        return configFile.exists();
    }
}
