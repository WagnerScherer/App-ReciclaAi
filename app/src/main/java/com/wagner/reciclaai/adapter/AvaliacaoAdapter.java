package com.wagner.reciclaai.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.wagner.reciclaai.model.Avaliacao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.wagner.reciclaai.R;

public class AvaliacaoAdapter extends RecyclerView.Adapter<AvaliacaoAdapter.ViewHolder> {
    private List<Avaliacao> avaliacoes;

    // Construtor
    public AvaliacaoAdapter(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_avaliacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Avaliacao avaliacao = avaliacoes.get(position);

        // Verificar se o UID do usuário está presente
        if (avaliacao.getUidUsuario() != null) {
            holder.textViewNomeUsuario.setText(avaliacao.getUidUsuario());
        } else {
            holder.textViewNomeUsuario.setText("Usuário desconhecido");
        }

        // Verificar se o nome do usuário está presente
        if (avaliacao.getNomeUsuario() != null) {
            holder.textViewNomeUsuario.setText(avaliacao.getNomeUsuario());
        } else {
            holder.textViewNomeUsuario.setText("Usuário desconhecido");
        }

        // Verificar se o comentário está presente
        if (avaliacao.getComentario() != null) {
            holder.textViewComentario.setText(avaliacao.getComentario());
        } else {
            holder.textViewComentario.setText("Sem comentário");
        }

        // Exibir a data no formato dd/MM/yyyy
        if (avaliacao.getData() != null) {
            Timestamp timestamp = avaliacao.getData();  // Recuperar o Timestamp

            // Formatar a data como dd/MM/yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(timestamp.toDate());  // Converter para string no formato desejado

            holder.textViewDataAvaliacao.setText(formattedDate);  // Exibir a data formatada
        } else {
            holder.textViewDataAvaliacao.setText("Data não disponível");
        }

        // Exibir as estrelas da nota
        holder.displayStars(avaliacao.getNota());
    }

    @Override
    public int getItemCount() {
        return avaliacoes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Declarar os elementos da view, como TextView, ImageView, etc.
        TextView textViewNomeUsuario, textViewComentario, textViewDataAvaliacao;
        ImageView[] stars = new ImageView[5];
        LinearLayout layoutStars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar os elementos da view
            textViewNomeUsuario = itemView.findViewById(R.id.textViewNomeUsuario);
            textViewComentario = itemView.findViewById(R.id.textViewComentario);
            textViewDataAvaliacao = itemView.findViewById(R.id.textViewDataAvaliacao);
            layoutStars = itemView.findViewById(R.id.layoutStars);

            // Inicializar as estrelas
            stars[0] = itemView.findViewById(R.id.star1);
            stars[1] = itemView.findViewById(R.id.star2);
            stars[2] = itemView.findViewById(R.id.star3);
            stars[3] = itemView.findViewById(R.id.star4);
            stars[4] = itemView.findViewById(R.id.star5);

        }

        // Função para exibir a nota com estrelas
        public void displayStars(int rating) {
            for (int i = 0; i < 5; i++) {
                if (i < rating) {
                    stars[i].setImageResource(R.drawable.baseline_star_24_preenchida); // Estrela cheia
                } else {
                    stars[i].setImageResource(R.drawable.baseline_star_border_24_vazia); // Estrela vazia
                }
            }
        }
    }
}

