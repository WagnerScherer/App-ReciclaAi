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

        // Buscar nome e endereço do usuário a partir do idUsuario
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

        // Exibir a data da coleta usando o método formatado do model
        holder.textDataColeta.setText("Data: " + agendamento.getDataColeta());

        // Exibir materiais coletados
        List<String> materiais = agendamento.getTipoMaterial();
        holder.textMateriaisColetados.setText("Materiais: " + formatarMateriais(materiais));

        // Exibir status da coleta
        String statusTexto = formatarStatus(agendamento.getStatusAgendamento());
        holder.textStatusDaColeta.setText("Status: " + statusTexto);

        // Configurar botão Rejeitar
        holder.buttonRejeitar.setOnClickListener(v -> {
            if (agendamento.getStatusAgendamento() != 1) {
                Toast.makeText(context, "Esta coleta não está mais pendente!", Toast.LENGTH_SHORT).show();
            } else {
                criarNotificacaoRejeitar(agendamento);
            }
        });

        // Configurar botão Aceitar
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

    private void criarNotificacaoRejeitar(AgendamentoSolicitado agendamento) {
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("idUsuario", agendamento.getIdUsuario());
        notificacao.put("status", false);
        notificacao.put("titulo", "Resposta de agendamento de coleta");
        notificacao.put("mensagem", "O ponto de coleta recusou a sua solicitação.");

        db.collection("NOTIFICACOES").add(notificacao)
                .addOnSuccessListener(documentReference -> {
                    atualizarStatus(agendamento, 2);
                });
    }

    private void criarNotificacaoAceitar(AgendamentoSolicitado agendamento) {
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("idUsuario", agendamento.getIdUsuario());
        notificacao.put("status", false);
        notificacao.put("titulo", "Resposta do seu agendamento de coleta");
        notificacao.put("mensagem", "O ponto de coleta aceitou a sua coleta.");

        db.collection("NOTIFICACOES").add(notificacao)
                .addOnSuccessListener(documentReference -> {
                    atualizarStatus(agendamento, 3);
                });
    }

    private void atualizarStatus(AgendamentoSolicitado agendamento, int status) {
        db.collection("AGENDAMENTOS").document(agendamento.getId())
                .update("status_agendamento", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Status atualizado!", Toast.LENGTH_SHORT).show();
                    recargaListener.recarregarSolicitacoes();
                });
    }

    /*
    private String formatarMateriais(List<String> materiais) {
        // Verifica se a lista é nula ou vazia antes de iterar
        if (materiais == null || materiais.isEmpty()) {
            return "Nenhum material coletado";
        }
        return String.join(", ", materiais);
    }

     */

    private String formatarMateriais(List<String> materiais) {
        if (materiais == null || materiais.isEmpty()) {
            return "Nenhum material coletado";
        }
        StringBuilder sb = new StringBuilder();
        for (String material : materiais) {
            switch (material) {
                case "1":
                    sb.append("Pilhas/Baterias, ");
                    break;
                case "2":
                    sb.append("Óleo de Cozinha, ");
                    break;
                case "3":
                    sb.append("Lâmpadas, ");
                    break;
                case "4":
                    sb.append("Eletrônicos, ");
                    break;
                default:
                    sb.append("Desconhecido, ");
                    break;
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2); // Remove a última vírgula e espaço extra
        }
        return sb.toString();
    }

    private String formatarStatus(int status) {
        switch (status) {
            case 1: return "Pendente";
            case 2: return "Recusada";
            case 3: return "Confirmada";
            default: return "Status desconhecido";
        }
    }

    // Método para formatar o endereço do usuário
    private String formatarEndereco(DocumentSnapshot documentSnapshot) {
        StringBuilder enderecoCompleto = new StringBuilder();

        String rua = documentSnapshot.getString("endereco");
        String numero = documentSnapshot.getString("numero");
        String bairro = documentSnapshot.getString("bairro");
        String cidade = documentSnapshot.getString("cidade");
        String uf = documentSnapshot.getString("estado");
        String complemento = documentSnapshot.getString("complemento");

        if (rua != null && !rua.isEmpty()) enderecoCompleto.append(rua);
        if (numero != null && !numero.isEmpty()) enderecoCompleto.append(", ").append(numero);
        if (bairro != null && !bairro.isEmpty()) enderecoCompleto.append(", ").append(bairro);
        if (cidade != null && !cidade.isEmpty()) enderecoCompleto.append(", ").append(cidade);
        if (uf != null && !uf.isEmpty()) enderecoCompleto.append(" - ").append(uf);
        if (complemento != null && !complemento.isEmpty()) enderecoCompleto.append(", ").append(complemento);

        return enderecoCompleto.toString();
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
}
