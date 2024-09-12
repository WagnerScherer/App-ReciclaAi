package com.wagner.reciclaai.model;

public class PontoColeta {

    private String nome;
    private String emailPC;
    private String fone;
    private String whatsappPC;
    private String endereco;
    private String numero;
    private String complementoPC;
    private String bairro;
    private String cidade;
    private String uf;
    private String data;
    private String sitePC;
    private String id_PC;
    private String materiaisColetados;
    private String pessoa_contato;
    private String horario_funcionamento;

    public String getWhatsAppPC() {
        return whatsappPC;
    }

    public void setWhatsAppPC(String whatsappPC) {
        this.whatsappPC = whatsappPC;
    }

    public PontoColeta() {
        // Construtor vazio
    }

    public PontoColeta(String nome, String endereco, String numero, String bairro, String cidade, String uf, String fone, String pessoa_contato, String horario_funcionamento) {
        this.nome = nome;
        this.endereco = endereco;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.fone = fone;
        this.pessoa_contato = pessoa_contato;
        this.horario_funcionamento = horario_funcionamento;
    }

    public String getId_PC() {
        return id_PC;
    }

    public void setId_PC(String id_PC) {
        this.id_PC = id_PC;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmailPC() {
        return emailPC;
    }

    public void setEmailPC(String emailPC) {
        this.emailPC = emailPC;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplementoPC() {
        return complementoPC;
    }

    public void setComplementoPC(String complementoPC) {
        this.complementoPC = complementoPC;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
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

    public String getMateriaisColetados() {
        return materiaisColetados;
    }

    public void setMateriaisColetados(String materiaisColetados) {
        this.materiaisColetados = materiaisColetados;
    }

    public String getPessoa_contato() {
        return pessoa_contato;
    }

    public void setPessoa_contato(String pessoa_contato) {
        this.pessoa_contato = pessoa_contato;

    }

    public String getHorario_funcionamento() {
        return horario_funcionamento;
    }

    public void setHorario_funcionamento(String horario_funcionamento) {
        this.horario_funcionamento = horario_funcionamento;
    }

}
