package com.zey.myapplication;


import java.io.Serializable;
import java.util.Comparator;

public class Sarki implements Serializable {

    String baslik;
    String path;
    String sanatci;
    String sure;
    String album;
    String uripath;
    String size;


    public String getUripath() {
        return uripath;
    }

    public void setUripath(String uripath) {
        this.uripath = uripath;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Sarki(String baslik, String path, String sanatci, String sure, String album,
                 String uripath, String size) {
        this.baslik = baslik;
        this.path = path;
        this.sanatci = sanatci;
        this.sure = sure;
        this.album =album;
        this.uripath =uripath;
        this.size =size;
    }

    public static Comparator<Sarki> nameComparator = new Comparator<Sarki>() {
        @Override
        public int compare(Sarki s1, Sarki s2) {
            return (int) (s1.getBaslik().compareTo(s2.getBaslik()));
        }
    };

    public static Comparator<Sarki> artistComparator = new Comparator<Sarki>() {
        @Override
        public int compare(Sarki s1, Sarki s2) {
            return (int) (s1.getSanatci().compareTo(s2.getSanatci()));
        }
    };

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getPath() {
        return path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSanatci() {
        return sanatci;
    }

    public void setSanatci(String sanatci) {
        this.sanatci = sanatci;
    }

    public String getSure() {
        return sure;
    }

    public void setSure(String sure) {
        this.sure = sure;
    }
}
