package com.wagner.reciclaai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.model.Historico;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wagner.reciclaai.R;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder> {
    private List<Historico> historicoList;
    private Context context;
    private FirebaseFirestore firestore;

    public HistoricoAdapter(List<Historico> historicoList, Context context) {
        this.historicoList = historicoList;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance(); // Inicializar o Firestore
    }

    @NonNull
    @Override
    public HistoricoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_historico, parent, false);
        return new HistoricoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoViewHolder holder, int position) {
        Historico historico = historicoList.get(position);

        // Buscar o nome do ponto de coleta usando o idPontoColeta
        buscarNomePontoColeta(historico.getIdPontoColeta(), holder.tituloHistorico);

        // Exibindo a data de agendamento
        holder.dataColeta.setText("Data: " + historico.getDataAgendamento());

        // Convertendo a lista de materiais para uma string legível
        List<String> materiais = historico.getTipoMaterial();
        String materiaisColetados = formatarMateriais(materiais);
        holder.materiaisColetados.setText("Materiais: " + materiaisColetados);
    }

    @Override
    public int getItemCount() {
        return historicoList.size();
    }

    // Método para buscar o nome do ponto de coleta
    private void buscarNomePontoColeta(String idPontoColeta, TextView tituloHistorico) {
        firestore.collection("PONTOSCOLETA").document(idPontoColeta)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nomePontoColeta = documentSnapshot.getString("nome");
                        tituloHistorico.setText("Ponto de coleta: " + nomePontoColeta);
                    } else {
                        tituloHistorico.setText("Ponto de coleta: Não encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    tituloHistorico.setText("Ponto de coleta: Erro ao carregar");
                    Log.e("HistoricoAdapter", "Erro ao buscar o nome do ponto de coleta: " + e.getMessage());
                });
    }

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

    public static class HistoricoViewHolder extends RecyclerView.ViewHolder {
        TextView tituloHistorico, dataColeta, materiaisColetados;

        public HistoricoViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloHistorico = itemView.findViewById(R.id.tituloHistorico);
            dataColeta = itemView.findViewById(R.id.dataColeta);
            materiaisColetados = itemView.findViewById(R.id.materiaisColetados);
        }
    }
}


