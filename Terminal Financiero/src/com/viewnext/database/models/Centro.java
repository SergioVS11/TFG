package com.viewnext.database.models;

import java.sql.Timestamp;

public class Centro {
    private Integer empresa;
    private Integer centro;
    private Integer terminal;
    private String versionTF;
    private String ipTerminal;
    private String nameMaquina;
    private Timestamp fechaHora;

   
    public Centro(Integer empresa, Integer centro, Integer terminal, String versionTF, String ipTerminal, String nameMaquina, Timestamp fechaHora) {
        this.empresa = empresa;
        this.centro = centro;
        this.terminal = terminal;
        this.versionTF = versionTF;
        this.ipTerminal = ipTerminal;
        this.nameMaquina = nameMaquina;
        this.fechaHora = fechaHora;
    }

    
   

	public Integer getEmpresa() { return empresa; }
    public Integer getCentro() { return centro; }
    public Integer getTerminal() { return terminal; }
    public String getVersionTF() { return versionTF; }
    public String getIpTerminal() { return ipTerminal; }
    public String getNameMaquina() { return nameMaquina; }
    public Timestamp getFechaHora() { return fechaHora; }
}
