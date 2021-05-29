package com.yjisolutions.music;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Song {
    private String uri;
    private String name;
    private String duration;
    private String size;

    public Song(String uri, String name, String duration, String size) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }



    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getSize() {
        return size;
    }
}
