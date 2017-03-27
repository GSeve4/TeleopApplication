package salinesingularity.singularitytimingapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static salinesingularity.singularitytimingapplication.R.id.btnDropped;

public class GameActivity extends AppCompatActivity {

    int scores_s, scores_f, pickups_s, pickups_f, gearsDropped;
    ArrayList<TeleopEvent> events;
    ArrayList<TeleopEvent> undone;

    public final long GAME_LENGTH = 150000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        events = new ArrayList<>();
        undone = new ArrayList<>();


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //leaving this commented leaves the default text values on the buttons until one is pressed.
        //updateCounters();

    }

    public void onPickupS (View v)
    {
        events.add(new TeleopEvent(TeleopEventType.PICKUP_S, System.currentTimeMillis()));
        updateCounters();
        clearUndoneList();
    }

    public void onPickupF (View v)
    {

        events.add(new TeleopEvent(TeleopEventType.PICKUP_F, System.currentTimeMillis()));
        updateCounters();
        clearUndoneList();
    }

    public void onScoredS (View v)
    {
        events.add(new TeleopEvent(TeleopEventType.GEAR_SCORE_S, System.currentTimeMillis()));
        updateCounters();
        clearUndoneList();
    }

    public void onScoredF (View v)
    {
        events.add(new TeleopEvent(TeleopEventType.GEAR_SCORE_F, System.currentTimeMillis()));
        updateCounters();
        clearUndoneList();
    }

    public void onDropped (View v)
    {
        events.add(new TeleopEvent(TeleopEventType.GEAR_DROP, System.currentTimeMillis()));
        updateCounters();
        clearUndoneList();
    }

    public void onUndo (View v)
    {
        if(events.size() > 0){
            undone.add(events.remove(events.size() - 1));
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Nothing to undo!", Toast.LENGTH_SHORT);
            toast.show();
        }
        updateCounters();
    }

    public void onRedo (View v)
    {
        if(undone.size() > 0) {
            events.add(undone.remove(undone.size() - 1));
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Nothing to redo!", Toast.LENGTH_SHORT);
            toast.show();
        }
        updateCounters();
    }

    public void updateCounters() {

        //Get the count of each type of event
        setCountsToZero();
        for(TeleopEvent e : events) {
            switch (e.getEventType()) {
                case PICKUP_S:
                    pickups_s++;
                    break;
                case PICKUP_F:
                    pickups_f++;
                    break;
                case GEAR_SCORE_S:
                    scores_s++;
                    break;
                case GEAR_SCORE_F:
                    scores_f++;
                    break;
                case GEAR_DROP:
                    gearsDropped++;
                    break;
            }
        }

        //Set the buttons' text fields to their respective counts

        Button btnPickupS = (Button) findViewById(R.id.btnPickupS);
        btnPickupS.setText(pickups_s + "");

        Button btnPickupF = (Button) findViewById(R.id.btnPickupF);
        btnPickupF.setText(pickups_f + "");

        Button btnScoreS = (Button) findViewById(R.id.btnScoreS);
        btnScoreS.setText(scores_s + "");

        Button btnScoreF = (Button) findViewById(R.id.btnScoreF);
        btnScoreF.setText(scores_f + "");

        TextView tvDropped = (TextView) findViewById(R.id.tvDropCount);
        tvDropped.setText(gearsDropped + "");

        //Log.d("Update!", "updated values");
        Log.d("Updated array: ", events.toString());
    }

    public void setCountsToZero() {
        scores_s = 0;
        scores_f = 0;
        pickups_s = 0;
        pickups_f = 0;
        gearsDropped = 0;
    }

    public void clearUndoneList() {

        undone.clear();
    }

    public void onEndGame (View v)
    {
        //Generate a string representing the match
        long endTime = System.currentTimeMillis();
        String result = "";
        for(TeleopEvent t : events) {
            //Log.d("event", t.toStringGameTime(endTime));
            result += t.toStringGameTime(endTime) + "; ";
        }

        Log.d("result", result);

        //Copy the string to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Singularity Match Data", result);
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(getApplicationContext(), "Match data copied to clipboard", Toast.LENGTH_SHORT);
        toast.show();



    }

    private class TeleopEvent
    {
        private long timestamp;
        private TeleopEventType eventType;

        public TeleopEvent(TeleopEventType eventType, long timestamp) {

            this.timestamp = timestamp;
            this.eventType = eventType;

        }

        public TeleopEventType getEventType() {
            return eventType;
        }

        public String toString() {

            return eventType + ", " + timestamp;

        }

        public String toStringGameTime(long endTime) {
            double gameTime = ((double)(GAME_LENGTH - (endTime - timestamp))) / 1000.0;
            return eventType + ", " + gameTime;
        }

    }

    private enum TeleopEventType {

        PICKUP_S,
        PICKUP_F,
        GEAR_SCORE_S,
        GEAR_SCORE_F,
        GEAR_DROP
    }
}
