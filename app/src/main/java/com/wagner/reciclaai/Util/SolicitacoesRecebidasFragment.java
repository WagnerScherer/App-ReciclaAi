package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.wagner.reciclaai.R;

public class SolicitacoesRecebidasFragment extends Fragment {
    public SolicitacoesRecebidasFragment() {
        // Construtor vazio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitacoes_recebidas, container, false);
    }
}
