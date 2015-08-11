package com.example.giangdam.mediamp3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView lvListSong;
    ArrayList<String> arrrayList;
    ImageButton imgbtnmusicbanner;
    int _position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgbtnmusicbanner = (ImageButton)findViewById(R.id.imgbtnmusicbanner);
        lvListSong = (ListView)findViewById(R.id.lvListSong);

        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());


        arrrayList = new ArrayList<String>();

        for(int i = 0; i < mySongs.size(); i++){
            arrrayList.add(mySongs.get(i).getName());
        }


        ListSongAdapter listSongAdapter = new ListSongAdapter(this,R.layout.list_song_row,arrrayList);
        lvListSong.setAdapter(listSongAdapter);

        lvListSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _position = position;
                startActivity(new Intent(MainActivity.this, Player.class).putExtra("pos", position).putExtra("songlist", mySongs).putExtra("flag",true));
            }
        });

        imgbtnmusicbanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_position != -1)
                    startActivity(new Intent(MainActivity.this,Player.class).putExtra("pos",_position).putExtra("songlist",mySongs).putExtra("flag",false));
            }
        });

    }


    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }


    public ArrayList<File> findSongs(File root){

        ArrayList<File> arrayList = new ArrayList<File>();

        File[] files = root.listFiles();

        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden() ){
                arrayList.addAll(findSongs(singleFile));
            }else{
                if(singleFile.getName().endsWith(".mp3")  || singleFile.getName().endsWith(".wav") ){
                    arrayList.add(singleFile);
                }
            }
        }

        return  arrayList;

    }


}
