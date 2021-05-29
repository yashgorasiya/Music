package com.yjisolutions.music;


public class positionInt{

    int Songposition;
    boolean changing;

    public void setChanging(boolean changing) {
        this.changing = changing;
    }

    public positionInt() {
    }

    public boolean getChanging() {
        return changing;
    }

    public int getSongposition() {
        return Songposition;
    }

    public void setSongposition(int songposition) {
        Songposition = songposition;
    }

}
