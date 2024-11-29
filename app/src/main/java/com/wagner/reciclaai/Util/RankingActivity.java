package com.wagner.reciclaai.Util;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.wagner.reciclaai.R;

import com.wagner.reciclaai.adapter.RankingAdapter;
import com.wagner.reciclaai.model.Ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {
    private RecyclerView recyclerRanking;
    private RankingAdapter adapter;
    private List<Ranking> rankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        recyclerRanking = findViewById(R.id.recyclerRanking);
        recyclerRanking.setLayoutManager(new LinearLayoutManager(this));
        rankingList = new ArrayList<>();
        adapter = new RankingAdapter(rankingList);
        recyclerRanking.setAdapter(adapter);

        fetchRankingData();
    }

    private void fetchRankingData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AGENDAMENTOS")
                .whereEqualTo("status_agendamento", 3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("RankingActivity", "Nenhum agendamento encontrado.");
                        return;
                    }

                    // Mapa para contar a quantidade de agendamentos por usuário
                    Map<String, Integer> userCountMap = new HashMap<>();

                    // Contar agendamentos por idUsuario
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String idUsuario = document.getString("idUsuario");
                        if (idUsuario != null) {
                            userCountMap.put(idUsuario, userCountMap.getOrDefault(idUsuario, 0) + 1);
                        }
                    }

                    // Transformar o mapa em uma lista de Ranking
                    List<Ranking> rankingListTemp = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : userCountMap.entrySet()) {
                        rankingListTemp.add(new Ranking(entry.getKey(), entry.getValue()));
                    }

                    // Ordenar pelo número de agendamentos (decrescente)
                    Collections.sort(rankingListTemp, (a, b) -> b.getColetas() - a.getColetas());

                    // Pegar apenas os top 5
                    List<Ranking> top5List = rankingListTemp.subList(0, Math.min(5, rankingListTemp.size()));

                    // Buscar os nomes dos usuários na coleção USUARIOS
                    fetchUsernames(top5List);
                })
                .addOnFailureListener(e -> Log.e("RankingActivity", "Erro ao carregar agendamentos", e));
    }

    private void fetchUsernames(List<Ranking> top5List) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        rankingList.clear(); // Limpar lista antes de preencher

        for (Ranking ranking : top5List) {
            db.collection("USUARIOS")
                    .document(ranking.getUsername()) // Usar o ID como referência do documento
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nomeUsuario = documentSnapshot.getString("nome");
                            ranking.setUsername(nomeUsuario != null ? nomeUsuario : "Usuário Desconhecido");
                        } else {
                            ranking.setUsername("Usuário Desconhecido");
                        }

                        // Adicionar à lista apenas após atualizar o nome
                        rankingList.add(ranking);

                        // Atualizar o adapter apenas após completar todas as buscas
                        if (rankingList.size() == top5List.size()) {
                            // Garantir ordenação correta antes de exibir
                            Collections.sort(rankingList, (a, b) -> b.getColetas() - a.getColetas());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("RankingActivity", "Erro ao buscar nome de usuário", e);
                        ranking.setUsername("Erro ao buscar nome");
                        rankingList.add(ranking);
                        if (rankingList.size() == top5List.size()) {
                            Collections.sort(rankingList, (a, b) -> b.getColetas() - a.getColetas());
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }
}

