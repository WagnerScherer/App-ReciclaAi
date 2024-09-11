/*
meu adapter atual consultapontoadapter


package com.wagner.reciclaai.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.Util.CadPontoColetaActivity;
import com.wagner.reciclaai.model.Favorito;
import com.wagner.reciclaai.model.PontoColeta;

import java.util.List;

public class ConsultaPontoAdapter extends RecyclerView.Adapter<ConsultaPontoAdapter.PontoColetaViewHolder> {

    private List<PontoColeta> listaPontosColeta;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ConsultaPontoAdapter(List<PontoColeta> listaPontosColeta, Context context) {
        this.listaPontosColeta = listaPontosColeta;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public PontoColetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_consulta_pontocoleta, parent, false);
        return new PontoColetaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PontoColetaViewHolder holder, int position) {
        PontoColeta pontoColeta = listaPontosColeta.get(position);

        // Preencher os TextViews com os dados do ponto de coleta

        // Nome
        holder.textViewNomePonto.setText(pontoColeta.getNome());

        // Endereço completo (Endereço + Número)
        String enderecoCompleto = pontoColeta.getEndereco() + ", " + pontoColeta.getNumero();
        holder.textViewEnderecoPonto.setText(enderecoCompleto);

        // Bairro
        holder.textViewBairroPonto.setText(pontoColeta.getBairro());

        // Cidade e Estado
        String cidadeUf = pontoColeta.getCidade() + " - " + pontoColeta.getEstado();
        holder.textViewCidadeUfPonto.setText(cidadeUf);

        // Telefone
        holder.textViewTelefonePonto.setText(pontoColeta.getFone());

        // Buscar os materiais coletados
        db.collection("PONTOSCOLETA_MATERIAIS")
                .whereEqualTo("id_ponto_coleta", pontoColeta.getId_PC())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder materiais = new StringBuilder();
                        for (DocumentSnapshot document : task.getResult()) {
                            int idMaterial = document.getLong("id_material").intValue();
                            switch (idMaterial) {
                                case 1:
                                    materiais.append("Pilha/Bateria\n");
                                    break;
                                case 2:
                                    materiais.append("Óleo de Cozinha\n");
                                    break;
                                case 3:
                                    materiais.append("Lâmpada\n");
                                    break;
                                case 4:
                                    materiais.append("Eletrônicos\n");
                                    break;
                            }
                        }
                        holder.textViewMateriaisColetados.setText(materiais.toString());
                    } else {
                        holder.textViewMateriaisColetados.setText("Erro ao buscar materiais");
                    }
                });

        // Verificar se o ponto de coleta já é favorito
        String uidUsuario = auth.getCurrentUser().getUid();
        db.collection("FAVORITOS")
                .whereEqualTo("uidUsuario", uidUsuario)
                .whereEqualTo("idPontoColeta", pontoColeta.getId_PC())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_24_preenchida);
                    } else {
                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_border_24_vazia);
                    }
                });

        // Ação de favoritar
        holder.imageButtonFavoritar.setOnClickListener(v -> {
            db.collection("FAVORITOS")
                    .whereEqualTo("uidUsuario", uidUsuario)
                    .whereEqualTo("idPontoColeta", pontoColeta.getId_PC())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot favoritoDoc = task.getResult().getDocuments().get(0);
                            db.collection("FAVORITOS").document(favoritoDoc.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Favorito removido", Toast.LENGTH_SHORT).show();
                                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_border_24_vazia);
                                    });
                        } else {
                            Favorito favorito = new Favorito(uidUsuario, pontoColeta.getId_PC());
                            db.collection("FAVORITOS").add(favorito)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(context, "Favoritado", Toast.LENGTH_SHORT).show();
                                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_24_preenchida);
                                    });
                        }
                    });
        });

        // Ação de alterar
        holder.buttonAlterarPonto.setOnClickListener(v -> {
            Intent intent = new Intent(context, CadPontoColetaActivity.class);
            intent.putExtra("idPontoColeta", pontoColeta.getId_PC()); // Passar o ID do ponto de coleta
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPontosColeta.size();
    }

    public static class PontoColetaViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNomePonto, textViewEnderecoPonto, textViewBairroPonto;
        TextView textViewCidadeUfPonto, textViewTelefonePonto, textViewMateriaisColetados;
        ImageButton imageButtonFavoritar;
        Button buttonAlterarPonto;

        public PontoColetaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar os TextViews
            textViewNomePonto = itemView.findViewById(R.id.textViewNomePonto);
            textViewEnderecoPonto = itemView.findViewById(R.id.textViewEnderecoPonto);
            textViewBairroPonto = itemView.findViewById(R.id.textViewBairroPonto);
            textViewCidadeUfPonto = itemView.findViewById(R.id.textViewCidadeUfPonto);
            textViewTelefonePonto = itemView.findViewById(R.id.textViewTelefonePonto);
            textViewMateriaisColetados = itemView.findViewById(R.id.textViewMateriaisColetados);

            // Inicializar o botão de favoritar e alterar
            imageButtonFavoritar = itemView.findViewById(R.id.imageButtonFavoritar);
            buttonAlterarPonto = itemView.findViewById(R.id.buttonAlterarPonto);
        }
    }
}
 */