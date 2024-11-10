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
import com.wagner.reciclaai.model.Usuario;

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
                // Obtendo o ID do usuário autenticado
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Buscando o documento do usuário no Firestore
                FirebaseFirestore.getInstance().collection("USUARIOS").document(uid)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Recupera a instância do usuário com base no documento
                                Usuario usuario = documentSnapshot.toObject(Usuario.class);

                                // Verifica se o usuário é administrador
                                boolean isAdmin = usuario != null && usuario.isAdmin();

                                // Inicia a Activity Agendamentos e passa a informação de administrador
                                Intent intent = new Intent(PrincipalActivity.this, AgendamentosActivity.class);
                                intent.putExtra("isAdmin", isAdmin);
                                startActivity(intent);
                            } else {
                                Toast.makeText(PrincipalActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(PrincipalActivity.this, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        notificacoes_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, NotificacaoActivity.class);
                startActivity(intent);

                //Toast.makeText(PrincipalActivity.this, "Função indisponível no momento, aguarde novas atualizações!", Toast.LENGTH_SHORT).show();
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
                    String uid = user.getUid();

                    // Buscar dados do usuário no Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("USUARIOS").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String nomeUsuario = documentSnapshot.getString("nome");
                                    String emailUsuario = documentSnapshot.getString("email");
                                    Boolean isAdmin = documentSnapshot.getBoolean("isAdmin");
                                    String idPontoColeta = documentSnapshot.getString("idPontoColeta");

                                    // Passar os dados para a tela de cadastro via Intent
                                    Intent intent = new Intent(PrincipalActivity.this, CadUserActivity.class);
                                    intent.putExtra("USER_ID", uid);
                                    intent.putExtra("NOME", nomeUsuario);
                                    intent.putExtra("EMAIL", emailUsuario);
                                    intent.putExtra("isAdmin", isAdmin);
                                    intent.putExtra("idPontoColeta", idPontoColeta);
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