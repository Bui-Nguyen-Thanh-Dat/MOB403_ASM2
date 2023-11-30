package com.example.appchat.Model;



public class Comic {
    String id;
    String name;
    String chapter;
    String mota;
    String image;

    public Comic() {
    }

    public Comic(String id, String name, String chapter, String mota, String image) {
        this.id = id;
        this.name = name;
        this.chapter = chapter;
        this.mota = mota;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "id=" + id +
                ", mota='" + mota + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", chapter=" + chapter +
                '}';
    }
}
