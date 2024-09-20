package com.wagner.reciclaai.model;

import com.google.firebase.Timestamp;

public class Avaliacao {
    private String uidUsuario;
    private String idPontoColeta;
    private int nota;
    private String comentario;
    private String nomeUsuario;
    private Timestamp data;

    public Avaliacao() {
        // Construtor vazio necessário para o Firebase
    }

    public Avaliacao(String uidUsuario, String idPontoColeta, int nota, String comentario, String nomeUsuario, Timestamp data) {
        this.uidUsuario = uidUsuario;
        this.idPontoColeta = idPontoColeta;
        this.nota = nota;
        this.comentario = comentario;
        this.nomeUsuario = nomeUsuario;
        this.data = data;
    }

    // Getters e setters
    public String getUidUsuario() { return uidUsuario; }

    public void setUidUsuario(String uidUsuario) { this.uidUsuario = uidUsuario; }

    public String getIdPontoColeta() { return idPontoColeta; }

    public void setIdPontoColeta(String idPontoColeta) { this.idPontoColeta = idPontoColeta; }

    public int getNota() { return nota; }

    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }

    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getNomeUsuario() { return nomeUsuario; }

    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public Timestamp getData() { return data; }

    public void setData(Timestamp data) { this.data = data; }
}