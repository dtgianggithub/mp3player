package com.example.giangdam.mediamp3;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Player extends AppCompatActivity {

    static  MediaPlayer mediaPlayer;
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





    private  Runnable UD = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,100);

            starttime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
            starttime_s = (long) TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition()) - (long)TimeUnit.MINUTES.toSeconds(starttime_m);
            lblCurrentTime.setText(convertTime(starttime_m,starttime_s));




            endtime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
            enttiem_s = (long) TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) - (long)TimeUnit.MINUTES.toSeconds(endtime_m);

            lblTotalTime.setText(convertTime(endtime_m,enttiem_s));
            //lblTotalTime.setText(endtime_m + ":" + enttiem_s);
            seekBar.setProgress(mediaPlayer.getCurrentPosition());

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
                doPlay();
            }
        });

        imgbtnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    doNext();

            }
        });

        imgbtnpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPre();
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
        if(!flag){
            return;
        }


        if(mediaPlayer!= null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        lblTitle.setText(mySongs.get(position).getName().toString());
        uri = Uri.parse(mySongs.get(position).toString());



        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());


        starttime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
        starttime_s = (long) TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition()) - (long)TimeUnit.MINUTES.toSeconds(starttime_m);
        lblCurrentTime.setText(convertTime(starttime_m,starttime_s));

        endtime_m = (long) TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
        enttiem_s = (long) TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) - (long)TimeUnit.MINUTES.toSeconds(endtime_m);
        lblTotalTime.setText(convertTime(endtime_m,enttiem_s));





        handler.postDelayed(UD, 100);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

    }




    public void doPlay(){
        if(mediaPlayer.isPlaying()){
            imgbtnplay.setImageResource(R.drawable.pause);
            mediaPlayer.pause();
        }else
        {
            imgbtnplay.setImageResource(R.drawable.play);
            mediaPlayer.start();
        }
    }

    public void doNext() throws NullPointerException {

        imgbtnnext.setImageResource(R.drawable.nexton);
        isNext = true;

        mediaPlayer.stop();
        mediaPlayer.release();

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




        uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        lblTitle.setText(mySongs.get(position).getName());
        seekBar.setMax(mediaPlayer.getDuration());
        handler.postDelayed(UD, 100);

    }

    public void doPre(){

        imgbtnpre.setImageResource(R.drawable.preon);
        isPre = true;

        mediaPlayer.stop();
        mediaPlayer.release();

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


        uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);
        lblTitle.setText(mySongs.get(position).getName());
        seekBar.setMax(mediaPlayer.getDuration());
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
