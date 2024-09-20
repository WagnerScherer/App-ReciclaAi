package com.wagner.reciclaai.Util;

import com.google.firebase.auth.FirebaseAuth;

public class ConfigBD {

    private static FirebaseAuth auth;

    public static FirebaseAuth Firebaseautenticacao(){
        if(auth == null){
            auth =FirebaseAuth.getInstance();
        }
        return auth;
    }
}