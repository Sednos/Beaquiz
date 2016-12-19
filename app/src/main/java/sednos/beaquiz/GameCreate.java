package sednos.beaquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class GameCreate extends AppCompatActivity {

    private static final String BQ_PREF = "beaquizPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);
        EditText GameName = (EditText) findViewById(R.id.editTextGameCreateName);
        Button CreateGame = (Button) findViewById(R.id.buttonGameCreateSave);

        CreateGame.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnCreateGame();
                    }
                });
            }
        });
    }

    public void doClickOnCreateGame() {
        EditText editTextGameName = (EditText) findViewById(R.id.editTextGameCreateName);
        final String gameName = editTextGameName.getText().toString();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(BQ_PREF, 0);
        final String TOKEN = pref.getString("token", null);

        Thread saveGame = new Thread(new Runnable() {
            @Override
            public void run() {
                //Définir les paramètres :
                try {
                    String data = URLEncoder.encode("token", "UTF-8")
                            + "=" + TOKEN;
                    data += "&" + URLEncoder.encode("name", "UTF-8")
                            + "=" + URLEncoder.encode(gameName, "UTF-8");
                    Log.v("data_send ", data);
                    BufferedReader reader = null;

                    // Defined URL  where to send data
                    URL url = new URL("http://pascalcamara.fr/API/game/create");

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
                    String json = sb.toString();
                    JSONObject sbJson = new JSONObject(json);
                    String Code = sbJson.getString("invit_code");
                    String GameId = sbJson.getString("id");


                    Intent GameWait = new Intent(GameCreate.this, GameWait.class);
                    GameWait.putExtra("GameCode", Code);
                    GameWait.putExtra("GameId", GameId);
                    GameWait.putExtra("Activity", "Create");
                    startActivity(GameWait);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        saveGame.start();
    }
}