package com.yjisolutions.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yjisolutions.music.HelperClass.Music;
import com.yjisolutions.music.HelperClass.Shorter;
import com.yjisolutions.music.HelperClass.timerConversion;
import com.yjisolutions.music.Services.OnClearFromRecentServise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import soup.neumorphism.NeumorphFloatingActionButton;
public class MainActivity extends AppCompatActivity implements OnItemClick, Playable
{

    RecyclerView recyclerView;
    List<Song> audioList = new ArrayList<>();
    MediaPlayer mediaPlayer;
    boolean mp = false;
    SongAdapter adapter;
    SeekBar seekBarCardPlay;
    CircleLineVisualizer mVisualizer;
    NeumorphFloatingActionButton playPausePlayCard, nextPlayCard, backPlaycard, floatingActionButton;
    TextView titlePlayCard, durationLivePlayCard, durationPlayCard, titleBottom;
    SeekBar seekbar;
    private double startTime = 0;
    private final Handler myHandler = new Handler();
    int songPosition;
    Uri myUri;
    boolean seekbarFlag = true;
    NotificationManager notificationManager;
    boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    doStuff();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need Permissions");
                    builder.setMessage("This app needs permission to use Visual effects with playing songs");
                    builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityIfNeeded(intent, 101);
//                        startActivityForResult(intent, 101);
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        MainActivity.this.finish();
                    });
                    builder.show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();


        CardView songPlayCard = findViewById(R.id.songPlayCard);
        songPlayCard.setOnClickListener(v -> {

        });
        Animation utd = AnimationUtils.loadAnimation(this, R.anim.uptobottom);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentServise.class));
        }


        //SongPLAyCard
        mVisualizer = findViewById(R.id.blast);
        playPausePlayCard = findViewById(R.id.playPausePlayCard);
        nextPlayCard = findViewById(R.id.nextPlayCard);
        backPlaycard = findViewById(R.id.backPlaycard);
        titlePlayCard = findViewById(R.id.playTitle);
        durationLivePlayCard = findViewById(R.id.durationLive);
        durationPlayCard = findViewById(R.id.duration);
        seekBarCardPlay = findViewById(R.id.seekBar);

        //home
        titleBottom = findViewById(R.id.homeBottomSongName);

        ImageView songPanelUp = findViewById(R.id.songThumbUp);
        ImageView songPanelDown = findViewById(R.id.songThumbDown);
        songPanelDown.setOnClickListener(v -> {
            songPlayCard.startAnimation(utd);
            songPlayCard.setVisibility(View.INVISIBLE);
        });
        songPanelUp.setOnClickListener(v -> songPlayCard.setVisibility(View.VISIBLE));


        Toolbar toolbar = findViewById(R.id.toolbarHome);
        toolbar.setTitle("Music Player");
        SearchView searchView = findViewById(R.id.searchHome);
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

    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "yjisolution",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void sortByDate(MenuItem item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            audioList.sort(new Shorter.songSorterByDateAdded());
            adapter.updateList(audioList);
        }
    }

    public void sortByNameAs(MenuItem item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            audioList.sort(new Shorter.songSorterByNameAscending());
            adapter.updateList(audioList);
        }
    }

    public void sortByNameDes(MenuItem item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            audioList.sort(new Shorter.songSorterByNameDescending());
            adapter.updateList(audioList);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying) {
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onTrackPrevious() {
        if (songPosition != 0) {
            songPosition--;
            adapter.setSelectedPosition(songPosition);
        }
        Song temp = audioList.get(songPosition);
        onClickData(temp.getUri(), temp.getName(), songPosition);
        CreateNotification.createNotification(this, audioList.get(songPosition),
                R.drawable.pause_button, songPosition, audioList.size() - 1);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onTrackPlay() {
        CreateNotification.createNotification(this, audioList.get(songPosition),
                R.drawable.pause_button, songPosition, audioList.size() - 1);
        adapter.pause(songPosition, true);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.pause_button));
        playPausePlayCard.setImageDrawable(getDrawable(R.drawable.pause_button));
        isPlaying = true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onTrackPause() {
        CreateNotification.createNotification(this, audioList.get(songPosition),
                R.drawable.play_button, songPosition, audioList.size() - 1);
        adapter.pause(songPosition, false);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.play_button));
        playPausePlayCard.setImageDrawable(getDrawable(R.drawable.play_button));
        isPlaying = false;
    }

    @Override
    public void onTrackNext() {

        songPosition++;
        if (audioList.size() == songPosition) {
            songPosition = 0;
        }
        Song temp = audioList.get(songPosition);
        adapter.setSelectedPosition(songPosition);
        onClickData(temp.getUri(), temp.getName(), songPosition);
        CreateNotification.createNotification(this, audioList.get(songPosition),
                R.drawable.pause_button, songPosition, audioList.size() - 1);
    }

    void filter(String text) {
        List<Song> temp = new ArrayList<>();
        for (Song d : audioList) {
            if (d.getName().toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }

    private void doStuff() {
        recyclerView = findViewById(R.id.homeRecView);
        audioList = Music.getMusic(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Shorter.songSorterByNameAscending shortbyName = new Shorter.songSorterByNameAscending();
                audioList.sort(shortbyName);
                audioList = Shorter.removeDuplicates((ArrayList<Song>) audioList);
            }
        } catch (Exception exception) {
            Log.d("roor", exception.toString());

        }
        audioList = Shorter.removeDuplicates((ArrayList<Song>) audioList);

        adapter = new SongAdapter(audioList, MainActivity.this, this);
        recyclerView.setAdapter(adapter);
        Log.d("roor", audioList.toString());
//        Song sang = audioList.get(0);
//        Toast.makeText(this, ""+sang.getName(), Toast.LENGTH_SHORT).show();

    }

    private final Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            if (seekbarFlag) {
                seekbar.setProgress((int) startTime);
                seekBarCardPlay.setProgress((int) startTime);
            }
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            mVisualizer.setColor(color);
            durationLivePlayCard.setText(timerConversion.timerConversion((long) startTime));
            myHandler.postDelayed(this, 900);
        }
    };

    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickData(String value, String title, int position) {
        this.songPosition = position;
        seekbar = findViewById(R.id.seekBarHomeBottom);
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
            adapter.play(songPosition, true);
            if (mediaPlayer.getAudioSessionId() != -1) {
                mVisualizer.setAudioSessionId(mediaPlayer.getAudioSessionId());
            }
            startTime = mediaPlayer.getCurrentPosition();
            double finalTime = mediaPlayer.getDuration();
            durationPlayCard.setText(timerConversion.timerConversion((long) finalTime));
            myHandler.postDelayed(UpdateSongTime, 900);
            seekBarCardPlay.setMax((int) finalTime);
            seekbar.setMax((int) finalTime);

            // On Complete MediaPlayer
            mediaPlayer.setOnCompletionListener(mp -> {
                songPosition++;
                if (audioList.size() == songPosition) {
                    songPosition = 0;
                }
                if (mediaPlayer.getAudioSessionId() != -1) {
                    mVisualizer.setAudioSessionId(mediaPlayer.getAudioSessionId());
                }
                Song temp = audioList.get(songPosition);
                adapter.setSelectedPosition(songPosition);
                onClickData(temp.getUri(), temp.getName(), songPosition);
            });
            mp = true;

            // Notification Creation
            Song temp = audioList.get(songPosition);
            CreateNotification.createNotification(this,
                    temp,
                    R.drawable.pause_button,
                    songPosition,
                    audioList.size() - 1);


        } catch (Exception e) {
            Toast.makeText(this, " " + e, Toast.LENGTH_SHORT).show();
        }
        // Seekbar
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
                        onClickData(temp.getUri(), temp.getName(), songPosition);

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
                        onClickData(temp.getUri(), temp.getName(), songPosition);

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

        //home
        floatingActionButton = findViewById(R.id.playPauseHomeBottom);
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.pause_button));
        isPlaying = true;
        floatingActionButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
                adapter.pause(songPosition, false);
                floatingActionButton.setImageDrawable(getDrawable(R.drawable.play_button));
                playPausePlayCard.setImageDrawable(getDrawable(R.drawable.play_button));

            } else {
                mediaPlayer.start();
                isPlaying = true;
                adapter.play(songPosition, true);
                floatingActionButton.setImageDrawable(getDrawable(R.drawable.pause_button));
                playPausePlayCard.setImageDrawable(getDrawable(R.drawable.pause_button));
            }
        });
        nextPlayCard.setOnClickListener(v -> {
            songPosition++;
            if (audioList.size() == songPosition) {
                songPosition = 0;
            }
            Song temp = audioList.get(songPosition);
            adapter.setSelectedPosition(songPosition);
            onClickData(temp.getUri(), temp.getName(), songPosition);
        });

        backPlaycard.setOnClickListener(v -> {
            if (songPosition != 0) {
                songPosition--;
                adapter.setSelectedPosition(songPosition);
            }
            Song temp = audioList.get(songPosition);
            onClickData(temp.getUri(), temp.getName(), songPosition);
        });


        // Card
        playPausePlayCard.setImageDrawable(getDrawable(R.drawable.pause_button));
        playPausePlayCard.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                onTrackPause();
                isPlaying = false;
                adapter.pause(songPosition, false);
                playPausePlayCard.setImageDrawable(getDrawable(R.drawable.play_button));
                floatingActionButton.setImageDrawable(getDrawable(R.drawable.play_button));

            } else {
                mediaPlayer.start();
                onTrackPlay();
                isPlaying = true;
                adapter.play(songPosition, true);
                playPausePlayCard.setImageDrawable(getDrawable(R.drawable.pause_button));
                floatingActionButton.setImageDrawable(getDrawable(R.drawable.pause_button));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlaying) {
            mediaPlayer.stop();
        }
        notificationManager.cancelAll();
        unregisterReceiver(broadcastReceiver);
    }
}





