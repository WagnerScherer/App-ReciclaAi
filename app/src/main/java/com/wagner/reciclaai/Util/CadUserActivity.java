package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.Usuario;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class CadUserActivity extends AppCompatActivity {

    // Declaração das variáveis
    private Usuario usuario;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText campoNome, campoEmail, campoSenha, campoEndereco, campoNumero, campoComplemento, campoBairro, campoCidade, campoEstado;
    private Button botaoCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        // Inicialização das variáveis
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        campoNome = findViewById(R.id.editTextName);
        campoEmail = findViewById(R.id.editTextEmail);
        campoSenha = findViewById(R.id.editTextPassword);
        campoEndereco = findViewById(R.id.editTextEndereco);
        campoNumero = findViewById(R.id.editTextNro);
        campoComplemento = findViewById(R.id.editTextComplemento);
        campoBairro = findViewById(R.id.editTextBairro);
        campoCidade = findViewById(R.id.editTextCidade);
        campoEstado = findViewById(R.id.editTextUF);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);

        botaoCadastrar.setOnClickListener(v -> validarCampos());

        // Obter UID do Intent
        String uid = getIntent().getStringExtra("USER_ID");
        if (uid != null) {
            recuperarDadosUsuario(uid);
        }
    }

    private void validarCampos() {
        String nome = campoNome.getText().toString().trim();
        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString().trim();
        String endereco = campoEndereco.getText().toString().trim();
        String numero = campoNumero.getText().toString().trim();
        String complemento = campoComplemento.getText().toString().trim();
        String bairro = campoBairro.getText().toString().trim();
        String cidade = campoCidade.getText().toString().trim();
        String estado = campoEstado.getText().toString().trim();

        // Validação básica
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(endereco) || TextUtils.isEmpty(numero) || TextUtils.isEmpty(bairro) || TextUtils.isEmpty(cidade) || TextUtils.isEmpty(estado)) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Digite um email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar o objeto Usuario com os dados válidos

        usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setEndereco(endereco);
        usuario.setNumero(numero);
        usuario.setComplemento(complemento);
        usuario.setBairro(bairro);
        usuario.setCidade(cidade);
        usuario.setEstado(estado);

        cadastrarUsuario(usuario);
    }

    private void cadastrarUsuario(Usuario usuario) {
        mAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Usuário criado com sucesso
                        String uid = mAuth.getCurrentUser().getUid();
                        db.collection("USUARIOS").document(uid).set(usuario)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                    // Redirecionar ou realizar outras ações necessárias
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "Erro ao cadastrar usuário", e);
                                });
                    } else {
                        // Se a criação do usuário falhar
                        Toast.makeText(this, "Falha ao criar usuário: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void recuperarDadosUsuario(String uid) {
        db.collection("USUARIOS").document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Usuario usuarioRecuperado = document.toObject(Usuario.class);
                            if (usuarioRecuperado != null) {
                                // Preencher os campos do formulário com os dados do usuário
                                campoNome.setText(usuarioRecuperado.getNome());
                                campoEmail.setText(usuarioRecuperado.getEmail());
                                campoSenha.setText(usuarioRecuperado.getSenha());
                                campoEndereco.setText(usuarioRecuperado.getEndereco());
                                campoNumero.setText(usuarioRecuperado.getNumero());
                                campoComplemento.setText(usuarioRecuperado.getComplemento());
                                campoBairro.setText(usuarioRecuperado.getBairro());
                                campoCidade.setText(usuarioRecuperado.getCidade());
                                campoEstado.setText(usuarioRecuperado.getEstado());

                                //campos e-mail e senha ficam bloqueados caso o usuário esteja autenticado
                                //e acesse a área de usuário
                                campoEmail.setEnabled(false);
                                campoSenha.setEnabled(false);

                                // O campo senha vai mostrar asteriscos caso o usuário esteja autenticado no sistema
                                campoSenha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                campoSenha.setTransformationMethod(new android.text.method.PasswordTransformationMethod());

                            }
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
                });
    }
}