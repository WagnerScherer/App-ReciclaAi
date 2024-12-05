package com.wagner.reciclaai.model;

import com.google.android.gms.common.api.internal.IStatusCallback;

import java.util.Date;
import java.util.List;

public class Agendamento {

    private String id_Agendamento;
    private String idUsuario;
    private String id_ponto_coleta;
    private List<String> tipo_material;
    private Date data_coleta;
    private Date data_agendamento;
    private int status_agendamento;

    //Construtor padrão vazio necessário para o Firestore
    public Agendamento(){

    }

    /**
     * Construtor para criar uma nova instancia de Agendamento.
     * @param idUsuario             UID do usuário que está realizando o agendamento.
     * @param id_ponto_coleta       ID do ponto de coleta selecionado.
     * @param tipo_material         Lista os tipos de material selecionados.
     * @param data_coleta           Data programada para a coleta ser realizada.
     * @param data_agendamento      Data que o agendamento foi realizado.
     * @param status_agendamento    Status do agendamento (1=Pendente, 2=Confirmado, 3=Rejeitado, 4=Cancelado)
     */
    public Agendamento(String idUsuario, String id_ponto_coleta, List<String> tipo_material,
                       Date data_coleta, Date data_agendamento, int status_agendamento) {

        this.idUsuario = idUsuario;
        this.id_ponto_coleta = id_ponto_coleta;
        this.tipo_material = tipo_material;
        this.data_coleta = data_coleta;
        this.data_agendamento = data_agendamento;
        this.status_agendamento = status_agendamento;
    }

    //Getters e Setters
    public String getId_Agendamento(){
        return id_Agendamento;
    }

    public void setId_Agendamento(String id_Agendamento) {
        this.id_Agendamento = id_Agendamento;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getId_ponto_coleta() {
        return id_ponto_coleta;
    }

    public void setId_ponto_coleta(String id_ponto_coleta) {
        this.id_ponto_coleta = id_ponto_coleta;
    }

    public List<String> getTipo_material() {
        return tipo_material;
    }

    public void setTipo_material(List<String> tipo_material) {
        this.tipo_material = tipo_material;
    }

    public Date getData_coleta() {
        return data_coleta;
    }

    public void setData_coleta(Date data_coleta) {
        this.data_coleta = data_coleta;
    }

    public Date getData_agendamento() {
        return data_agendamento;
    }

    public void setData_agendamento(Date data_agendamento) {
        this.data_agendamento = data_agendamento;
    }

    public int getStatus_agendamento() {
        return status_agendamento;
    }

    public void setStatus_agendamento(int status_agendamento) {
        this.status_agendamento = status_agendamento;
    }

    @Override
    public String toString() {
        return "Agendamento {" +
                "id_agendamento='" + id_Agendamento + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", idPontoColeta='" + id_ponto_coleta + '\'' +
                ", tipoMaterial=" + tipo_material +
                ", dataColeta=" + data_coleta +
                ", dataAgendamento=" + data_agendamento +
                ", status=" + status_agendamento +
                '}';
    }
}
