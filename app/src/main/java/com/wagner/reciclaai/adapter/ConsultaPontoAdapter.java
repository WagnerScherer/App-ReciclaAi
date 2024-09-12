package com.wagner.reciclaai.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultaPontoAdapter extends RecyclerView.Adapter<ConsultaPontoAdapter.PontoColetaViewHolder> {

    private List<PontoColeta> listaPontosColeta;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Map<String, Boolean> cacheFavoritos; // Cache para armazenar os favoritos localmente

    public ConsultaPontoAdapter(List<PontoColeta> listaPontosColeta, Context context) {
        this.listaPontosColeta = listaPontosColeta;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.cacheFavoritos = new HashMap<>(); // Inicializar o cache de favoritos
    }

    @NonNull
    @Override
    public PontoColetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_consulta_pontocoleta, parent, false);
        return new PontoColetaViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull PontoColetaViewHolder holder, int position) {
        PontoColeta pontoColeta = listaPontosColeta.get(position);

        // Preencher os TextViews com os dados do ponto de coleta
        if (holder.textViewNomePonto != null) {
            holder.textViewNomePonto.setText(pontoColeta.getNome());
            Log.d("Adapter", "Nome do Ponto: " + pontoColeta.getNome());
        }
        if (holder.textViewEnderecoPonto != null) {
            String enderecoCompleto = pontoColeta.getEndereco() + ", " + pontoColeta.getNumero();
            holder.textViewEnderecoPonto.setText(enderecoCompleto);
            Log.d("Adapter", "Endereço: " + enderecoCompleto);
        }
        if (holder.textViewBairroPonto != null) {
            holder.textViewBairroPonto.setText(pontoColeta.getBairro());
        }
        if (holder.textViewCidadeUfPonto != null) {
            String cidadeUfPonto = pontoColeta.getCidade() + "-" + pontoColeta.getUf();
            holder.textViewCidadeUfPonto.setText(cidadeUfPonto);
            Log.d("Adapter", "Cidade-UF: " + cidadeUfPonto);
        }
        if (holder.textViewPessoaContato != null) {
            holder.textViewPessoaContato.setText(pontoColeta.getPessoa_contato());
        }
        if (holder.textViewHorarioFuncionamento != null) {
            holder.textViewHorarioFuncionamento.setText(pontoColeta.getHorario_funcionamento());
        }
        if (holder.textViewTelefonePonto != null) {
            holder.textViewTelefonePonto.setText(pontoColeta.getFone());
        }

        // Exibir os materiais coletados
        if (pontoColeta.getMateriaisColetados() != null && !pontoColeta.getMateriaisColetados().isEmpty()) {
            // Separar os materiais por vírgula
            String materiaisFormatados = pontoColeta.getMateriaisColetados().replace("\n", ", ");
            holder.textViewMateriaisColetados.setText(materiaisFormatados);
        } else {
            holder.textViewMateriaisColetados.setText("Nenhum material registrado");
        }

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
            intent.putExtra("idPontoColeta", pontoColeta.getId_PC());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPontosColeta.size();
    }

    public static class PontoColetaViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNomePonto, textViewEnderecoPonto, textViewBairroPonto, textViewPessoaContato, textViewHorarioFuncionamento;
        TextView textViewCidadePonto, textViewUfPonto, textViewCidadeUfPonto, textViewTelefonePonto, textViewMateriaisColetados;
        ImageButton imageButtonFavoritar;
        Button buttonAlterarPonto;

        public PontoColetaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar os TextViews
            textViewNomePonto = itemView.findViewById(R.id.textViewNomePonto);
            textViewEnderecoPonto = itemView.findViewById(R.id.textViewEnderecoPonto);
            textViewBairroPonto = itemView.findViewById(R.id.textViewBairroPonto);
            textViewCidadePonto = itemView.findViewById(R.id.editTextCidadePontoColeta);
            textViewCidadeUfPonto = itemView.findViewById(R.id.textViewCidadeUfPonto);
            textViewPessoaContato = itemView.findViewById(R.id.textViewPessoaContato);
            textViewHorarioFuncionamento = itemView.findViewById(R.id.textViewHorarioFuncionamento);
            textViewTelefonePonto = itemView.findViewById(R.id.textViewTelefonePonto);
            textViewMateriaisColetados = itemView.findViewById(R.id.textViewMateriaisColetados);

            // Inicializar o botão de favoritar e alterar
            imageButtonFavoritar = itemView.findViewById(R.id.imageButtonFavoritar);
            buttonAlterarPonto = itemView.findViewById(R.id.buttonAlterarPonto);

            // Adicionar logs para verificar se todos os componentes foram corretamente inicializados
            if (textViewNomePonto == null) {
                Log.e("ViewHolder", "textViewNomePonto é null");
            }
            if (textViewEnderecoPonto == null) {
                Log.e("ViewHolder", "textViewEnderecoPonto é null");
            }
            if (textViewBairroPonto == null) {
                Log.e("ViewHolder", "textViewBairroPonto é null");
            }
            if (textViewCidadePonto == null) {
                Log.e("ViewHolder", "textViewCidadePonto é null");
            }
            if (textViewUfPonto == null) {
                Log.e("ViewHolder", "textViewUfPonto é null");
            }
            if (textViewPessoaContato == null) {
                Log.e("ViewHolder", "textViewUfPonto é null");
            }
            if (textViewHorarioFuncionamento == null) {
                Log.e("ViewHolder", "textViewUfPonto é null");
            }
            if (textViewTelefonePonto == null) {
                Log.e("ViewHolder", "textViewTelefonePonto é null");
            }
            if (textViewMateriaisColetados == null) {
                Log.e("ViewHolder", "textViewMateriaisColetados é null");
            }
            if (imageButtonFavoritar == null) {
                Log.e("ViewHolder", "imageButtonFavoritar é null");
            }
            if (buttonAlterarPonto == null) {
                Log.e("ViewHolder", "buttonAlterarPonto é null");
            }
        }
    }
}