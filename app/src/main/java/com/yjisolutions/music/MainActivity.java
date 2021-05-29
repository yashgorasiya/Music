package com.yjisolutions.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphFloatingActionButton;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements OnItemClick {

    private static final int MY_PERMISSION_REQUEST = 101;
    private static final int MY_PERMISSION_REQUEST_WT = 201;
    RecyclerView recyclerView;
    List<Song> audioList = new ArrayList<>();
    MediaPlayer mediaPlayer;
    boolean mp = false;
    SongAdapter adapter;
    SeekBar seekBarCardPlay;
    LottieAnimationView equlizerAnimation;
    NeumorphFloatingActionButton playPausePlayCard,nextPlayCard,backPlaycard;
    TextView titlePlayCard,durationLivePlayCard,durationPlayCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NeumorphCardView songPlayCard = findViewById(R.id.songPlayCard);
        Animation btu = AnimationUtils.loadAnimation(this,R.anim.bottumtoup);
        Animation utd = AnimationUtils.loadAnimation(this,R.anim.uptobottom);


        //SongPLAyCard
        equlizerAnimation = findViewById(R.id.equlizerAnimation);
        playPausePlayCard = findViewById(R.id.playPausePlayCard);
        nextPlayCard = findViewById(R.id.nextPlayCard);
        backPlaycard = findViewById(R.id.backPlaycard);
        titlePlayCard = findViewById(R.id.playTitle);
        durationLivePlayCard = findViewById(R.id.durationLive);
        durationPlayCard = findViewById(R.id.duration);
        seekBarCardPlay = findViewById(R.id.seekBar);

        ImageView songPanelUp = findViewById(R.id.songThumbUp);
        ImageView songPanelDown = findViewById(R.id.songThumbDown);
        songPanelDown.setOnClickListener(v -> {
            songPlayCard.startAnimation(utd);
            songPlayCard.setVisibility(View.INVISIBLE);
        });
        songPanelUp.setOnClickListener(v -> {
            songPlayCard.startAnimation(btu);
            songPlayCard.setVisibility(View.VISIBLE);
        });


        Toolbar toolbar = findViewById(R.id.toolbarHome);
        toolbar.setTitle("Music Player");
        SearchView searchView  = findViewById(R.id.searchHome);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toLowerCase());
                return false;
            }
        });

        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSION_REQUEST);
        checkPermission(WRITE_EXTERNAL_STORAGE, MY_PERMISSION_REQUEST_WT);


    }


    void filter(String text){
        List<Song> temp = new ArrayList<>();
        for(Song d: audioList){
            if(d.getName().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    private void doStuff() {
        recyclerView = findViewById(R.id.homeRecView);
        getMusic();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongAdapter(audioList, MainActivity.this, this);
        recyclerView.setAdapter(adapter);

    }


    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();

        Uri songUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            songUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        try (@SuppressLint("Recycle") Cursor cursor = contentResolver.query(songUri,
                null,
                null,
                null,
                null)) {


            if (cursor != null && cursor.moveToFirst()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    audioList.add(new Song(url, title, artist, size));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Toast.makeText(this, e + "Failed to Load Song", Toast.LENGTH_SHORT).show();
        }

    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            doStuff();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == MY_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private final Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();

            if (seekbarFlag) {
                seekbar.setProgress((int) startTime);
                seekBarCardPlay.setProgress((int) startTime);
            }

            durationLivePlayCard.setText(timerConversion((long) startTime));
            myHandler.postDelayed(this, 100);
        }
    };

    SeekBar seekbar;
    private double startTime = 0;
    private final Handler myHandler = new Handler();
    int songPosition;
    Uri myUri;
    boolean seekbarFlag = true;

    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickData(String value, String title , int position) {
        this.songPosition =position;
        seekbar = findViewById(R.id.seekBarHomeBottom);
        TextView titleBottom = findViewById(R.id.homeBottomSongName);
        titleBottom.setText(title);
        titlePlayCard.setText(title);
        myUri = Uri.parse(value);
        try {
            if (mp) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
                mp = false;
            } else {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            adapter.play(songPosition,true);
            startTime = mediaPlayer.getCurrentPosition();
            double finalTime = mediaPlayer.getDuration();
            seekBarCardPlay.setMax((int) finalTime);
            seekbar.setMax((int) finalTime);

            durationPlayCard.setText(timerConversion((long) finalTime));

            seekBarCardPlay.setProgress((int) startTime);
            seekbar.setProgress((int) startTime);
            seekBarCardPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                        if (progress == 0) {
                            if (songPosition != 0) {
                                songPosition--;
                                adapter.setSelectedPosition(songPosition);
                            }
                            Song temp = audioList.get(songPosition);
                            onClickData(temp.getUri(), temp.getName() ,songPosition);

                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    seekbarFlag = false;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    seekbarFlag = true;
                }
            });
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                        if (progress == 0) {
                            if (songPosition != 0) {
                                songPosition--;
                                adapter.setSelectedPosition(songPosition);
                            }
                            Song temp = audioList.get(songPosition);
                            onClickData(temp.getUri(), temp.getName() ,songPosition);

                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    seekbarFlag = false;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    seekbarFlag = true;
                }
            });


            myHandler.postDelayed(UpdateSongTime, 100);
            mediaPlayer.setOnCompletionListener(mp -> {
                songPosition++;
                Song temp = audioList.get(songPosition);
                adapter.setSelectedPosition(songPosition);
                onClickData(temp.getUri(), temp.getName(),songPosition);
            });
            mp = true;


            nextPlayCard.setOnClickListener(v -> {
                songPosition++;
                Song temp = audioList.get(songPosition);
                adapter.setSelectedPosition(songPosition);
                onClickData(temp.getUri(), temp.getName(),songPosition);
            });

            backPlaycard.setOnClickListener(v -> {
                if (songPosition != 0) {
                    songPosition--;
                    adapter.setSelectedPosition(songPosition);
                }
                Song temp = audioList.get(songPosition);
                onClickData(temp.getUri(), temp.getName() ,songPosition);
            });

            NeumorphFloatingActionButton floatingActionButton = findViewById(R.id.playPauseHomeBottom);
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.pause_button));
            playPausePlayCard.setImageDrawable(getDrawable(R.drawable.pause_button));
            equlizerAnimation.playAnimation();
            playPausePlayCard.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    equlizerAnimation.cancelAnimation();
                    adapter.pause(songPosition,false);
                    playPausePlayCard.setImageDrawable(getDrawable(R.drawable.play_button));

                } else {
                    mediaPlayer.start();
                    equlizerAnimation.playAnimation();
                    adapter.play(songPosition,true);
                    playPausePlayCard.setImageDrawable(getDrawable(R.drawable.pause_button));
                }
            });
            floatingActionButton.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    adapter.pause(songPosition,false);
                    floatingActionButton.setImageDrawable(getDrawable(R.drawable.play_button));

                } else {
                    mediaPlayer.start();
                    adapter.play(songPosition,true);
                    floatingActionButton.setImageDrawable(getDrawable(R.drawable.pause_button));
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, " " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    public String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}





