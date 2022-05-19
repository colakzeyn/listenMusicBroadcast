package com.zey.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayer_Activity extends AppCompatActivity {

    MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    TextView baslikk;
    TextView currentTime;
    TextView duration;
    ImageView resumeButton;
    ImageView  nextButton ;
    ImageView   prevButton ;
    ImageView  music_icon;
    SeekBar seek_bar;

    ArrayList<Sarki> MusicList;
    Sarki currentMusic;

    MediaPlayer mediaPlayer= MyPlayer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        getIds();

        MuzigiKoy();

        MusicPlayer_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seek_bar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(milisaniye2dakika(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        resumeButton.setImageResource(R.drawable.pause64);
//                        music_icon.setRotation(x++);
                    }else{
                        resumeButton.setImageResource(R.drawable.play64);
//                        music_icon.setRotation(0);
                    }

                }
                new Handler().postDelayed(this,100);        //burdabi importseçimi yaptım
            }
        });

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        IntentFilter filter = new IntentFilter("com.zey.EXAMPLE_ACTION");
        registerReceiver(myBroadcastReceiver,filter);
        Log.i("lannn", "main");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    public void signupClick(View v){
        Intent i = new Intent(this, signupActivity.class);
        startActivity(i);
    }


    void MuzigiKoy(){

        currentMusic = MusicList.get(MyPlayer.currentIndex);

        baslikk.setText(currentMusic.getBaslik());
        //ilgili album resmini koyma
        Uri uri;
        uri = Uri.parse(currentMusic.getUripath());
        music_icon.setImageURI(uri);

        duration.setText(milisaniye2dakika(currentMusic.getSure()));

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                else
                    mediaPlayer.start();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyPlayer.currentIndex== MusicList.size()-1)  /// SON şarkıdaysa
                    return;

                MyPlayer.currentIndex +=1;
                mediaPlayer.reset();

                MuzigiKoy();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyPlayer.currentIndex== 0)
                    return;

                MyPlayer.currentIndex -=1;
                mediaPlayer.reset();        //çalan  şarkı reset

                MuzigiKoy();        // yeni şarkıyı  koy
            }
        });

        playtheMusic();

    }

    private void playtheMusic(){

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentMusic.getPath());      //şarkının yolunu al
            mediaPlayer.prepare();
            mediaPlayer.start();
            seek_bar.setProgress(0);            //sfrdan  başlar
            seek_bar.setMax(mediaPlayer.getDuration());  // seekbar uzunluğu  şarkının süresine göre ayarlanır
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getIds(){
        baslikk = findViewById(R.id.baslikText);
        currentTime = findViewById(R.id.currentTextview);
        duration = findViewById(R.id.durationTextview);
        seek_bar = findViewById(R.id.seekBar);
        resumeButton = findViewById(R.id.resume);
        nextButton = findViewById(R.id.next);
        prevButton = findViewById(R.id.previous);
        music_icon = findViewById(R.id.musicIcon);

        baslikk.setSelected(true);

        MusicList = (ArrayList<Sarki>) getIntent().getSerializableExtra("LIST");

    }
    public static String milisaniye2dakika(String sure){
        Long millisaniye = Long.parseLong(sure);

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisaniye) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millisaniye) % TimeUnit.MINUTES.toSeconds(1));
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {
        public AudioManager myAudioManager;
        @Override
        public void onReceive(Context context, Intent intent) {
            myAudioManager = (AudioManager)MusicPlayer_Activity.this.getSystemService(Context.AUDIO_SERVICE);

            if ("com.zey.EXAMPLE_ACTION".equals(intent.getAction()) ){
                String receivedtext = intent.getStringExtra("com.zey.EXTRA_TEXT");
                if (receivedtext.equals("kisi hareket halinde")){
                    myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            50,
                             AudioManager.FLAG_SHOW_UI);
                }else if(receivedtext.equals("kisi duruyor")){
                    myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            0,
                            AudioManager.FLAG_SHOW_UI);
                }

            }

        }
    }
}
