package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.wagner.reciclaai.R;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private EditText edtEmailRecuperacao;
    private Button btnEnviarRecuperacao;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        // Inicializar componentes
        edtEmailRecuperacao = findViewById(R.id.et_email_recuperacao);
        btnEnviarRecuperacao = findViewById(R.id.btn_enviar_recuperacao);
        firebaseAuth = FirebaseAuth.getInstance();

        // Configurar o botão de envio
        btnEnviarRecuperacao.setOnClickListener(view -> {
            String email = edtEmailRecuperacao.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RecuperarSenhaActivity.this, "Por favor, insira seu e-mail!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enviar o e-mail de recuperação
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperarSenhaActivity.this, "E-mail de recuperação enviado com sucesso!", Toast.LENGTH_LONG).show();
                            finish(); // Fecha a Activity
                        } else {
                            Toast.makeText(RecuperarSenhaActivity.this, "Erro ao enviar o e-mail de recuperação.", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
