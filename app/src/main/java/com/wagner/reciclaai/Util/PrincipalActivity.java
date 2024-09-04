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
        CardView consultaPontoColeta_Card = findViewById(R.id.consultaPontoColeta_Card);
        CardView cadastraPontoColeta_Card = findViewById(R.id.cadastraPontoColeta_Card);
        CardView agendamentoColeta_Card = findViewById(R.id.agendamentoColeta_Card);
        CardView notificacoes_Card = findViewById(R.id.notificacoes_Card);
        CardView ranking_Card = findViewById(R.id.ranking_Card);
        CardView consultaHistorico_Card = findViewById(R.id.consultaPontoColeta_Card);

        // Inicializar variáveis e configurar listeners para os cards
        //setupCards();

        buttonCadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //obter o usuário autenticado
                FirebaseUser user = auth.getCurrentUser();
                if(user != null) {
                    //pega o uid do usuário
                    String userId = user.getUid();

                    //inicia a CadUser passando o USER_ID pra lá
                    Intent intent = new Intent(PrincipalActivity.this, CadUserActivity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                }
            }

        });

        buttonPerguntasFrequentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento.", Toast.LENGTH_SHORT).show();

                //preparando para funcionalidade futura
                //Intent intent = new Intent(PrincipalActivity.this,  ....Activity.class);
                //startActivity(intent);
            }
        });

        consultaPontoColeta_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento.", Toast.LENGTH_SHORT).show();

                //preparando para funcionalidade futura
                //Intent intent = new Intent(PrincipalActivity.this,  ....Activity.class);
                //startActivity(intent);
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

                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento.", Toast.LENGTH_SHORT).show();

                //preparando para funcionalidade futura
                //Intent intent = new Intent(PrincipalActivity.this,  ....Activity.class);
                //startActivity(intent);
            }
        });

        notificacoes_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento.", Toast.LENGTH_SHORT).show();

                //preparando para funcionalidade futura
                //Intent intent = new Intent(PrincipalActivity.this,  ....Activity.class);
                //startActivity(intent);
            }
        });

        ranking_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento.", Toast.LENGTH_SHORT).show();

                //preparando para funcionalidade futura
                //Intent intent = new Intent(PrincipalActivity.this,  ....Activity.class);
                //startActivity(intent);
            }
        });

        consultaHistorico_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PrincipalActivity.this, "Função indisponível no momento.", Toast.LENGTH_SHORT).show();

                //preparando para funcionalidade futura
                //Intent intent = new Intent(PrincipalActivity.this,  ....Activity.class);
                //startActivity(intent);
            }
        });

    }

    //private void setupCards() {
        //CardView consultaPontoColetaCard = findViewById(R.id.consultaPontoColeta_Card);
        //CardView cadastraPontoColetaCard = findViewById(R.id.cadastraPontoColeta_card);
        //CardView agendamentoColetaCard = findViewById(R.id.agendamentoColeta_Card);
        //CardView notificacoesCard = findViewById(R.id.notificacoes_Card);
        //CardView rankingCard = findViewById(R.id.ranking_Card);
        //CardView consultaHistoricoCard = findViewById(R.id.consultaHistorico_Card);

        //setCardListener(consultaPontoColetaCard, ConsultaPontoColetaActivity.class);
        //setCardListener(cadastraPontoColetaCard, CadPontoColetaActivity.class);
        //setCardListener(agendamentoColetaCard, AgendamentoColetaActivity.class);
        //setCardListener(notificacoesCard, NotificacoesActivity.class);
        //setCardListener(rankingCard, RankingActivity.class);
        //setCardListener(consultaHistoricoCard, CondultaHistoricoActivity.class);
    }

    //private void setCardListener(CardView cardView, final Class<?> activityClass) {
    //    cardView.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            Intent intent = new Intent(PrincipalActivity.this, activityClass);
    //            startActivity(intent);
    //        }
    //    });
    //}

    //public void logout(View view) {
    //    try {
    //        auth.signOut();
    //        startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
    //        finish(); // This line will ensure that the user cannot press the back button to return to the HomeActivity
    //    } catch (Exception e) {
    //        Toast.makeText(this, "Erro ao fazer logout. Tente novamente.", Toast.LENGTH_SHORT).show();
    //    }
    //}

//}