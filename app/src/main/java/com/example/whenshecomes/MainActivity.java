package com.example.whenshecomes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import java.security.Key;

import static com.example.whenshecomes.MouseControls.base_sens;
import static com.example.whenshecomes.MouseControls.start_x;
import static com.example.whenshecomes.MouseControls.start_y;
import static com.example.whenshecomes.MouseControls.step_x;
import static com.example.whenshecomes.MouseControls.step_y;
import static com.example.whenshecomes.MouseControls.sens;

public class MainActivity extends AppCompatActivity {
    Connection conn;
    TextView statusTv, sensTv, volumeTv, inputTv;
    SeekBar volumeBar, sensBar;
    Switch zoomSw, holdSw;
    Button rightClick, zoomIn, zoomOut, recButton, kbdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getSupportActionBar().hide();
        setContentView(R.layout.main_layout);

        initElements();
        initValues();
        setEvents();
        conn = new Connection("192.168.1.2",1338, this);
        conn.estabilish();

    }

    void connecter(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                while(true){
                    if(!conn.isConnected){
                        conn.estabilish();
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    void checkConnection(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                while(true){
                    conn.check();
                    if(!conn.isConnected) {
                        conn.estabilish();
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            }
        };
        thread.start();
    }

    void initElements(){
        statusTv = findViewById(R.id.statusTv);
        sensTv = findViewById(R.id.sensTv);
        volumeTv = findViewById(R.id.volumeTv);
        volumeBar = findViewById(R.id.volumeBar);
        sensBar = findViewById(R.id.sensBar);
        zoomSw = findViewById(R.id.zoomSwitch);
        holdSw = findViewById(R.id.holdLSwitch);
        rightClick = findViewById(R.id.rightClick);
        zoomIn = findViewById(R.id.zoomInButton);
        zoomOut = findViewById(R.id.zoomOutButton);
        recButton = findViewById(R.id.reconnectButton);
        kbdButton = findViewById(R.id.kbdButton);
        inputTv = findViewById(R.id.inputTv);
    }

    void initValues(){
        sensBar.setProgress(MouseControls.sens);
        inputTv.setVisibility(View.GONE);
        //zoomIn.setVisibility(View.GONE);
        //zoomOut.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    void setEvents(){
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                conn.send("setVolume",""+progress);
                volumeTv.setText("Volume: "+100*progress/100+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sensBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensTv.setText("Sensitivity: "+(Math.round(100*progress/ base_sens))/100.0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MouseControls.sens = seekBar.getProgress();
            }
        });

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.estabilish();
            }
        });

        holdSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    conn.send("leftPress","");
                }else{
                    conn.send("leftRelease","");
                }
            }
        });

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.send("clickRight","");
            }
        });


        kbdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
            }
        });


        zoomSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    conn.send("enableZoom");
//                    zoomIn.setVisibility(View.VISIBLE);
//                    zoomOut.setVisibility(View.VISIBLE);
                }
                else{
                    conn.send("disableZoom");
//                    zoomIn.setVisibility(View.GONE);
//                    zoomOut.setVisibility(View.GONE);
                }
            }
        });

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zoomSw.isChecked()) {
                    conn.send("zoomIn");
                }

            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zoomSw.isChecked()){
                    conn.send("zoomOut");
                }

            }
        });

        View contentView = findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        View contentView = findViewById(android.R.id.content);
                        Rect r = new Rect();
                        contentView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = contentView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!KeyboardControls.isKeyboardShowing) {
                                KeyboardControls.isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        }
                        else {
                            // keyboard is closed
                            if (KeyboardControls.isKeyboardShowing) {
                                KeyboardControls.isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }
                    }
                });


        inputTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEvent(event);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
        {
            conn.send("handleKeyboard",String.valueOf(event.getKeyCode()));
            String t = inputTv.getText().toString();
            if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                showKeyboard();
            }
            if(event.getKeyCode() == KeyEvent.KEYCODE_DEL && t.length() > 0){
                inputTv.setText(t.substring(0, t.length() - 1));
            }else{
                inputTv.setText(t+(char)event.getUnicodeChar());
                //inputTv.setText(t+(char)event.getUnicodeChar());
            }


        }
        return super.dispatchKeyEvent(event);
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void onKeyboardVisibilityChanged(boolean open){
        if(open){
            inputTv.setVisibility(View.VISIBLE);
            rightClick.setVisibility(View.GONE);
            sensBar.setVisibility(View.GONE);
            sensTv.setVisibility(View.GONE);
            volumeBar.setVisibility(View.GONE);
            volumeTv.setVisibility(View.GONE);
        }else{
            inputTv.setVisibility(View.GONE);
            rightClick.setVisibility(View.VISIBLE);
            sensBar.setVisibility(View.VISIBLE);
            sensTv.setVisibility(View.VISIBLE);
            volumeBar.setVisibility(View.VISIBLE);
            volumeTv.setVisibility(View.VISIBLE);
            inputTv.setText("");
        }
    }


    @Override
    public void onDestroy() {
        if(KeyboardControls.isKeyboardShowing){
            showKeyboard();
        }
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);


        super.onDestroy();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!conn.isConnected) { return false; }
        int action = event.getActionMasked();
        int x = (int) event.getX();
        int y = (int) event.getY();
        String data;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                start_x = x;
                start_y = y;
                step_x = x;
                step_y = y;
                break;
            case MotionEvent.ACTION_MOVE:

                int xoffset = (int)((x-step_x)*(sens/base_sens));
                int yoffset = (int)((y-step_y)*(sens/base_sens));
                step_x = x;
                step_y = y;

                data = ""+xoffset + "|" + ""+yoffset;
                conn.send("move",data);

                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(x-start_x) <= 5 && Math.abs(y-start_y) <= 5 && !holdSw.isChecked()){
                    conn.send("clickLeft","");
                }
                break;
//            case MotionEvent.ACTION_CANCEL:
//                break;
        }
        return true;
    }
}
