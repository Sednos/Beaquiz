package sednos.beaquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

public class GameWait extends AppCompatActivity {

    private static final String BQ_PREF = "beaquizPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_wait);

        Intent old = getIntent();
        String codeSend = old.getStringExtra("GameCode");
        String activity = old.getStringExtra("Activity");

        TextView GameCode = (TextView) findViewById(R.id.textViewGameWaitCode);
        Button LaunchGame = (Button) findViewById(R.id.buttonGameWaitLaunch);
        if("Create".equals(activity)){
            LaunchGame.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doClickOnLaunch();
                        }
                    });
                }
            });
        }
        else if ("Join".equals(activity)){
            LaunchGame.setVisibility(View.INVISIBLE);
            TextView InformCreate = (TextView) findViewById(R.id.editTextGameWaitInform);
            InformCreate.setText("La partie est en cours de création");
        }


        GameCode.setText(codeSend);
    }
    private void doClickOnLaunch() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(BQ_PREF, 0);
        final String TOKEN = pref.getString("token", null);

        Intent old = getIntent();
        final String GameId = old.getStringExtra("GameId");

        Thread GameTeamCreate = new Thread(new Runnable() {
            @Override
            public void run() {
                //Définir les paramètres :
                try {
                    String data = URLEncoder.encode("game_id", "UTF-8")
                            + "=" + GameId;
                    data += "&" + URLEncoder.encode("token", "UTF-8")
                            + "=" + TOKEN;
                    Log.v("data_send ", data);
                    BufferedReader reader = null;

                    // Defined URL  where to send data
                    URL url = new URL("http://pascalcamara.fr/API/game/start");

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
                        sb.append(line + "\n");
                    }
                    Log.v("reponse", String.valueOf(sb));
                    Intent goGameTeam = new Intent(GameWait.this, GameTeam.class);
                    goGameTeam.putExtra("GameId", GameId);
                    startActivity(goGameTeam);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        GameTeamCreate.start();
    };
}
