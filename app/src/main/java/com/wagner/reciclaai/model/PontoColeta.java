package com.wagner.reciclaai.model;

public class PontoColeta {

    private String nomeLocalColeta;
    private String emailPC;
    private String fonePC;
    private String whatsappPC;
    private String enderecoPC;
    private String numeroPC;
    private String complementoPC;
    private String bairroPC;
    private String cidadePC;
    private String estadoPC;
    private String data;
    private String sitePC;

    public String getWhatsAppPC() {
        return whatsappPC;
    }

    public void setWhatsAppPC(String whatsappPC) {
        this.whatsappPC = whatsappPC;
    }

    public PontoColeta() {
        // Construtor vazio
    }

    public String getNomeLocalColeta() {
        return nomeLocalColeta;
    }

    public void setNomeLocalColeta(String nomeLocalColeta) {
        this.nomeLocalColeta = nomeLocalColeta;
    }

    public String getEmailPC() {
        return emailPC;
    }

    public void setEmailPC(String emailPC) {
        this.emailPC = emailPC;
    }

    public String getFonePC() {
        return fonePC;
    }

    public void setFonePC(String fonePC) {
        this.fonePC = fonePC;
    }

    public String getEnderecoPC() {
        return enderecoPC;
    }

    public void setEnderecoPC(String enderecoPC) {
        this.enderecoPC = enderecoPC;
    }

    public String getNumeroPC() {
        return numeroPC;
    }

    public void setNumeroPC(String numeroPC) {
        this.numeroPC = numeroPC;
    }

    public String getComplementoPC() {
        return complementoPC;
    }

    public void setComplementoPC(String complementoPC) {
        this.complementoPC = complementoPC;
    }

    public String getBairroPC() {
        return bairroPC;
    }

    public void setBairroPC(String bairroPC) {
        this.bairroPC = bairroPC;
    }

    public String getCidadePC() {
        return cidadePC;
    }

    public void setCidadePC(String cidadePC) {
        this.cidadePC = cidadePC;
    }

    public String getEstadoPC() {
        return estadoPC;
    }

    public void setEstadoPC(String estadoPC) {
        this.estadoPC = estadoPC;
    }

    public String getSitePC() {
        return sitePC;
    }

    public void setSitePC(String sitePC) {
        this.sitePC = sitePC;
    }

    //public boolean RealizaColeta() {
    //    return realizaColeta;
    //}

    //public void setRealizaColeta(boolean realizaColeta) {
    //    this.realizaColeta = realizaColeta;
    //}

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



}
