package com.wagner.reciclaai.Util;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.AvaliacaoAdapter;
import com.wagner.reciclaai.model.Avaliacao;
import com.wagner.reciclaai.model.PontoColeta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvaliacaoActivity extends AppCompatActivity {
    private ImageView[] stars = new ImageView[5];
    private EditText editTextComentario;
    private Button buttonAvaliar;
    private PontoColeta pontoColeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao);

        editTextComentario = findViewById(R.id.editTextComentario);
        buttonAvaliar = findViewById(R.id.buttonAvaliar);

        // Configuração inicial das estrelas
        setupStars();

        // Configuração do botão Avaliar
        buttonAvaliar.setOnClickListener(view -> {
            submitAvaliacao();
        });

        String idPontoColeta = getIntent().getStringExtra("id_ponto_coleta");

        // Receber o nome do ponto de coleta passado pelo Intent
        String nomePontoColeta = getIntent().getStringExtra("nome_ponto_coleta");

        // Log para verificar se o nome foi recebido
        Log.d("AvaliacaoActivity", "Nome do ponto de coleta recebido: " + nomePontoColeta);

        // Definir o nome do ponto de coleta no título da tela
        TextView tituloPontoColeta = findViewById(R.id.textViewTituloPontoColeta);
        if (nomePontoColeta != null && !nomePontoColeta.isEmpty()) {
            tituloPontoColeta.setText(nomePontoColeta); // Exibir o nome do ponto de coleta
        } else {
            tituloPontoColeta.setText("Nome do Ponto de Coleta Indisponível"); // Fallback se o nome estiver nulo
        }

        // Carregar avaliações existentes
        carregarAvaliacoes(); // Carregar avaliações existentes ao abrir a tela
    }

    private int currentRating = 0;  // Variável para armazenar o rating atual
    private void setupStars() {
        for (int i = 0; i < stars.length; i++) {
            int starId = getResources().getIdentifier("star" + (i + 1), "id", getPackageName());
            stars[i] = findViewById(starId);
            int finalI = i;
            stars[i].setOnClickListener(view -> {
                setRating(finalI + 1);  // Define a quantidade de estrelas selecionadas
            });
        }
    }

    private void setRating(int rating) {
        currentRating = rating;  // Atualiza a nota atual
        Log.d("AvaliacaoActivity", "Nota capturada no clique: " + currentRating);

        // Atualiza a exibição das estrelas
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.baseline_star_24_preenchida);
            } else {
                stars[i].setImageResource(R.drawable.baseline_star_border_24_vazia);
            }
        }
    }

    private void submitAvaliacao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Obtenha o valor do comentário
        String comentario = editTextComentario.getText().toString();

        // Obtenha a nota correta através do método getRating()
        int nota = getRating();  // Nota de 1 a 5 (ou 0 se nenhuma estrela for selecionada)

        Log.d("AvaliacaoActivity", "Nota: " + nota);

        // Obtenha o UID do usuário autenticado
        String usuarioId = auth.getCurrentUser().getUid();

        // Obtenha o ID do ponto de coleta
        String pontoColetaId = getIntent().getStringExtra("id_ponto_coleta");

        if (nota == 0 && comentario.isEmpty()) {
            Toast.makeText(this, "Insira um comentário ou selecione uma nota.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log para verificar se o ID do ponto de coleta está correto
        Log.d("AvaliacaoActivity", "ID do ponto de coleta: " + pontoColetaId);

        // Recupera o nome do usuário a partir da coleção de USUARIOS
        db.collection("USUARIOS").document(usuarioId).get().addOnSuccessListener(documentSnapshot -> {
            String nomeUsuario = documentSnapshot.getString("nome");

            // Criar o mapa de dados para a avaliação
            Map<String, Object> avaliacao = new HashMap<>();
            avaliacao.put("id_usuario", usuarioId);   // ID do usuário
            avaliacao.put("nomeUsuario", nomeUsuario); // Nome do usuário
            avaliacao.put("id_ponto_coleta", pontoColetaId);  // ID do ponto de coleta
            avaliacao.put("comentario", comentario);  // Comentário
            avaliacao.put("nota", nota);  // Nota de 0 a 5
            avaliacao.put("data", FieldValue.serverTimestamp());  // Data da avaliação (Timestamp no Firestore)

            // Salvar no Firestore
            db.collection("AVALIACOES")
                    .add(avaliacao)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                        // Limpar os campos e resetar as estrelas
                        editTextComentario.setText("");
                        resetStars();
                        // Atualizar o RecyclerView com a nova avaliação
                        carregarAvaliacoes();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Erro ao enviar a avaliação. Tente novamente.", Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Erro ao recuperar o nome do usuário.", Toast.LENGTH_SHORT).show();
        });
    }

    private void carregarAvaliacoes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obter o ID do ponto de coleta
        String pontoColetaId = getIntent().getStringExtra("id_ponto_coleta");

        // Verificar o ID do ponto de coleta
        Log.d("AvaliacaoActivity", "Carregando avaliações para o ponto de coleta ID: " + pontoColetaId);

        // Consultar as avaliações relacionadas ao ponto de coleta, ordenadas pela data (mais recente primeiro)
        db.collection("AVALIACOES")
                .whereEqualTo("id_ponto_coleta", pontoColetaId)
                //.orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING) // Ordenar pela data de forma decrescente
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Avaliacao> listaAvaliacoes = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Avaliacao avaliacao = documentSnapshot.toObject(Avaliacao.class);

                            // Recuperar o timestamp da data
                            Timestamp timestamp = documentSnapshot.getTimestamp("data");
                            if (timestamp != null) {
                                // Definir o timestamp diretamente no objeto Avaliacao
                                avaliacao.setData(timestamp);
                            }

                            listaAvaliacoes.add(avaliacao);
                        }

                        // Atualizar o RecyclerView com as avaliações
                        atualizarRecyclerView(listaAvaliacoes);
                    } else {
                        Toast.makeText(this, "Nenhuma avaliação encontrada.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar avaliações.", Toast.LENGTH_SHORT).show();
                    Log.e("AvaliacaoActivity", "Erro ao carregar avaliações", e);
                });
    }

    private void atualizarRecyclerView(List<Avaliacao> listaAvaliacoes) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewAvaliacoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Definir o adapter com a lista de avaliações
        AvaliacaoAdapter adapter = new AvaliacaoAdapter(listaAvaliacoes);
        recyclerView.setAdapter(adapter);

        // Aplicar o divisor após configurar o RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.linha_divisoria_itens_recyclerview));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private int getRating() {
        return currentRating;  // Retorna o valor atualizado da nota
    }

    private void resetStars() {
        for (int i = 0; i < stars.length; i++) {
            stars[i].setImageResource(R.drawable.baseline_star_border_24_vazia);
        }
    }
}