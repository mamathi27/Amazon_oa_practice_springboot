package com.amazon.watchlist.model;

public class Movie {
    private Long id;
    private String title;
    private int runtimeMinutes;

    public Movie(Long id, String title, int runtimeMinutes) {
        this.id = id;
        this.title = title;
        this.runtimeMinutes = runtimeMinutes;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public int getRuntimeMinutes() { return runtimeMinutes; }
}
