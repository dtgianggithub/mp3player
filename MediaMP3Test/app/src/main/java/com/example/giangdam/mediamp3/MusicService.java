package com.example.giangdam.mediamp3;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Giang.Dam on 8/3/2015.
 */
public class MusicService extends Service {

    private final IBinder mBinder = new LocalBinder();

    static MediaPlayer mediaPlayer ;
    ArrayList<File> mySongs;
    int position;
    Uri uri;

    public void creareUri(){
        uri = Uri.parse(mySongs.get(position).toString());
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public ArrayList<File> getMySongs() {
        return mySongs;
    }

    public int getPosition() {
        return position;
    }

    public Uri getUri() {
        return uri;
    }


    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setMySongs(ArrayList<File> mySongs) {
        this.mySongs = mySongs;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public class LocalBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startMedia(){
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
    }
}
