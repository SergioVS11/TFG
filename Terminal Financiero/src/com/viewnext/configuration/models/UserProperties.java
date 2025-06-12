package com.viewnext.configuration.models;

import javax.swing.*;

public class UserProperties {
    private String ipServer;
    private String userName;
    private String userPassword;
    private int port;
    private String instanceBBDD;
    private String nombre;
    private String apellido;

    private static UserProperties userProperties;

    public UserProperties(){
        this.ipServer = "000.000.000.000";
        this.userName = "defaultUser";
        this.userPassword = "defaultPass";
        setPort(1535);
        setInstanceBBDD("ORA2");
    }


    public void setIpServer(String ip) {
        if (ip != null) {
            String regex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
            if (ip.matches(regex)) {
                boolean valid = true;
                String[] nums = ip.split("\\.");
                for (String num : nums) {
                    int numInt = Integer.parseInt(num);
                    if (numInt < 0 || numInt > 255) {
                        valid = false;
                    }
                }
                if (valid) {
                    this.ipServer = ip;
                } else {
                    JOptionPane.showMessageDialog(null, "La dirección IP no es válida");
                }
            } else {
                JOptionPane.showMessageDialog(null, "La dirección IP no es válida");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Dirección IP no introducida");
        }
    }

    public void setUserName(String userName) {
        if (userName != null) {
            String regex = "[a-zA-Z0-9]+";
            if (userName.matches(regex)) {
                this.userName = userName;
            } else {
                JOptionPane.showMessageDialog(null, "Se ha detectado un valor incorrecto en el nombre");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Usuario no introducido");
        }
    }

    public void setUserPassword(String userPassword) {
        if (userPassword != null) {
            String regex = "[a-zA-Z0-9]+";
            if (userPassword.matches(regex)) {
                this.userPassword = userPassword;
            } else {
                JOptionPane.showMessageDialog(null, "Se ha detectado un valor incorrecto en la contraseña " + userPassword);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Contraseña no introducida");
        }
    }

    public void setPort(int port) {
        if (port >= 1000 && port <= 9999) {
            this.port = port;
        } else {
            JOptionPane.showMessageDialog(null, "Puerto inválido");
        }
    }

    public void setInstanceBBDD(String instanceBBDD) {
        if (instanceBBDD != null) {
            if (instanceBBDD.equals("ORA1") || instanceBBDD.equals("ORA2") || instanceBBDD.equals("centrosdb")) {
                this.instanceBBDD = instanceBBDD;
            } else {
                JOptionPane.showMessageDialog(null, "Instancia de la base de datos incorrecta");
            }
        } else {
        	this.instanceBBDD = "ORA2";
        }
    }

    public String getApellido() {
        return apellido;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getIpServer() {
        return ipServer;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public int getPort() {
        return port;
    }

    public String getInstanceBBDD() {
        return instanceBBDD;
    }

    public static UserProperties getUserProperties() {
        return userProperties;
    }

    public String getNombre() {
        return nombre;
    }


}
