package com.wagner.reciclaai.model;

public class Favorito {
    private String uidUsuario;
    private String idPontoColeta;

    public Favorito() {
        // Construtor vazio necessário para o Firestore
    }

    public Favorito(String uidUsuario, String idPontoColeta) {
        this.uidUsuario = uidUsuario;
        this.idPontoColeta = idPontoColeta;
    }

    // Getters e Setters
    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getIdPontoColeta() {
        return idPontoColeta;
    }

    public void setIdPontoColeta(String idPontoColeta) {
        this.idPontoColeta = idPontoColeta;
    }
}
