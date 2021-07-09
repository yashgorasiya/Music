package com.yjisolutions.music.HelperClass;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import com.yjisolutions.music.Song;

import java.util.ArrayList;
import java.util.List;

public class Music {

    public static List<Song> getMusic(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            songUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        List<Song> audioList = null;
        try (@SuppressLint("Recycle") Cursor cursor = contentResolver.query(songUri,
                null,
                null,
                null,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                audioList = new ArrayList<>();
                do {
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));

                    audioList.add(new Song(url, title, artist, size, dateAdded));

                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            Toast.makeText(context, e + "Failed to Load Song", Toast.LENGTH_SHORT).show();
        }
        return audioList;
    }
}

