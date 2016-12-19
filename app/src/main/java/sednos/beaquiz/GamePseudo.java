package sednos.beaquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static android.R.attr.data;

public class GamePseudo extends AppCompatActivity {
public static final String BQ_PREF = "beaquizPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_pseudo);

        SharedPreferences prefs = getSharedPreferences(BQ_PREF, MODE_PRIVATE);
            String pseudo = prefs.getString("pseudo", null);//"No name defined" is the default value.
            String token = prefs.getString("token", null);//"No name defined" is the default value.

            if (pseudo != null && token != null){
                Intent goHome = new Intent(this, GameHome.class);
                startActivity(goHome);
            }


        Button registerPseudo = (Button) findViewById(R.id.buttonGamePseudoSave);
        EditText textPseudo = (EditText) findViewById(R.id.editTextGamePseudoSet);
        registerPseudo.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doClickOnButton();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }



    public void doClickOnButton() throws IOException {

        EditText recupPseudo = (EditText) findViewById(R.id.editTextGamePseudoSet);
        final String pseudo = recupPseudo.getText().toString();



        Thread pseudoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL pseudoExist = null;
                try {
                    pseudoExist = new URL("http://pascalcamara.fr/API/player/exist/" + pseudo);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection c = null;
                try {
                    c = (HttpURLConnection) pseudoExist.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    c.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStream response = null;
                try {
                    response = c.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final BufferedReader read = new BufferedReader(new InputStreamReader(response));
                final String[] responsePseudo = new String[1];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Si le pseudo existe déjà
                            if ("1".equals(responsePseudo[0] = read.readLine())) {
                                Toast pseudoUsed = Toast.makeText(getApplicationContext(), "Pseudo déjà utilisé !", Toast.LENGTH_SHORT);
                                pseudoUsed.show();
                            } else {
                                // Si le pseudo n'existe pas, on l'enregistre
                                Thread savePseudo = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Définir les paramètres :
                                        try {
                                            String data = URLEncoder.encode("pseudo", "UTF-8")
                                                    + "=" + URLEncoder.encode(pseudo, "UTF-8");

                                            String token = "";
                                            BufferedReader reader = null;

                                            // Defined URL  where to send data
                                            URL url = new URL("http://pascalcamara.fr/API/player/create");

                                            // Send POST data request

                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.setDoOutput(true);
                                            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                                            wr.write(data);
                                            wr.flush();

                                            // Get the server response

                                            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                            StringBuilder sb = new StringBuilder();
                                            String line = null;

                                            // Read Server Response
                                            while ((line = reader.readLine()) != null) {
                                                // Append server response in string
                                                sb.append(line);
                                            }

                                            token = sb.toString();
                                            Log.v("Token =", token);
                                            Log.v("Pseudo =", pseudo);

                                            final String finalToken = token;
                                            final String finalPseudo = pseudo;


                                            runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        SharedPreferences pref = getApplicationContext().getSharedPreferences(BQ_PREF, 0);

                                                        SharedPreferences.Editor editor = pref.edit();
                                                        editor.putString("token", finalToken); // Storing string
                                                        editor.putString("pseudo", pseudo); // Storing string
                                                        editor.commit();

                                                        String PSEUDO = pref.getString("pseudo", null);
                                                        String TOKEN = pref.getString("token", null);

                                                        Log.v("pref_token = ", TOKEN);
                                                        Log.v("pref_pseudo = ", PSEUDO);

                                                        Intent goHome = new Intent(GamePseudo.this, GameHome.class);
                                                        Toast pseudoSave = Toast.makeText(getApplicationContext(), "Vous êtes enregistré en tant que " + pseudo + " !", Toast.LENGTH_SHORT);
                                                        pseudoSave.show();
                                                        startActivity(goHome);
                                                    }

                                                });

                                        } catch (UnsupportedEncodingException e1) {
                                            e1.printStackTrace();
                                        } catch (MalformedURLException e1) {
                                            e1.printStackTrace();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                });
                                savePseudo.start();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        });
        pseudoThread.start();
    }
}