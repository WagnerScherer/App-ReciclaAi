/*
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

public class CadUserActivity2 extends AppCompatActivity {

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
                                campoEndereco.setText(usuarioRecuperado.getEndereco());
                                campoNumero.setText(usuarioRecuperado.getNumero());
                                campoComplemento.setText(usuarioRecuperado.getComplemento());
                                campoBairro.setText(usuarioRecuperado.getBairro());
                                campoCidade.setText(usuarioRecuperado.getCidade());
                                campoEstado.setText(usuarioRecuperado.getEstado());
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
*/
//backup da classe CadUserActivity.java
/*
package com.wagner.reciclaai.Util;

        import android.nfc.Tag;
        import android.os.Bundle;
        import android.util.Log;
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

        import org.w3c.dom.Document;

        import java.util.HashMap;
        import java.util.Map;

public class CadUserActivity extends AppCompatActivity {

    Usuario usuario;
    FirebaseAuth autenticacao;
    FirebaseFirestore db;
    EditText campoNome, campoEmail, campoSenha, campoEndereco, campoNumero, campoComplemento, campoBairro, campoCidade, campoEstado;
    Button botaoCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        inicializar();
    }

    private void inicializar() {
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



        autenticacao = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }
        });
    }

    private void validarCampos() {
        String nome = campoNome.getText().toString();
        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();
        String endereco = campoEndereco.getText().toString();
        String numero = campoNumero.getText().toString();
        String complemento = campoComplemento.getText().toString();
        String bairro = campoBairro.getText().toString();
        String cidade = campoCidade.getText().toString();
        String estado = campoEstado.getText().toString();

        if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty() && !endereco.isEmpty() && !numero.isEmpty() &&
                !bairro.isEmpty() && !cidade.isEmpty() && !estado.isEmpty()) {

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

            cadastrarUsuario();

        } else {
            Toast.makeText(this, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void cadastrarUsuario() {
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Usuário criado com sucesso
                    FirebaseUser user = autenticacao.getCurrentUser();
                    String uid = user.getUid();

                    // Criando documento para o usuário no Firestore
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("UID", uid);
                    userMap.put("email", usuario.getEmail());
                    userMap.put("nome", usuario.getNome());
                    userMap.put("endereco", usuario.getEndereco());  // Include these fields
                    userMap.put("numero", usuario.getNumero());
                    userMap.put("complemento", usuario.getComplemento());
                    userMap.put("bairro", usuario.getBairro());
                    userMap.put("cidade", usuario.getCidade());
                    userMap.put("estado", usuario.getEstado());

                    db.collection("USUARIOS").document(uid)
                            .set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(CadUserActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CadUserActivity.this, "Erro ao cadastrar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    String excecao;

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Digite um e-mail válido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta já existe";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadUserActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void recuperarDadosUsuario() {
        FirebaseUser user = autenticacao.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            db.collection("USUARIOS").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String nome = document.getString("nome");
                                String email = document.getString("email");
                                String endereco = document.getString("endereco");
                                String numero = document.getString("numero");
                                String complemento = document.getString("complemento");
                                String bairro = document.getString("bairro");
                                String cidade = document.getString("cidade");
                                String estado = document.getString("estado");

                                // Atualizando os campos de texto com os dados recuperados
                                campoNome.setText(nome);
                                campoEmail.setText(email);
                                campoEndereco.setText(endereco);
                                campoNumero.setText(numero);
                                campoComplemento.setText(complemento);
                                campoBairro.setText(bairro);
                                campoCidade.setText(cidade);
                                campoEstado.setText(estado);

                            } else {
                                Toast.makeText(CadUserActivity.this, "Nenhum dado encontrado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CadUserActivity.this, "Conexão falhou", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(CadUserActivity.this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
        }
    }

//}
fim do backup da classe CadUserActivity.java
 */