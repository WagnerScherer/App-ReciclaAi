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
import java.util.List;

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
        db.collection("USUARIOS")
                .orderBy("coletasFinalizadas", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    rankingList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String username = document.getString("nome");
                        int coletas = document.getLong("coletasFinalizadas").intValue();
                        rankingList.add(new Ranking(username, coletas));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("RankingActivity", "Erro ao carregar dados", e));
    }
}

