package salinesingularity.singularitytimingapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class AutonomousActivity extends Activity {

    //long startTime = Long.parseLong(getIntent().getStringExtra("startTime"));
    long startTime;
    TextView txtTimer = (TextView) findViewById(R.id.txtTimer);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomous);
        startTime = System.currentTimeMillis();
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Autonomous: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Intent intent = new Intent(AutonomousActivity.this,GameActivity.class);
                startActivity(intent);
            }
        }.start();
    }

}
