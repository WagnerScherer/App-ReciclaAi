package com.wagner.reciclaai.model;

import android.text.TextWatcher;
import android.widget.EditText;
import android.text.Editable;

public class PhoneNumberFormatter implements TextWatcher {
    private boolean isUpdating;
    private final EditText editText;
    private String oldText = "";  // Variável para armazenar o texto anterior

    public PhoneNumberFormatter(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Armazena o texto antigo antes de qualquer mudança
        oldText = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Não precisa fazer nada aqui
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Evita loop recursivo
        if (isUpdating) {
            return;
        }

        isUpdating = true;

        // Remove qualquer formatação anterior, mantendo apenas os números
        String str = s.toString().replaceAll("[^\\d]", "");

        // Evita falhas ao tentar acessar índices inexistentes
        try {
            // Aplica a formatação de telefone (XX) XXXXX-XXXX
            if (str.length() > 10) {
                str = String.format("(%s) %s-%s", str.substring(0, 2), str.substring(2, 7), str.substring(7, 11));
            } else if (str.length() > 6) {
                str = String.format("(%s) %s-%s", str.substring(0, 2), str.substring(2, 6), str.substring(6, 10));
            } else if (str.length() > 2) {
                str = String.format("(%s) %s", str.substring(0, 2), str.substring(2, 6));
            }

            // Atualiza o campo de texto apenas se o novo texto for diferente do antigo
            if (!str.equals(oldText)) {
                editText.setText(str);
                editText.setSelection(str.length());  // Mantém o cursor no final do texto
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        isUpdating = false;
    }

    public static String format(String fone) {
        if (fone != null && fone.length() == 11) {
            // Formatar telefone de 11 dígitos (inclui DDD e 9 dígitos)
            return String.format("(%s) %s-%s",
                    fone.substring(0, 2), // DDD
                    fone.substring(2, 7), // Primeira parte
                    fone.substring(7));   // Segunda parte
        } else {
            return fone; // Retornar o telefone como está se não for de 11 dígitos
        }
    }

}
