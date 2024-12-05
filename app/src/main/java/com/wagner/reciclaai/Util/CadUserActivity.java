package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.PhoneNumberFormatter;
import com.wagner.reciclaai.model.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CadUserActivity extends AppCompatActivity {

    // Declaração das variáveis
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText campoNome, campoEmail, campoSenha, campoFoneUser,
            campoEndereco, campoNumero, campoComplemento, campoBairro, campoCidade, campoEstado;
    private Button botaoCadastrar;
    private String uid;
    private Switch switchUserAdmin;
    private Spinner spinnerAdmPontoColeta;

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

        switchUserAdmin = findViewById(R.id.switchUserAdmin);
        spinnerAdmPontoColeta = findViewById(R.id.spinnerAdmPontoColeta);

        //define o spinner inicialmente invisível
        spinnerAdmPontoColeta.setVisibility(View.GONE);

        // Obter dados da Intent
        uid = getIntent().getStringExtra("USER_ID");
        Boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        String idPontoColeta = getIntent().getStringExtra("idPontoColeta");

        // Configurar o switch e a visibilidade do spinner com base em isAdmin
        if (isAdmin != null && isAdmin) {
            switchUserAdmin.setChecked(true);
            spinnerAdmPontoColeta.setVisibility(View.VISIBLE);

            // Carregar o nome do ponto de coleta no spinner
            if (idPontoColeta != null) {
                db.collection("PONTOSCOLETA").document(idPontoColeta)
                        .get()
                        .addOnSuccessListener(document -> {
                            if (document.exists()) {
                                String nomePontoColeta = document.getString("nome");
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Collections.singletonList(nomePontoColeta));
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerAdmPontoColeta.setAdapter(adapter);
                            }
                        })
                        .addOnFailureListener(e -> Log.d("Firestore", "Erro ao carregar nome do ponto de coleta", e));
            }
        } else {
            switchUserAdmin.setChecked(false);
            spinnerAdmPontoColeta.setVisibility(View.GONE);
        }

        // Lidar com o clique do botão
        botaoCadastrar.setOnClickListener(v -> validarCampos(uid == null));

        // Obter UID do Intent
        uid = getIntent().getStringExtra("USER_ID");
        if (uid != null) {
            recuperarDadosUsuario(uid);
            botaoCadastrar.setText("Salvar"); // Mudar o texto do botão para "Salvar"
        } else {
            botaoCadastrar.setText("Cadastrar"); // Mudar o texto para "Cadastrar" se não houver usuário autenticado
        }

        // Exibe o spinner de pontos de coleta apenas se o usuário for administrador
        switchUserAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                spinnerAdmPontoColeta.setVisibility(View.VISIBLE);

                // Carregar todos os pontos de coleta com realizaColeta = true
                db.collection("PONTOSCOLETA")
                        .whereEqualTo("realizaColeta", true)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Criar lista para o Spinner, adicionando o item padrão
                                List<String> pontosDeColeta = new ArrayList<>();
                                pontosDeColeta.add("Selecione um ponto de coleta"); // Primeira posição

                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    String nomePonto = document.getString("nome");
                                    if (nomePonto != null) {
                                        pontosDeColeta.add(nomePonto);
                                    }
                                }

                                // Adicionar os nomes ao Spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pontosDeColeta);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerAdmPontoColeta.setAdapter(adapter);
                            } else {
                                Toast.makeText(this, "Nenhum ponto de coleta disponível.", Toast.LENGTH_SHORT).show();

                                // Mesmo sem pontos, exibir "Selecione um ponto de coleta"
                                List<String> pontosDeColeta = new ArrayList<>();
                                pontosDeColeta.add("Selecione um ponto de coleta");
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pontosDeColeta);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerAdmPontoColeta.setAdapter(adapter);
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Erro ao carregar pontos de coleta", e));
            } else {
                spinnerAdmPontoColeta.setVisibility(View.GONE);
            }
        });
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
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(endereco) || TextUtils.isEmpty(numero) ||
                TextUtils.isEmpty(bairro) || TextUtils.isEmpty(cidade) || TextUtils.isEmpty(estado)) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCadastro) {
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
                        String uid = mAuth.getCurrentUser().getUid();
                        db.collection("USUARIOS").document(uid).set(usuario)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show());
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
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show());
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
                                //vai preencher os campos do cadastro com os dados do usuário autenticado
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

                                //desabilita a edição nos campos de e-mail e senha
                                campoEmail.setEnabled(false);
                                campoSenha.setEnabled(false);
                                campoSenha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);



                                //Configura o switch de administrador
                                if (usuarioRecuperado.isAdmin()) {
                                    switchUserAdmin.setChecked(true);
                                }

                                //Vai buscar e exibir o nome do ponto de coleta no spinner
                                // Buscar e exibir o nome do ponto de coleta no Spinner
                                if (usuarioRecuperado.getIdPontoColeta() != null) {
                                    db.collection("PONTOSCOLETA")
                                            .document(usuarioRecuperado.getIdPontoColeta())
                                            .get()
                                            .addOnSuccessListener(pontoDocument -> {
                                                if (pontoDocument.exists()) {
                                                    String nomePontoColeta = pontoDocument.getString("nome");
                                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Collections.singletonList(nomePontoColeta));
                                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    spinnerAdmPontoColeta.setAdapter(adapter);
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.d("Firestore", "Erro ao carregar nome do ponto de coleta", e));
                                }

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
