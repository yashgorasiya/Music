package com.yjisolutions.music;

public class Song {
    private final String uri;
    private final String name;
    private final String duration;
    private final String size;
    private final String dateAdded;


    public Song(String uri, String name, String duration, String size, String dateAdded) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.dateAdded = dateAdded;
    }

    public String getDateAdded() {
        return dateAdded;
    }

//    public void setDateAdded(String dateAdded) {
//        this.dateAdded = dateAdded;
//    }

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
