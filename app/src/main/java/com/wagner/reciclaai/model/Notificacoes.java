package com.wagner.reciclaai.model;

public class Notificacoes {
    private String idNotificacao;
    private String idUsuario;
    private String titulo;
    private String mensagem;
    private boolean status; // T = lida, F = não lida

    // Construtor vazio necessário para o Firebase
    public Notificacoes() {}

    // Construtor com parâmetros
    public Notificacoes(String idNotificacao, String idUsuario, String titulo, String mensagem, boolean status) {
        this.idNotificacao = idNotificacao;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.status = status;
    }

    // Getters e Setters
    public String getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(String idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
