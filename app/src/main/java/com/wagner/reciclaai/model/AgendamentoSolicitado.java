package com.wagner.reciclaai.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgendamentoSolicitado {
    private String id;
    private String idUsuario;
    private String endereco;
    private String dataColeta; // Mudamos para String
    private List<String> tipoMaterial;
    private String idPontoColeta;
    private int statusAgendamento;

    public AgendamentoSolicitado() {}

    public AgendamentoSolicitado(String idUsuario, String endereco,  Timestamp dataColeta, List<String> tipoMaterial, String idPontoColeta, int statusAgendamento) {
        this.idUsuario = idUsuario;
        this.endereco = endereco;
        this.dataColeta = formatarData(dataColeta);
        this.tipoMaterial = tipoMaterial;
        this.idPontoColeta = idPontoColeta;
        this.statusAgendamento = statusAgendamento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getEndereco () { return endereco; }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDataColeta() { return dataColeta; }
    public void setDataColeta(Timestamp dataColeta) { this.dataColeta = formatarData(dataColeta); }

    public List<String> getTipoMaterial() { return tipoMaterial; }
    public void setTipoMaterial(List<String> tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    public String getIdPontoColeta() { return idPontoColeta; }
    public void setIdPontoColeta(String idPontoColeta) { this.idPontoColeta = idPontoColeta; }

    public int getStatusAgendamento() { return statusAgendamento; }
    public void setStatusAgendamento(int statusAgendamento) { this.statusAgendamento = statusAgendamento; }

    // Método para converter Timestamp para String
    public static String formatarData(Timestamp timestamp) {
        if (timestamp == null) return "Data não disponível";
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
