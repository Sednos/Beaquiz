package sednos.beaquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
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

public class GameTeam extends AppCompatActivity {
    private static final String BQ_PREF = "beaquizPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_team);

        Intent old = getIntent();
        final String GameId = old.getStringExtra("GameId");

        SharedPreferences pref = getApplicationContext().getSharedPreferences(BQ_PREF, 0);
        final String TOKEN = pref.getString("token", null);

        Thread showTeam = new Thread(new Runnable() {
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
                    URL url = new URL("http://pascalcamara.fr/API/game/result");

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
                    String json = sb.toString();
                    JSONArray sbJsonArray = new JSONArray(json);

                    final ListView RedTeam = (ListView) findViewById(R.id.listViewGameTeamRedPlayers);
                    final ListView BlueTeam = (ListView) findViewById(R.id.listViewGameTeamBluePlayers);

                    final ArrayAdapter<String> adapterRed = new ArrayAdapter<String>(GameTeam.this,
                            android.R.layout.simple_list_item_1);
                    final ArrayAdapter<String> adapterBlue = new ArrayAdapter<String>(GameTeam.this,
                            android.R.layout.simple_list_item_1);

                    for(int i=0; i < sbJsonArray.length(); i++) {
                        final JSONObject jsonobject = sbJsonArray.getJSONObject(i);
                        Intent old = getIntent();
                        final String GameIdIntent = old.getStringExtra("GameId");
                        String gameId = jsonobject.getString("bq_games_id");
                        if(GameIdIntent.equals(gameId)) {
                            String teamOrder = jsonobject.getString("team_order");
                            if("0".equals(teamOrder)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            adapterRed.add(jsonobject.getString("pseudo"));
                                            adapterRed.notifyDataSetChanged();
                                            RedTeam.setAdapter(adapterRed);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            if("1".equals(teamOrder)) {
                                final String[] pseudo = new String[]{jsonobject.getString("pseudo")};
                                Log.v("pseudo team 1", String.valueOf(pseudo));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            adapterBlue.add(jsonobject.getString("pseudo"));
                                            adapterBlue.notifyDataSetChanged();
                                            BlueTeam.setAdapter(adapterBlue);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        showTeam.start();

        Button EnterGame = (Button) findViewById(R.id.buttonGameTeamGoGameScreen);

        EnterGame.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnEnterGame();
                    }
                });
            }
        });
    }

    public void doClickOnEnterGame() {
        Intent goGameScreen = new Intent(this, GameScreen.class);
        startActivity(goGameScreen);
    }
}

