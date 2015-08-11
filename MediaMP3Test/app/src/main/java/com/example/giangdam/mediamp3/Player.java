package com.example.giangdam.mediamp3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Player extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    ArrayList<File> mySongs;
    int position;
    Uri uri;

    Handler handler = new Handler();


    SeekBar seekBar;
    ImageButton imgbtnshuffle,imgbtnrepeat,imgbtnplay,imgbtnpre,imgbtnnext;
    TextView lblTitle,lblCurrentTime,lblTotalTime;


    long starttime_m;
    long starttime_s;
    long endtime_m;
    long enttiem_s;

    Boolean isShuffle = false;
    Boolean isRepeat = false;
    Boolean isNext = false;
    Boolean isPre = false;
    Boolean flag;


    static MusicService mService;
    boolean mBound = false;



    private  Runnable UD = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,100);

            starttime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mService.getMediaPlayer().getCurrentPosition());
            starttime_s = (long) TimeUnit.MILLISECONDS.toSeconds(mService.getMediaPlayer().getCurrentPosition()) - (long)TimeUnit.MINUTES.toSeconds(starttime_m);
            lblCurrentTime.setText(convertTime(starttime_m,starttime_s));




            endtime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mService.getMediaPlayer().getDuration());
            enttiem_s = (long) TimeUnit.MILLISECONDS.toSeconds(mService.getMediaPlayer().getDuration()) - (long)TimeUnit.MINUTES.toSeconds(endtime_m);

            lblTotalTime.setText(convertTime(endtime_m,enttiem_s));
            //lblTotalTime.setText(endtime_m + ":" + enttiem_s);
            seekBar.setProgress(mService.getMediaPlayer().getCurrentPosition());

            if(starttime_m == endtime_m && starttime_s == enttiem_s){
                doNext();
            }

            if(isNext){
                if(starttime_s > 1){
                    imgbtnnext.setImageResource(R.drawable.next);
                    isNext = false;
                }

            }

            if(isPre){
                if(starttime_s > 1){
                    imgbtnpre.setImageResource(R.drawable.pre);
                    isPre = false;
                }

            }


        }
    };


   public String convertTime(long m, long s){
       String _m = "00";
       String _s = "00";
       if(m < 10){
           _m = "0"+m;
       }else {
           _m = String.valueOf(m);
       }

       if(s < 10){
           _s = "0"+s;
       }else {
           _s = String.valueOf(s);
       }

       return _m+ ":" + _s;
   }



    static final String PROGRESS = "progress";

    protected void onSaveInstanceState(Bundle outState) {

        //outState.putInt(PROGRESS,mService.getMediaPlayer().getCurrentPosition());
        super.onSaveInstanceState(outState);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);



        imgbtnplay = (ImageButton)findViewById(R.id.imgbtnplay);
        imgbtnshuffle = (ImageButton)findViewById(R.id.imgbtnshuffle);
        imgbtnrepeat = (ImageButton)findViewById(R.id.imgbtnrepeat);
        imgbtnpre = (ImageButton)findViewById(R.id.imgbtnpre);
        imgbtnnext = (ImageButton)findViewById(R.id.imgbtnnext);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        lblTitle = (TextView)findViewById(R.id.lblTitle);
        lblCurrentTime = (TextView)findViewById(R.id.lblCurrentTime);
        lblTotalTime = (TextView)findViewById(R.id.lblTotalTime);


        imgbtnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound)doPlay();
            }
        });

        imgbtnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound)
                    doNext();

            }
        });

        imgbtnpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound)doPre();
            }
        });

        imgbtnshuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShuffle();
            }
        });

        imgbtnrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRepeat();
            }
        });





        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs = (ArrayList)bundle.getParcelableArrayList("songlist");
        position = bundle.getInt("pos");

        flag = bundle.getBoolean("flag");



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.getMediaPlayer().seekTo(seekBar.getProgress());
            }
        });

    }

    protected  void onStart(){
        super.onStart();
        //start service;
        Intent serviceIntent = new Intent(this,MusicService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

        //doCreateMusic();


    }


    String preferencesfile = "my_pre";

    public void savingPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(preferencesfile,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(isShuffle){
            editor.putBoolean("Shuffle",true);
        }
        else{
            editor.putBoolean("Shuffle",false);
        }

        if(isRepeat){
            editor.putBoolean("Repeat",true);
        }else{
            editor.putBoolean("Repeat",false);
        }

        editor.commit();

    }

    public void restoringPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(preferencesfile,MODE_PRIVATE);
        isShuffle = sharedPreferences.getBoolean("Shuffle",false);
        isRepeat = sharedPreferences.getBoolean("Repeat",false);

        if(isShuffle){
            imgbtnshuffle.setImageResource(R.drawable.shuffleon);
        }
        if(isRepeat){
            imgbtnrepeat.setImageResource(R.drawable.repeaton);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        savingPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoringPreferences();
    }

    public void doCreateMusic() {
        if(mBound){
            //set postion and mysong
            mService.setMySongs(mySongs);
            mService.setPosition(position);
            mService.creareUri();

            lblTitle.setText(mService.getMySongs().get(position).getName().toString());

            mService.startMedia();
            seekBar.setMax(mService.getMediaPlayer().getDuration());


            starttime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mService.getMediaPlayer().getCurrentPosition());
            starttime_s = (long) TimeUnit.MILLISECONDS.toSeconds(mService.getMediaPlayer().getCurrentPosition()) - (long)TimeUnit.MINUTES.toSeconds(starttime_m);
            lblCurrentTime.setText(convertTime(starttime_m,starttime_s));

            endtime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mService.getMediaPlayer().getDuration());
            enttiem_s = (long) TimeUnit.MILLISECONDS.toSeconds(mService.getMediaPlayer().getDuration()) - (long)TimeUnit.MINUTES.toSeconds(endtime_m);
            lblTotalTime.setText(convertTime(endtime_m,enttiem_s));
            handler.postDelayed(UD, 100);
        }
        else {
            Toast.makeText(getApplicationContext(),"Server not connect",Toast.LENGTH_LONG).show();
        }
    }

    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;}
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if(mService.getMediaPlayer() != null && flag){ //new song
                mService.getMediaPlayer().stop();
                mService.getMediaPlayer().release();
                doCreateMusic();
                return;
            }
            if(!flag ){
                if(mService.getMediaPlayer() != null){
                    position = mService.getPosition();
                    seekBar.setMax(mService.getMediaPlayer().getDuration());
                    handler.postDelayed(UD, 100);
                }
                return;
            }
            doCreateMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };


    public void doPlay(){
        if(mService.getMediaPlayer().isPlaying()){
            imgbtnplay.setImageResource(R.drawable.pause);
            mService.getMediaPlayer().pause();
        }else
        {
            imgbtnplay.setImageResource(R.drawable.play);
            mService.getMediaPlayer().start();
        }
    }

    public void doNext() throws NullPointerException {

        imgbtnnext.setImageResource(R.drawable.nexton);
        isNext = true;

        mService.getMediaPlayer().stop();
        mService.getMediaPlayer().release();

        if(isRepeat){
            //
        }else {
            if(isShuffle){
                //get casual various
                position = new Random().nextInt((mySongs.size() - 1)  + 1) ;
            }else {
                if (position == mySongs.size() - 1)
                {
                    position = 0;
                } else {
                    position = position + 1;
                }
            }
        }




        mService.setMySongs(mySongs);
        mService.setPosition(position);
        mService.creareUri();
        mService.startMedia();

        lblTitle.setText(mService.getMySongs().get(mService.getPosition()).getName());
        seekBar.setMax(mService.getMediaPlayer().getDuration());
        handler.postDelayed(UD, 100);

    }

    public void doPre(){

        imgbtnpre.setImageResource(R.drawable.preon);
        isPre = true;

        mService.getMediaPlayer().stop();
        mService.getMediaPlayer().release();

        if(isRepeat){
            //
        }else {
            if(isShuffle){
                //get casual various
                position = new Random().nextInt((mySongs.size() - 1)  + 1) ;
            }else {
                position = (position - 1< 0)? mySongs.size() - 1 : position-1;
            }
        }


        mService.setMySongs(mySongs);
        mService.setPosition(position);
        mService.creareUri();
        mService.startMedia();


        lblTitle.setText(mService.getMySongs().get(mService.getPosition()).getName());
        seekBar.setMax(mService.getMediaPlayer().getDuration());

        handler.postDelayed(UD,100);
    }

    public void doShuffle(){
        //
        if(isShuffle){
            isShuffle = false;
            imgbtnshuffle.setImageResource(R.drawable.shuffle);
        }else {
            isShuffle = true;

            imgbtnshuffle.setImageResource(R.drawable.shuffleon);
        }
    }


    public void doRepeat(){
        if(isRepeat){
            isRepeat = false;
            imgbtnrepeat.setImageResource(R.drawable.repeat);
        }else {
            isRepeat = true;
            imgbtnrepeat.setImageResource(R.drawable.repeaton);
        }
    }


}
