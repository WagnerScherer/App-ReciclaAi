package com.wagner.reciclaai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.Util.RecargaListener;
import com.wagner.reciclaai.model.AgendamentoSolicitado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgendamentoRecyclerAdapter extends RecyclerView.Adapter<AgendamentoRecyclerAdapter.AgendamentoViewHolder> {
    private List<AgendamentoSolicitado> agendamentos;
    private Context context;
    private FirebaseFirestore db;
    private RecargaListener recargaListener;

    public AgendamentoRecyclerAdapter(List<AgendamentoSolicitado> agendamentos, Context context, RecargaListener recargaListener) {
        this.agendamentos = agendamentos;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.recargaListener = recargaListener;
    }

    @NonNull
    @Override
    public AgendamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_agendamento, parent, false);
        return new AgendamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendamentoViewHolder holder, int position) {
        AgendamentoSolicitado agendamento = agendamentos.get(position);

        db.collection("USUARIOS").document(agendamento.getIdUsuario())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nomeUsuario = documentSnapshot.getString("nome");
                        holder.textUserName.setText("Usuário: " + (nomeUsuario != null ? nomeUsuario : "Desconhecido"));

                        String endereco = formatarEndereco(documentSnapshot);
                        holder.textUserAdress.setText("Endereço: " + (endereco != null ? endereco : "Não disponível"));
                    }
                });

        holder.textDataColeta.setText("Data: " + agendamento.getDataColeta());
        List<String> materiais = agendamento.getTipoMaterial();
        holder.textMateriaisColetados.setText("Materiais: " + formatarMateriais(materiais));
        String statusTexto = formatarStatus(agendamento.getStatusAgendamento());
        holder.textStatusDaColeta.setText("Status: " + statusTexto);

        holder.buttonRejeitar.setOnClickListener(v -> {
            if (agendamento.getStatusAgendamento() != 1) {
                Toast.makeText(context, "Esta coleta não está mais pendente!", Toast.LENGTH_SHORT).show();
            } else {
                criarNotificacaoRejeitar(agendamento);
            }
        });

        holder.buttonAceitar.setOnClickListener(v -> {
            if (agendamento.getStatusAgendamento() != 1) {
                Toast.makeText(context, "Esta coleta não está mais pendente!", Toast.LENGTH_SHORT).show();
            } else {
                criarNotificacaoAceitar(agendamento);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (agendamentos != null) ? agendamentos.size() : 0;
    }

    // Método para atualizar a lista de agendamentos
    public void atualizarLista(List<AgendamentoSolicitado> novaLista) {
        if (novaLista != null) {
            agendamentos.clear();
            agendamentos.addAll(novaLista);
            notifyDataSetChanged(); // Atualizar RecyclerView
        }
    }

    private void criarNotificacaoRejeitar(AgendamentoSolicitado agendamento) {
        db.collection("PONTOSCOLETA").document(agendamento.getIdPontoColeta())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nomePonto = documentSnapshot.getString("nome");
                        String dataColeta = agendamento.getDataColeta();
                        enviarNotificacaoRejeitar(agendamento, nomePonto, dataColeta);
                    } else {
                        Log.e("Notificacao", "Ponto de coleta não encontrado!");
                        Toast.makeText(context, "Erro ao localizar o ponto de coleta.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("Notificacao", "Erro ao buscar ponto de coleta", e));
    }

    private void enviarNotificacaoRejeitar(AgendamentoSolicitado agendamento, String nomePonto, String dataColeta) {
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("id_usuario", agendamento.getIdUsuario());
        notificacao.put("status", false);
        notificacao.put("titulo", "Resposta à solicitação de coleta");
        notificacao.put("mensagem",
                "O ponto de coleta \"" + nomePonto + "\" recusou a sua coleta para o dia " + dataColeta + ".\n" +
                        "Para maiores informações, contate o ponto de coleta.");

        db.collection("NOTIFICACOES").add(notificacao)
                .addOnSuccessListener(documentReference -> atualizarStatus(agendamento, 2))
                .addOnFailureListener(e -> Log.e("Notificacao", "Erro ao enviar notificação", e));
    }

    private void criarNotificacaoAceitar(AgendamentoSolicitado agendamento) {
        db.collection("PONTOSCOLETA").document(agendamento.getIdPontoColeta())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nomePonto = documentSnapshot.getString("nome");
                        String dataColeta = agendamento.getDataColeta();
                        enviarNotificacaoAceitar(agendamento, nomePonto, dataColeta);
                    }
                })
                .addOnFailureListener(e -> Log.e("Notificacao", "Erro ao buscar ponto de coleta", e));
    }

    private void enviarNotificacaoAceitar(AgendamentoSolicitado agendamento, String nomePonto, String dataColeta) {
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("id_usuario", agendamento.getIdUsuario());
        notificacao.put("status", false);
        notificacao.put("titulo", "Resposta à solicitação de coleta");
        notificacao.put("mensagem",
                "O ponto de coleta \"" + nomePonto + "\" aceitou a sua coleta para o dia " + dataColeta + ".\n" +
                        "Na data marcada, um funcionário devidamente identificado realizará a coleta no endereço informado."
        );
        db.collection("NOTIFICACOES").add(notificacao)
                .addOnSuccessListener(documentReference -> atualizarStatus(agendamento, 3))
                .addOnFailureListener(e -> Log.e("Notificacao", "Erro ao enviar notificação", e));
    }

    private void atualizarStatus(AgendamentoSolicitado agendamento, int status) {
        db.collection("AGENDAMENTOS").document(agendamento.getId())
                .update("status_agendamento", status)
                .addOnSuccessListener(aVoid -> recargaListener.recarregarSolicitacoes());
    }

    private String formatarMateriais(List<String> materiais) {
        if (materiais == null || materiais.isEmpty()) {
            return "Nenhum material coletado";
        }
        StringBuilder sb = new StringBuilder();
        for (String material : materiais) {
            switch (material) {
                case "1": sb.append("Pilhas/Baterias, "); break;
                case "2": sb.append("Óleo de Cozinha, "); break;
                case "3": sb.append("Lâmpadas, "); break;
                case "4": sb.append("Eletrônicos, "); break;
                default: sb.append("Desconhecido, "); break;
            }
        }
        return sb.substring(0, sb.length() - 2);
    }

    private String formatarEndereco(DocumentSnapshot documentSnapshot) {
        StringBuilder endereco = new StringBuilder();
        endereco.append(documentSnapshot.getString("endereco")).append(", ");
        endereco.append(documentSnapshot.getString("numero")).append(", ");
        endereco.append(documentSnapshot.getString("bairro"));
        return endereco.toString();
    }

    private String formatarStatus(int status) {
        switch (status) {
            case 1: return "Pendente";
            case 2: return "Recusada";
            case 3: return "Confirmada";
            default: return "Desconhecido";
        }
    }

    public static class AgendamentoViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textUserAdress, textDataColeta, textMateriaisColetados, textStatusDaColeta;
        Button buttonAceitar, buttonRejeitar;

        public AgendamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUserAdress = itemView.findViewById(R.id.textUserAdress);
            textDataColeta = itemView.findViewById(R.id.textDataColeta);
            textMateriaisColetados = itemView.findViewById(R.id.textMateriaisColetados);
            textStatusDaColeta = itemView.findViewById(R.id.textStatusDaColeta);
            buttonAceitar = itemView.findViewById(R.id.buttonAceitar);
            buttonRejeitar = itemView.findViewById(R.id.buttonRejeitar);
        }
    }

    // Método para obter a lista de agendamentos
    public List<AgendamentoSolicitado> getAgendamentos() {
        return new ArrayList<>(agendamentos); // Retorna uma cópia da lista para evitar manipulação direta
    }

}