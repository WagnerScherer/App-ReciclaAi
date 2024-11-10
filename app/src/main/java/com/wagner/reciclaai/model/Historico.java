package com.wagner.reciclaai.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Historico {
    private String idUsuario;
    private String dataAgendamento;
    private String idPontoColeta;
    private List<String> tipoMaterial;

    public Historico() {
        this.tipoMaterial = new ArrayList<>();
    }

    public Historico(String idUsuario, String dataAgendamento, String idPontoColeta, List<String> tipoMaterial) {
        this.idUsuario = idUsuario;
        this.dataAgendamento = dataAgendamento;
        this.idPontoColeta = idPontoColeta;
        this.tipoMaterial = tipoMaterial != null ? tipoMaterial : new ArrayList<>();
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(String dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public String getIdPontoColeta() { return idPontoColeta; }
    public void setIdPontoColeta(String idPontoColeta) { this.idPontoColeta = idPontoColeta; }

    public List<String> getTipoMaterial() { return tipoMaterial != null ? tipoMaterial : new ArrayList<>(); }
    public void setTipoMaterial(List<String> tipoMaterial) {
        this.tipoMaterial = tipoMaterial != null ? tipoMaterial : new ArrayList<>();
    }

    // Método para converter Timestamp para String
    public static String formatarData(Timestamp timestamp) {
        if (timestamp == null) return "Data não disponível";
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
