package sednos.beaquiz;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        Button nearBeacon = (Button) findViewById(R.id.button);
        Button getQuiz = (Button) findViewById(R.id.button2);

        nearBeacon.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnNearBeacon();
                    }
                });
            }
        });

        getQuiz.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickGetQuiz();
                    }
                });
            }
        });
    }

    public void doClickOnNearBeacon() {
        View layout = (View) findViewById(R.id.activity_game_screen);
        layout.setBackgroundColor(Color.parseColor("#b80000"));
    }

    public void doClickGetQuiz() {
        Intent Quiz = new Intent(this, GameQuiz.class);
        startActivity(Quiz);
    }
}
