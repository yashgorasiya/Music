package com.yjisolutions.music.HelperClass;

import com.yjisolutions.music.Song;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Shorter {

    public static ArrayList<Song> removeDuplicates(ArrayList<Song> list) {
        Set<Song> set = new TreeSet<>((Comparator<Song>) (o1, o2) -> {
            if (o1.getName().equalsIgnoreCase(o2.getName())) {
                return 0;
            }
            return 1;
        });
        set.addAll(list);
        return new ArrayList<>(set);
    }


    public static class songSorterByNameAscending implements Comparator<Song> {
        @Override
        public int compare(Song o1, Song o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    public static class songSorterByNameDescending implements Comparator<Song> {
        @Override
        public int compare(Song o1, Song o2) {
            return o2.getName().compareToIgnoreCase(o1.getName());
        }
    }

    public static class songSorterByDateAdded implements Comparator<Song> {
        @Override
        public int compare(Song o1, Song o2) {
            return o2.getDateAdded().compareToIgnoreCase(o1.getDateAdded());
        }
    }
}
