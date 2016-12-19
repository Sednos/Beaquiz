package sednos.beaquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class partiel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partiel_news);
    }

    private void rechercheNews() {
        // Ici on récupère les données du premier champ, qu'on appellera param1
        String param1 = null;

        // Les données du deuxieme champs
        String param2 = null;

        // etc pour tous les paramètres prévus

        ArrayList searchParam = new ArrayList();
        searchParam.add(param1);
        searchParam.add(param2);

        // ici on envoie la requete de sélection dans la bdd via les webservices
        // on récupère
    }
}
