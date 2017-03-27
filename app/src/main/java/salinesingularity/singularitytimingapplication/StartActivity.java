package salinesingularity.singularitytimingapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class StartActivity extends AppCompatActivity {

    Button startButton;
    Button instructionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(StartActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

        instructionButton = (Button) findViewById(R.id.btnInst);
        instructionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(StartActivity.this,InstructionActivity.class);
                startActivity(intent);
            }
        });
    }

}
