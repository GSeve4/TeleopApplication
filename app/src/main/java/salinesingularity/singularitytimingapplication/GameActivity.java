package salinesingularity.singularitytimingapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    int scores_s, scores_f, pickups_s, pickups_f, gearsDropped;
    int low_s, low_f, high_s, high_f;

    int auton_low = 0;
    int auton_high = 0;

    int peg_id = 0;

    boolean autonGearAttempted = false;
    boolean autonGearSuccessful = false;
    long autonGearScoreTime;

    boolean climbing = false;
    boolean climb_finished = false;
    boolean end_match_pressed = false;
    long endTime = 0;
    ClimbState climb = ClimbState.NOT_ATTEMPTED;
    ClimbState prevClimb = ClimbState.NOT_ATTEMPTED;
    TeleopEvent climbEvent;
    long activityStartTime;

    ArrayList<TeleopEvent> events;
    ArrayList<TeleopEvent> undone;

    public final long GAME_LENGTH = 150000;
    public long startTime;
    TextView countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        events = new ArrayList<>();
        undone = new ArrayList<>();

        autonGearSuccessful = false;
        autonGearAttempted = false;

        activityStartTime = System.currentTimeMillis();

        climbEvent = new TeleopEvent(TeleopEventType.CLIMB_NOT_ATTEMPTED, System.currentTimeMillis());

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_game);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //leaving this commented leaves the default text values on the buttons until one is pressed.
        //updateCounters();

        //starting a countdown timer when the activity opens
        countDownTimer = (TextView) findViewById(R.id.txtCountDownTimer);
        startTime = System.currentTimeMillis();



        new CountDownTimer(152000, 1000) {

            public void onTick(long millisUntilFinished)
            {

                int secondsUntilFinished = ((int) millisUntilFinished / 1000) - 1;

                String gameMode = "Teleop";
                if(secondsUntilFinished < 135){
                    if(secondsUntilFinished < 30)
                    {
                    gameMode = "Endgame";}
                } else {
                    gameMode = "Autonomous";
                }

                countDownTimer.setText(gameMode + ": " + (secondsUntilFinished));
            }

            public void onFinish()
            {

            }
        }.start();
    }

    //Autonomous button methods

    public void onAutonGearButtonClick(View v) {

        if(autonGearAttempted){
            if(autonGearSuccessful) {
                autonGearSuccessful = false;
            } else {
                autonGearSuccessful = true;
                autonGearScoreTime = System.currentTimeMillis();
            }
        } else {
            autonGearAttempted = true;
        }

        updateCounters();
    }

    public void onAutonLowGoal(View v){
        auton_low++;
        updateCounters();
    }

    public void onAutonLowGoalDec(View v){
        auton_low--;
        updateCounters();
    }

    public void onAutonHighGoal(View v){
        auton_high++;
        updateCounters();
    }

    public void onAutonHighGoalDec(View v){
        auton_high--;
        updateCounters();
    }



    //gear button methods

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
        if(events.size() > 0 && (events.get(events.size() - 1).getEventType() != TeleopEventType.CLIMB_START && events.get(events.size() - 1).getEventType() != TeleopEventType.CLIMB_FINISH && events.get(events.size() - 1).getEventType() != TeleopEventType.CLIMB_FAIL)){
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

    //Fuel Button methods

    public void onLowGoalS(View v){
        low_s++;
        updateCounters();
    }

    public void onLowGoalF(View v){
        low_f++;
        updateCounters();
    }

    public void onHighGoalS(View v){
        high_s++;
        updateCounters();
    }

    public void onHighGoalF(View v){
        high_f++;
        updateCounters();
    }

    public void onLowGoalSDec(View v){
        if(low_s > 0) low_s--;
        updateCounters();
    }

    public void onLowGoalFDec(View v){
        if(low_f > 0) low_f--;
        updateCounters();
    }

    public void onHighGoalSDec(View v){
        if(high_s > 0) high_s--;
        updateCounters();
    }

    public void onHighGoalFDec(View v){
        if(high_f > 0) high_f--;
        updateCounters();
    }

    //climbing

    public void onStartClimb (View v)
    {

        /*
        if(climbing) { // if we are climbing when button clicked, remove all climbs from data

            for(int i = 0; i < events.size(); i++) {
                if(events.get(i).getEventType() == TeleopEventType.CLIMB_START)
                    events.remove(i);
            }
            climbing = false;

        } else if(!climb_finished) { //if we are have not started climbing when clicked, add a new start event
            events.add(new TeleopEvent(TeleopEventType.CLIMB_START, System.currentTimeMillis()));
            climbing = true;
        } else{
            for(int i = 0; i < events.size(); i++) { //if we have finished a climb when clicked, remove the event that ended the climb and continue climb
                if(events.get(i).getEventType() == TeleopEventType.CLIMB_FINISH || events.get(i).getEventType() == TeleopEventType.CLIMB_FAIL)
                    events.remove(i);
            }
            climb_finished = false;
            climbing = true;
        }
        */


        climb = ClimbState.STARTED;
        climbEvent = new TeleopEvent(TeleopEventType.CLIMB_START, System.currentTimeMillis());


        updateCounters();
    }

    public void onFinishClimb (View v)
    {

        climb = ClimbState.SUCCESS;
        climbEvent = new TeleopEvent(TeleopEventType.CLIMB_FINISH, System.currentTimeMillis());
        //events.add(new TeleopEvent(TeleopEventType.CLIMB_FINISH, System.currentTimeMillis()));


        /*
        if(climbing) {
            events.add(new TeleopEvent(TeleopEventType.CLIMB_FINISH, System.currentTimeMillis()));
            climbing = false;
            climb_finished = true;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Not currently climbing", Toast.LENGTH_SHORT);
            toast.show();
        }
        */
        updateCounters();
    }

    public void onFailedClimb (View v)
    {

        climb = ClimbState.FAILURE;
        climbEvent = new TeleopEvent(TeleopEventType.CLIMB_FAIL, System.currentTimeMillis());

        //events.add(new TeleopEvent(TeleopEventType.CLIMB_FAIL, System.currentTimeMillis()));

        /*
        if(climbing) {
            events.add(new TeleopEvent(TeleopEventType.CLIMB_FAIL, System.currentTimeMillis()));
            climbing = false;
            climb_finished = true;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Not currently climbing", Toast.LENGTH_SHORT);
            toast.show();
        }
        */
        updateCounters();
    }

    public void onClimbEventUndo(View v) {

        for(int i = 0; i < events.size(); i++) { //if we have finished a climb when clicked, remove the event that ended the climb and continue climb
            if(events.get(i).getEventType() == TeleopEventType.CLIMB_FINISH || events.get(i).getEventType() == TeleopEventType.CLIMB_FAIL)
                events.remove(i);
        }

        if(climb == ClimbState.STARTED) {
            climb = ClimbState.NOT_ATTEMPTED;
            climbEvent = new TeleopEvent(TeleopEventType.CLIMB_NOT_ATTEMPTED, System.currentTimeMillis());
        }
        else if(climb == ClimbState.SUCCESS){
            climb = ClimbState.STARTED;
            climbEvent = new TeleopEvent(TeleopEventType.CLIMB_START, System.currentTimeMillis());
        }
        else if(climb == ClimbState.FAILURE){
            climb = ClimbState.STARTED;
            climbEvent = new TeleopEvent(TeleopEventType.CLIMB_START, System.currentTimeMillis());
        }
        updateCounters();
    }

    //Data methods

    public void updateCounters() {

        //set end match button back to normal when any other button is pressed
        end_match_pressed = false;

        Button btnEndGame = (Button) findViewById(R.id.btnEndGame);
        btnEndGame.setText(getString(R.string.default_end_game));


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

        //Gears

        /*
        Button btnPickupS = (Button) findViewById(R.id.btnPickupS);
        btnPickupS.setText(pickups_s + "");

        Button btnPickupF = (Button) findViewById(R.id.btnPickupF);
        btnPickupF.setText(pickups_f + "");

        Button btnScoreS = (Button) findViewById(R.id.btnScoreS);
        btnScoreS.setText(scores_s + "");

        Button btnScoreF = (Button) findViewById(R.id.btnScoreF);
        btnScoreF.setText(scores_f + "");
*/
        TextView tvDropped = (TextView) findViewById(R.id.tvDropCount);
        tvDropped.setText(gearsDropped + "");

        TextView tvGearScoreCount = (TextView) findViewById(R.id.tvGearScoreCount);
        tvGearScoreCount.setText(scores_s + "");

        TextView tvPickupFailCount = (TextView) findViewById(R.id.tvPickupFailCount);
        tvPickupFailCount.setText(pickups_f + "");

        //Fuel

        TextView tvLowGoalCount = (TextView) findViewById(R.id.tvLowGoalCount);
        tvLowGoalCount.setText(low_s + "");

        TextView tvHighGoalCount = (TextView) findViewById(R.id.tvHighGoalCount);
        tvHighGoalCount.setText(high_s + "");

        TextView tvAutonLowGoal = (TextView) findViewById(R.id.tvAutonLowGoal);
        tvAutonLowGoal.setText(auton_low + "");

        TextView tvAutonHighGoal = (TextView) findViewById(R.id.tvAutonHighGoal);
        tvAutonHighGoal.setText(auton_high + "");

        /*
        Button btnLowGoalS = (Button) findViewById(R.id.btnLowGoalS);
        btnLowGoalS.setText(low_s + "");

        Button btnLowGoalF = (Button) findViewById(R.id.btnLowGoalF);
        btnLowGoalF.setText(low_f + "");

        Button btnHighGoalS = (Button) findViewById(R.id.btnHighGoalS);
        btnHighGoalS.setText(high_s + "");

        Button btnHighGoalF = (Button) findViewById(R.id.btnHighGoalF);
        btnHighGoalF.setText(high_f + "");

        */

        //Climbing (new version)
        if(climb != prevClimb) {
            prevClimb = climb;
            LinearLayout climbLayout = (LinearLayout) findViewById(R.id.climbContainer);
            climbLayout.removeAllViews();

            LayoutInflater layoutInflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch(climb){
                case NOT_ATTEMPTED:
                    climbLayout.addView(layoutInflater.inflate(R.layout.climb_buttons_initial, climbLayout, false) );
                    break;
                case STARTED:
                    climbLayout.addView(layoutInflater.inflate(R.layout.climb_buttons_after_press, climbLayout, false) );
                    break;
                case SUCCESS:
                    climbLayout.addView(layoutInflater.inflate(R.layout.climb_buttons_after_success, climbLayout, false) );
                    break;
                case FAILURE:
                    climbLayout.addView(layoutInflater.inflate(R.layout.climb_buttons_after_fail, climbLayout, false) );
                    break;
            }
            //climbLayout.addView(layoutInflater.inflate(R.layout.climb_buttons_after_fail, climbLayout, false) );
        }


        //Auton gear button

        Button btnAutonGear = (Button) findViewById(R.id.btnAutonGear);

        if(autonGearAttempted){
            if(autonGearSuccessful){
                btnAutonGear.setText(getString(R.string.undo) + " Score");
            } else {
                btnAutonGear.setText(getString(R.string.auton_gear_success));
            }
        } else {
            btnAutonGear.setText(getString(R.string.auton_gear_attempted));
        }


        //Climbing (change text of start/cancel button based on whether climbing or not
        //Button btnClimbStart = (Button) findViewById(R.id.btnClimbStart);
        //btnClimbStart.setText(climb_finished ? getString(R.string.climb_complete) : climbing ? getString(R.string.cancel_climb) : getString(R.string.start_climb));


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


        //Auton events (non-fuel)
        RadioGroup radioStartPos = (RadioGroup) findViewById(R.id.radioStartPos);
        int selectedId = radioStartPos.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        String radioTextSelected;
        try {
            radioTextSelected = (String) ((RadioButton) findViewById(selectedId)).getText();
        } catch(NullPointerException e) {
            radioTextSelected = "";
        }



        if(!end_match_pressed) { //if the end match button hasn't been pressed yet


            end_match_pressed = true;
            Button btnEndGame = (Button) findViewById(R.id.btnEndGame);
            btnEndGame.setText(getString(R.string.dialogEndGame));
            endTime = System.currentTimeMillis();

            switch(radioTextSelected) {

                case "Boiler Side":
                    peg_id = 0;
                    break;


                case "Center":

                    peg_id = 1;
                    break;


                case "Gear Slot Side":

                    peg_id = 2;
                    break;

                default:
                    end_match_pressed = false;

                    Toast.makeText(GameActivity.this,
                            "Please select a start position!", Toast.LENGTH_SHORT).show();


                    btnEndGame.setText(getString(R.string.default_end_game));
                    break;
            }


        } else { //if the end match button has been pressed once

            Button btnEndGame = (Button) findViewById(R.id.btnEndGame);
            btnEndGame.setText(getString(R.string.dialogEndGame));

            switch(radioTextSelected) {

                case "Boiler Side":
                    peg_id = 0;
                    break;


                case "Center":

                    peg_id = 1;
                    break;


                case "Gear Slot Side":

                    peg_id = 2;
                    break;

                default:
                    end_match_pressed = false;

                    Toast.makeText(GameActivity.this,
                            "Please select a start position!", Toast.LENGTH_SHORT).show();


                    btnEndGame.setText(getString(R.string.default_end_game));
                    break;
            }

            ArrayList<String> eventTypesList = new ArrayList<>();
            ArrayList<String> eventValuesList = new ArrayList<>();

            events.add(climbEvent);

            if(autonGearSuccessful) events.add(0, new TeleopEvent(TeleopEventType.AUTON_GEAR_SUCCESS, autonGearScoreTime));
            if(autonGearAttempted) events.add(0, new TeleopEvent(TeleopEventType.AUTON_GEAR_ATTEMPTED, activityStartTime + (long) (peg_id * 1000)));

            CheckBox baselineCheckBox = (CheckBox) findViewById(R.id.baselineCheckbox);
            if(baselineCheckBox.isChecked()) events.add(0, new TeleopEvent(TeleopEventType.BASELINE_CROSSED, activityStartTime));

            //Generate a string representing the match
            String result = "";
            for(TeleopEvent t : events) {
                //Log.d("event", t.toStringGameTime(endTime));
                result += t.toStringGameTime(endTime) + "; ";
                eventTypesList.add(t.getEventTypeAsString());
                eventValuesList.add(t.getValueAsString());
            }

            //Add all additional data to that string
            ArrayList<TeleopEvent> additionalData = new ArrayList<>();

            additionalData.add(new TeleopEvent(TeleopEventType.LOW_GOAL_S, (long) low_s));
            additionalData.add(new TeleopEvent(TeleopEventType.LOW_GOAL_F, low_f));
            additionalData.add(new TeleopEvent(TeleopEventType.HIGH_GOAL_S, high_s));
            additionalData.add(new TeleopEvent(TeleopEventType.HIGH_GOAL_F, high_f));

            additionalData.add(new TeleopEvent(TeleopEventType.AUTON_HIGH_GOAL_S, auton_high));
            additionalData.add(new TeleopEvent(TeleopEventType.AUTON_LOW_GOAL_S, auton_low));


            //Setting up data to send to the review activity


            for(TeleopEvent t : additionalData) {
                //Log.d("event", t.toStringGameTime(endTime));
                result += t.toString() + "; ";
                eventTypesList.add(t.getEventTypeAsString());
                eventValuesList.add(t.getValueAsString());
            }

            //Log.d("result", result);

            //Copy the string to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Singularity Match Data", result);
            clipboard.setPrimaryClip(clip);

            Toast toast = Toast.makeText(getApplicationContext(), "Match data copied to clipboard", Toast.LENGTH_SHORT);
            toast.show();

            //Start review activity, pass the string data, and finish the current event
            /*
            Intent intent = new Intent(GameActivity.this,ReviewActivity.class);
            intent.putExtra("eventTypes", eventTypesList.toArray());
            intent.putExtra("eventValues", eventValuesList.toArray());
            intent.putExtra("dataString", result);
            startActivity(intent);
            */
            finish();

        }


    }

    //===========================================  PRIVATE CLASSES ============================================//

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

        public String getEventTypeAsString() {
            return eventType.toString();
        }

        public String getValueAsString() {
            return timestamp + "";
        }

        public String toString() {

            return eventType + ", " + timestamp;

        }

        public String toStringGameTime(long endTime) {
            /*
            double gameTime = ((double)(GAME_LENGTH - (endTime - timestamp))) / 1000.0;
            return eventType + ", " + gameTime;
            */
            double gameTime = ((double) timestamp - activityStartTime) / 1000.0;
            return eventType + ", " + gameTime;

        }

    }
/*
    private class EndMatchDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_end_match)
                    .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
*/
    private enum TeleopEventType {

        //Gears
        PICKUP_S,
        PICKUP_F,
        GEAR_SCORE_S,
        GEAR_SCORE_F,
        GEAR_DROP,

        AUTON_GEAR_ATTEMPTED,
        AUTON_GEAR_SUCCESS,

        //Fuel
        LOW_GOAL_S,
        LOW_GOAL_F,
        HIGH_GOAL_S,
        HIGH_GOAL_F,

        AUTON_LOW_GOAL_S,
        AUTON_LOW_GOAL_F,
        AUTON_HIGH_GOAL_S,
        AUTON_HIGH_GOAL_F,

        //Climb
        CLIMB_START,
        CLIMB_FINISH,
        CLIMB_FAIL,
        CLIMB_NOT_ATTEMPTED,

        //Other Auton
        BASELINE_CROSSED,

    }

    private enum ClimbState {
        NOT_ATTEMPTED,
        STARTED,
        SUCCESS,
        FAILURE
    }


}
