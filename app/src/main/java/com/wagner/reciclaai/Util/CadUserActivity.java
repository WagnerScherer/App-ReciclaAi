package com.wagner.reciclaai.Util;

import android.os.Bundle;
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
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.Usuario;

public class CadUserActivity extends AppCompatActivity {

    Usuario usuario;
    FirebaseAuth autenticacao;
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
        campoEstado = findViewById(R.id.editTextEstado);

        botaoCadastrar = findViewById(R.id.buttonCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener(){
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

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!senha.isEmpty()) {
                    if (!endereco.isEmpty()){
                        if (!numero.isEmpty()){
                            if (!bairro.isEmpty()){
                                if (!cidade.isEmpty()){
                                    if (!estado.isEmpty()){

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
                                        Toast.makeText(this, "Preencha a sigla da UF", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Preencha a cidade", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Preencha o bairro", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Preencha o numero", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Preencha o endereço", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha o e-mail", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Preencha o nome", Toast.LENGTH_SHORT).show();
        }
    }


    private void cadastrarUsuario() {
        autenticacao = ConfigBD.Firebaseautenticacao();

        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadUserActivity.this, "Sucesso ao cadastrar o usuário", Toast.LENGTH_SHORT).show();
                }else {
                    String excecao ="";

                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        excecao ="Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e) {
                        excecao="Esta conta já existe";
                    }catch (Exception e){
                        excecao= "Erro ao cadastrar usuário"+ e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadUserActivity.this,excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}