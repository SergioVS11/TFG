package com.viewnext.panels;

import com.viewnext.frames.HomeFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class SupportPanel extends JPanel {
    private JTextArea respuestaVisible = null;

    public SupportPanel(CardLayout cardLayout, JPanel fathercontentPanel) {
        setLayout(new BorderLayout()); // Usamos BorderLayout

        Map<String, String> preguntasRespuestas = new HashMap<>();
        preguntasRespuestas.put("¿Cómo realizar una consulta?",
                "Para realizar una consulta, debes dirigirte a la ventana de consultas haciendo clic en 'Consulta centros' en la ventana principal.\n" +
                        "Una vez allí, introduce los datos que deseas consultar y pulsa en 'Consulta'.");
        preguntasRespuestas.put("¿Cómo puedo modificar la configuración de la aplicación?",
                "Para modificar los datos contenidos en la configuración debes dirigirte a la ventana de configuración " +
                        "haciendo clic en 'Configuración Utilidad' en la ventana principal.\n" +
                        "Una vez allí, modifica los datos necesarios y pulsa en 'Guardar'.");
        preguntasRespuestas.put("¿Cómo puedo saber cuál es la versión de la aplicación?",
                "Para visualizar la versión de la aplicación dirígete a la ventana 'Terminal Financiero. Datos Centros' haciendo clic en el botón 'Acerca de' en la ventana principal.");
        preguntasRespuestas.put("¿Cómo cierro la aplicación?",
                "Para cerrar la aplicación pulsa en el botón 'Salir' en la ventana principal");

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Map.Entry<String, String> entry : preguntasRespuestas.entrySet()) {
            String pregunta = entry.getKey();
            String respuesta = entry.getValue();

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel labelPregunta = new JLabel(pregunta);
            labelPregunta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            labelPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextArea textArea = new JTextArea(respuesta);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setFocusable(false);
            textArea.setVisible(false);
            textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            textArea.setBorder(null);
            textArea.setMaximumSize(new Dimension(450, 50));

            labelPregunta.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (respuestaVisible != null) {
                        respuestaVisible.setVisible(false);
                    }
                    if (!textArea.isVisible()) {
                        textArea.setVisible(true);
                    }
                    respuestaVisible = textArea;
                    panel.revalidate();
                    panel.repaint();
                }
            });

            panel.add(labelPregunta);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(textArea);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            contentPanel.add(panel);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Botón de salida abajo
        JButton salirButton = new JButton("Salir");
        salirButton.addActionListener(e -> {
            cardLayout.show(fathercontentPanel, "consulta");
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(salirButton);

        // Añadir al layout principal
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
