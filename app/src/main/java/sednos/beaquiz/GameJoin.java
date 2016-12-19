package sednos.beaquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class GameJoin extends AppCompatActivity {
    public static final String BQ_PREF = "beaquizPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_join);

        Button GameJoinMatch = (Button) findViewById(R.id.buttonGameJoinMatch);

        GameJoinMatch.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnGameJoinMatch();
                    }
                });
            }
        });
    }

    public void doClickOnGameJoinMatch() {
        EditText GameJoinCode = (EditText) findViewById(R.id.editTextGameJoinCode);
        final String textGameJoinCode = GameJoinCode.getText().toString();
        SharedPreferences prefs = getSharedPreferences(BQ_PREF, MODE_PRIVATE);
        final String TOKEN = prefs.getString("token", null);

        Thread joinGame = new Thread(new Runnable() {
            @Override
            public void run() {
                //Définir les paramètres :
                try {
                    String data = "invit_code=" + textGameJoinCode;
                    data += "&" + URLEncoder.encode("token", "UTF-8")
                            + "=" + TOKEN;
                    Log.v("data_send ", data);

                    BufferedReader reader = null;

                    // Defined URL  where to send data
                    URL url = new URL("http://pascalcamara.fr/API/game/select");

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
                    String gameCode = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line);
                    }
                    Log.v("reponse", String.valueOf(sb));

                    Intent goGameWait = new Intent(GameJoin.this, GameWait.class);
                    //Toast gameInvalid = Toast.makeText(getApplicationContext(), "Le code est invalide ou la partie n'existe pas.", Toast.LENGTH_LONG);
                    //gameInvalid.show();
                    goGameWait.putExtra("Activity", "Join");
                    goGameWait.putExtra("GameCode", textGameJoinCode);
                    startActivity(goGameWait);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        joinGame.start();

    }
}