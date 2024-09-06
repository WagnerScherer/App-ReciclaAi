package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.PhoneNumberFormatter;
import com.wagner.reciclaai.model.Usuario;

public class CadUserActivity extends AppCompatActivity {

    // Declaração das variáveis
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText campoNome, campoEmail, campoSenha, campoFoneUser;
    private EditText campoEndereco, campoNumero, campoComplemento, campoBairro, campoCidade, campoEstado;
    private Button botaoCadastrar;
    private String uid;

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
        campoFoneUser = findViewById(R.id.editTextFoneUser);
        campoNumero = findViewById(R.id.editTextNro);
        campoComplemento = findViewById(R.id.editTextComplemento);
        campoBairro = findViewById(R.id.editTextBairro);
        campoCidade = findViewById(R.id.editTextCidade);
        campoEstado = findViewById(R.id.editTextUF);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);

        campoFoneUser.addTextChangedListener(new PhoneNumberFormatter(campoFoneUser));

        // Lidar com o clique do botão
        botaoCadastrar.setOnClickListener(v -> validarCampos(uid == null));

        // Obter UID do Intent
        uid = getIntent().getStringExtra("USER_ID");
        if (uid != null) {
            recuperarDadosUsuario(uid);
        }
    }

    private void validarCampos(boolean isCadastro) {
        String nome = campoNome.getText().toString().trim();
        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString().trim();
        String foneUser = campoFoneUser.getText().toString().trim();
        String endereco = campoEndereco.getText().toString().trim();
        String numero = campoNumero.getText().toString().trim();
        String complemento = campoComplemento.getText().toString().trim();
        String bairro = campoBairro.getText().toString().trim();
        String cidade = campoCidade.getText().toString().trim();
        String estado = campoEstado.getText().toString().trim();

        // Validação básica para todos os casos
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(endereco) || TextUtils.isEmpty(numero) || TextUtils.isEmpty(bairro) || TextUtils.isEmpty(cidade) || TextUtils.isEmpty(estado)) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCadastro) {
            // Validações adicionais para o cadastro
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show();
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

            // Criar um novo usuário

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setFoneUser(foneUser);
            usuario.setEndereco(endereco);
            usuario.setNumero(numero);
            usuario.setComplemento(complemento);
            usuario.setBairro(bairro);
            usuario.setCidade(cidade);
            usuario.setEstado(estado);

            cadastrarUsuario(usuario);

        } else {
            // Atualizar os dados do usuário, sem email e senha
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setFoneUser(foneUser);
            usuario.setEndereco(endereco);
            usuario.setNumero(numero);
            usuario.setComplemento(complemento);
            usuario.setBairro(bairro);
            usuario.setCidade(cidade);
            usuario.setEstado(estado);

            atualizarDadosUsuario(usuario);
        }
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
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Falha ao criar usuário: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void atualizarDadosUsuario(Usuario usuario) {
        db.collection("USUARIOS").document(uid)
                .update("nome", usuario.getNome(),
                        "endereco", usuario.getEndereco(),
                        "foneUser", usuario.getFoneUser(),
                        "numero", usuario.getNumero(),
                        "complemento", usuario.getComplemento(),
                        "bairro", usuario.getBairro(),
                        "cidade", usuario.getCidade(),
                        "estado", usuario.getEstado())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show();
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
                                campoNome.setText(usuarioRecuperado.getNome());
                                campoEmail.setText(usuarioRecuperado.getEmail());
                                campoSenha.setText(usuarioRecuperado.getSenha());
                                campoFoneUser.setText(usuarioRecuperado.getFoneUser());
                                campoEndereco.setText(usuarioRecuperado.getEndereco());
                                campoNumero.setText(usuarioRecuperado.getNumero());
                                campoComplemento.setText(usuarioRecuperado.getComplemento());
                                campoBairro.setText(usuarioRecuperado.getBairro());
                                campoCidade.setText(usuarioRecuperado.getCidade());
                                campoEstado.setText(usuarioRecuperado.getEstado());

                                // Campos de email e senha ficam bloqueados ao atualizar
                                campoEmail.setEnabled(false);
                                campoSenha.setEnabled(false);

                                campoSenha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            }
                        } else {
                            Log.d("Firestore", "Documento não encontrado");
                        }
                    } else {
                        Log.d("Firestore", "Erro ao recuperar dados", task.getException());
                    }
                });
    }
}
