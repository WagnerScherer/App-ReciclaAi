package com.wagner.reciclaai.model;

public class Faq {
    private String id;
    private String pergunta;
    private String resposta;

    public Faq() {}

    public Faq(String id, String question, String answer) {
        this.id = id;
        this.pergunta = pergunta;
        this.resposta = resposta;
    }

    public String getId() { return id; }
    public String getPergunta() { return pergunta; }
    public String getResposta() { return resposta; }
}

