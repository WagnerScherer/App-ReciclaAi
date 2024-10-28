package com.wagner.reciclaai.Util;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wagner.reciclaai.R;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmailLogin, editTextPasswordLogin;
    private Button loginButton;
    private TextView textViewForgotPassword, textViewRegister;
    private ImageView imageViewShowPassword;
    private FirebaseAuth mAuth;
    private Boolean isPasswordVisible = false;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmailLogin = findViewById(R.id.editTextEmailLogin);
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin);
        imageViewShowPassword = findViewById(R.id.imageViewShowPassword);
        loginButton = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewRegister = findViewById(R.id.textViewRegister);

        imageViewShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    // Ocultar a senha
                    editTextPasswordLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imageViewShowPassword.setImageResource(R.drawable.visibility_off); // Ícone para senha oculta
                } else {
                    // Exibir a senha
                    editTextPasswordLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imageViewShowPassword.setImageResource(R.drawable.visibility_on); // Ícone para senha visível
                }
                // Move o cursor para o final do texto no campo de senha
                editTextPasswordLogin.setSelection(editTextPasswordLogin.length());
                isPasswordVisible = !isPasswordVisible;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //método tá separado mais abaixo
                loginUser();
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //implementar aqui o processo de redefinição de senha
                //função pensada para Grau C - funcionalidade fora do escopo
                Toast.makeText(LoginActivity.this, "Função ainda não implementada.", Toast.LENGTH_SHORT).show();
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CadUserActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmailLogin.setError("Informe o seu e-mail");
            editTextEmailLogin.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailLogin.setError("Por favor, insira um e-mail válido!");
            editTextEmailLogin.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
           editTextPasswordLogin.setError("Necessário preencher a sua senha!");
           editTextPasswordLogin.requestFocus();
           return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                   startActivity(intent);
                   finish();
               }
          }else {
                Toast.makeText(LoginActivity.this, "Autenticação falhou, e-mail ou senha inválidos! Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}