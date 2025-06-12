package com.viewnext.panels;

import javax.swing.*;
import java.awt.*;

public class AboutPanel extends JPanel {
    private float version = 1.2F;

    public AboutPanel(CardLayout layout, JPanel contentPanel) {
        this.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("TERMINAL FINANCIERO. DATOS CENTROS", SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        JLabel versionLabel = new JLabel("Versión " + this.version + " del programa.", SwingConstants.CENTER);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(versionLabel, BorderLayout.CENTER);

        // Etiqueta autores en azul
        JLabel autoresLabel = new JLabel(
            "<html><div style='color:blue;'>Realizado por Francisco Manuel Martin Cabello y Sergio Vargas Sánchez</div></html>",
            SwingConstants.CENTER
        );
        autoresLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JButton salirButton = new JButton("Volver");
        salirButton.addActionListener(e -> layout.show(contentPanel, "consulta"));

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        southPanel.add(autoresLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(salirButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(southPanel, BorderLayout.SOUTH);
    }

    public float getVersion() {
        return this.version;
    }
}
