package com.wagner.reciclaai.Util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;

import com.wagner.reciclaai.Util.ConfigBD;
import com.google.firebase.auth.FirebaseAuth;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        auth = ConfigBD.Firebaseautenticacao();

        //findViewById(R.id.logout).setOnClickListener(this::logout);
        Button buttonCadUser = findViewById(R.id.buttonCadUser);
        Button buttonPerguntasFrequentes = findViewById(R.id.buttonPerguntasFrequentes);

        //localizando os cards da tela principal
        CardView buscaPontoColeta_Card = findViewById(R.id.buscaPontoColeta_Card);
        CardView cadastraPontoColeta_Card = findViewById(R.id.cadastraPontoColeta_Card);
        CardView agendamentoColeta_Card = findViewById(R.id.agendamentoColeta_Card);
        CardView notificacoes_Card = findViewById(R.id.notificacoes_Card);
        CardView ranking_Card = findViewById(R.id.ranking_Card);
        CardView consultaHistorico_Card = findViewById(R.id.consultaHistorico_Card);

        buscaPontoColeta_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, ConsultaPontoActivity.class);
                startActivity(intent);
            }
        });

        cadastraPontoColeta_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, CadPontoColetaActivity.class);
                startActivity(intent);
            }
        });

        agendamentoColeta_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento, aguarde novas atualizações!", Toast.LENGTH_SHORT).show();

            }
        });

        notificacoes_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento, aguarde novas atualizações!", Toast.LENGTH_SHORT).show();
            }
        });

        ranking_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento, aguarde novas atualizações!", Toast.LENGTH_SHORT).show();
            }
        });

        consultaHistorico_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento, aguarde novas atualizações!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonPerguntasFrequentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento, aguarde novas atualizações!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    // Buscar dados do usuário no Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("USUARIOS").document(userId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String nomeUsuario = documentSnapshot.getString("nome");
                                    String emailUsuario = documentSnapshot.getString("email");

                                    // Passar os dados para a tela de cadastro via Intent
                                    Intent intent = new Intent(PrincipalActivity.this, CadUserActivity.class);
                                    intent.putExtra("USER_ID", userId);
                                    intent.putExtra("NOME", nomeUsuario);
                                    intent.putExtra("EMAIL", emailUsuario);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(PrincipalActivity.this, "Erro ao carregar dados do usuário.", Toast.LENGTH_SHORT).show();
                            });

                }

            }

        });

    }

}