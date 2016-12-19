package sednos.beaquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);

        Button buttonCreate = (Button) findViewById(R.id.buttonGameHomeCreate);
        Button buttonJoin = (Button) findViewById(R.id.buttonGameHomeJoin);

        buttonCreate.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnButtonCreate();
                    }
                });
            }
        });
        buttonJoin.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnButtonJoin();
                    }
                });
            }
        });

    }

    public void doClickOnButtonCreate() {
        Intent createGame = new Intent(this, GameCreate.class);
        startActivity(createGame);
    }public void doClickOnButtonJoin() {
        Intent createGame = new Intent(this, GameJoin.class);
        startActivity(createGame);
    }
}
