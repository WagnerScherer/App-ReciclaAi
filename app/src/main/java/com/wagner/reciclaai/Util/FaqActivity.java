package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.FaqAdapter;
import com.wagner.reciclaai.model.Faq;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFaq;
    private FaqAdapter faqAdapter;
    private List<Faq> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        recyclerViewFaq = findViewById(R.id.recyclerViewFaq);
        recyclerViewFaq.setLayoutManager(new LinearLayoutManager(this));
        faqList = new ArrayList<>();
        faqAdapter = new FaqAdapter(faqList);
        recyclerViewFaq.setAdapter(faqAdapter);

        loadFaqData();
    }

    private void loadFaqData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FAQ")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Faq faq = doc.toObject(Faq.class);
                            faqList.add(faq);
                        }
                        faqAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(FaqActivity.this, "Erro ao carregar FAQ", Toast.LENGTH_SHORT).show());
    }
}


