package com.viewnext.frames;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import com.viewnext.configuration.panels.ConfigPanel;
import com.viewnext.database.panels.ConsultaPanel;
import com.viewnext.panels.AboutPanel;
import com.viewnext.panels.SupportPanel;
import com.viewnext.tools.Conexion;

import java.awt.*;
import java.io.*;
import java.util.Properties;

public class HomeFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Conexion conexion;
    private JLabel textLabel;
    private String configString;

    public HomeFrame() {
        conexion = new Conexion(); // Para poder manejar la conexión cuando se cambia de JPanel.
        this.setSize(900, 600);

        if(configFileExists()) {
            this.setTitle("Terminal Financiero. Consulta datos centros. V1 R2. Consulta Datos");
        }else {
            this.setTitle("Terminal Financiero. Consulta datos centros. V1 R2. Configuración");
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // Crear la barra de menú
        JMenuBar menuBar = new JMenuBar();

        // Menú "Operaciones"
        JMenu menuOperaciones = new JMenu("Operaciones");
        JMenuItem itemConsulta = new JMenuItem("Consulta Centros");
        JMenuItem itemConfig = new JMenuItem("Configuración Utilidad");
        menuOperaciones.add(itemConsulta);
        menuOperaciones.add(itemConfig);

        // Menú "Ayuda"
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemSoporte = new JMenuItem("Soporte");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        menuAyuda.add(itemSoporte);
        menuAyuda.add(itemAcerca);

        // Menú "Salir"
        JMenu menuSalir = new JMenu("Salir");
        JMenuItem itemSalir = new JMenuItem("Salir");
        menuSalir.add(itemSalir);

        // Agregar los menús a la barra
        menuBar.add(menuOperaciones);
        menuBar.add(menuAyuda);
        menuBar.add(menuSalir);

        // Imagen
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon bannerIcon = new ImageIcon(getClass().getClassLoader().getResource("com/viewnext/resources/bdd.png"));
        imageLabel.setIcon(bannerIcon);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setPreferredSize(new Dimension(this.getWidth(), bannerIcon.getIconHeight()));
        
        textLabel = new JLabel("");
        textLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrado horizontal

        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            configString = config.getProperty("ultimo_usuario");

            // Verificar que no sea null o vacío
            if (configString != null && !configString.isEmpty()) {
                textLabel.setText("Está usando la sigiente configuración: " + configString);
            }
        } catch (IOException ex) {
            System.out.println("Archivo config.properties no encontrado");
        }


        // Crear panel superior y añadir barra de menú + imagen
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(menuBar, BorderLayout.NORTH);
        topPanel.add(imagePanel, BorderLayout.CENTER);
        topPanel.add(textLabel,BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);

        // Panel con los distintos contenidos de la aplicación
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                new BevelBorder(BevelBorder.LOWERED)
        ));
        ConfigPanel configPanel = new ConfigPanel(cardLayout, contentPanel, this);
        contentPanel.add(new ConsultaPanel(conexion), "consulta");
        contentPanel.add(configPanel, "config");
        contentPanel.add(new SupportPanel(cardLayout, contentPanel), "soporte");
        contentPanel.add(new AboutPanel(cardLayout, contentPanel), "acerca");

        if (configFileExists()) {
            cardLayout.show(contentPanel, "consulta");
        } else {
            cardLayout.show(contentPanel, "config");
        }

        contentPanel.setPreferredSize(new Dimension(this.getWidth(), 500));
        this.add(contentPanel, BorderLayout.CENTER);

        // Acciones de los ítems del menú
        itemConsulta.addActionListener(e -> {
        	cargarUltimoUsuario();
            closeConnectionAndShowPanel("consulta");
            this.setTitle("Terminal Financiero. Consulta datos centros. V1 R2. Consulta Datos");
        });

        itemConfig.addActionListener(e -> {
        	configPanel.resetCampos();
        	closeConnectionAndShowPanel("config");
            this.setTitle("Terminal Financiero. Consulta datos centros. V1 R2. Configuración");
            
        });

        itemSoporte.addActionListener(e -> {
            closeConnectionAndShowPanel("soporte");
            this.setTitle("Terminal Financiero. Consulta datos centros. V1 R2. Soporte");
        });

        itemAcerca.addActionListener(e -> {
            closeConnectionAndShowPanel("acerca");
            this.setTitle("Terminal Financiero. Consulta datos centros. V1 R2. Acerca de");
        });

        itemSalir.addActionListener(e -> {
            closeConnectionAndExit();
        });

        this.setVisible(true);
    }

    private void closeConnectionAndShowPanel(String panelName) {
        conexion.cerrarConexion();
        cardLayout.show(contentPanel, panelName);
    }

    private void closeConnectionAndExit() {
        conexion.cerrarConexion();
        System.exit(0);
    }

    private boolean configFileExists() {
        File configFile = new File("resources" + File.separator + "config.xml");
        return configFile.exists();
    }
    
    private void cargarUltimoUsuario() {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            String ultimoUsuario = config.getProperty("ultimo_usuario");
            if (ultimoUsuario != null && !ultimoUsuario.isEmpty()) {
                textLabel.setText("Está usando la sigiente configuración: " + ultimoUsuario);
            }
        } catch (IOException ex) {
            System.err.println("No se pudo cargar el archivo de configuración.");
            ex.printStackTrace();
        }
    }
    
    public void setConfigString(String configString) {
    	this.configString = obtenerUltimoUsuario();
        textLabel.setText("Está usando la sigiente configuración: " + configString);
	}
    
	public static String obtenerUltimoUsuario() {
	    Properties props = new Properties();
	    try (FileInputStream fis = new FileInputStream("config.properties")) {
	        props.load(fis);
	        return props.getProperty("ultimo_usuario");
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}
