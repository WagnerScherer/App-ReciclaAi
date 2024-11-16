package com.wagner.reciclaai.model;

public class Ranking {
    private String username;
    private int coletas;

    public Ranking() { }

    public Ranking(String username, int coletas) {
        this.username = username;
        this.coletas = coletas;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getColetas() {
        return coletas;
    }

    public void setColetas(int coletas) {
        this.coletas = coletas;
    }
}

