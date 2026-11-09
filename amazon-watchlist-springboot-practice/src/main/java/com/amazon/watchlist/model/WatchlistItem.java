package com.amazon.watchlist.model;

import java.time.LocalDateTime;

public class WatchlistItem {
    private Long id;
    private String username;
    private Movie movie;
    private boolean watched;
    private LocalDateTime watchedAt;

    public WatchlistItem(Long id, String username, Movie movie) {
        this.id = id;
        this.username = username;
        this.movie = movie;
        this.watched = false;
        this.watchedAt = null;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Movie getMovie() { return movie; }
    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }
    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }
}
