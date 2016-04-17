package com.raido.android.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Display display;
    private String _previousOp = "=";
    private Double _previousNum = .0;

    private static final String STATE_SCREEN = "screenValue";
    private static final String STATE_SCREEN_STATE = "screenState";
    private static final String STATE_MAIN_ACTIVITY_PREVIOUS_OP = "mainActivityPreviousOp";
    private static final String STATE_MAIN_ACTIVITY_PREVIOUS_NUM = "mainActivityPreviousNum";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = new Display("0", (TextView) findViewById(R.id.textViewScreen));
        loadSavedInstanceState(savedInstanceState);
    }

    private void loadSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            display.setValue(savedInstanceState.getString(STATE_SCREEN));
            display.setAddNextSymbolAsNew(savedInstanceState.getBoolean(STATE_SCREEN_STATE));
            _previousOp = savedInstanceState.getString(STATE_MAIN_ACTIVITY_PREVIOUS_OP);
            _previousNum = savedInstanceState.getDouble(STATE_MAIN_ACTIVITY_PREVIOUS_NUM);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(STATE_SCREEN, display.toString());
        savedInstanceState.putBoolean(STATE_SCREEN_STATE, display.getAddNextSymbolAsNew());
        savedInstanceState.putString(STATE_MAIN_ACTIVITY_PREVIOUS_OP, _previousOp);
        savedInstanceState.putDouble(STATE_MAIN_ACTIVITY_PREVIOUS_NUM, _previousNum);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void numberClicked(View v) {
	    String num = v.getTag().toString();
        display.addSymbol(num);
    }
    public void operatorClicked(View v) {
        String op = v.getTag().toString();
        if (_previousOp.equals("=")) {
            _previousNum = display.toDouble();
            display.setValue(_previousNum);
        } else {
            if (!display.getAddNextSymbolAsNew()) {
                Intent intent = new Intent();
                intent.setAction("com.raido.calculator.CALCULATE");
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.putExtra("nr1", _previousNum);
                intent.putExtra("nr2", display.toDouble());
                intent.putExtra("op", _previousOp);
                sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        _previousNum = Double.parseDouble(getResultData());
                        display.setValue(_previousNum);
                    }
                }, null, Activity.RESULT_OK, null, null);
            }
        }
        _previousOp = op;
        display.setAddNextSymbolAsNew(true);
    }

    public void delClicked(View v) {
        display.delLastSymbol();
    }

    public void exitButtonClicked(View v) {
        finish();
    }

    public void ceButtonClicked(View v) {
        _previousOp = "=";
        _previousNum = .0;
        display.clear();
    }

    public void signButtonClicked(View v) {
        display.changeSign();
    }


}
