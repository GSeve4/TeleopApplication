package salinesingularity.singularitytimingapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InstructionActivity extends AppCompatActivity {

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        backButton = (Button) findViewById(R.id.btnReturn);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                finish();

                /* This starts a new start activity... we just want to go back ti the previous one
                Intent intent = new Intent(InstructionActivity.this,StartActivity.class);
                startActivity(intent);
                */
            }
        });
    }
}
