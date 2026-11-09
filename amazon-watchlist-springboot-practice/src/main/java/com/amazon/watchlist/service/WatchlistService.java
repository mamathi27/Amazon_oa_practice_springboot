package com.amazon.watchlist.service;

import com.amazon.watchlist.model.Movie;
import com.amazon.watchlist.model.WatchlistItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WatchlistService {

    private final Map<Long, Movie> movieDb = new HashMap<>();
    private final List<WatchlistItem> watchlistDb = new ArrayList<>();
    private long itemCounter = 1;

    // BUG 6: Global state cache leaking between requests/tests
    private final Map<String, Map<String, Integer>> staticStatsCache = new HashMap<>();

    public WatchlistService() {
        movieDb.put(1L, new Movie(1L, "Jawan", 169));
        movieDb.put(2L, new Movie(2L, "RRR", 187));
    }

    public Map<String, Object> getWatchlistAndStats(String username) {
        List<WatchlistItem> userItems = new ArrayList<>();
        for (WatchlistItem item : watchlistDb) {
            if (item.getUsername().equals(username)) {
                userItems.add(item);
            }
        }

        if (!staticStatsCache.containsKey(username)) {
            int total = userItems.size();
            int watched = 0;
            int remainingMinutes = 0;

            for (WatchlistItem item : userItems) {
                if (item.isWatched()) {
                    watched++;
                }
                // BUG 4: Adding all minutes instead of only unwatched
                remainingMinutes += item.getMovie().getRuntimeMinutes(); 
            }

            Map<String, Integer> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("watched", watched);
            stats.put("remaining_minutes", remainingMinutes);
            
            staticStatsCache.put(username, stats);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", userItems);
        response.put("stats", staticStatsCache.get(username));
        return response;
    }

    public WatchlistItem addMovie(String username, Long movieId) {
        if (movieId == null) throw new IllegalArgumentException("400");
        Movie movie = movieDb.get(movieId);
        if (movie == null) throw new NoSuchElementException("404");

        // BUG 1: Checks global DB instead of user scope
        for (WatchlistItem item : watchlistDb) {
            if (item.getMovie().getId().equals(movieId)) {
                throw new IllegalStateException("409");
            }
        }

        WatchlistItem newItem = new WatchlistItem(itemCounter++, username, movie);
        watchlistDb.add(newItem);
        return newItem;
    }

    public void markAsWatched(String username, Long itemId) {
        WatchlistItem targetedItem = null;
        for (WatchlistItem item : watchlistDb) {
            if (item.getId().equals(itemId)) {
                targetedItem = item;
                break;
            }
        }

        if (targetedItem == null) {
            throw new NoSuchElementException("404");
        }

        // BUG 3: watched_at is lost/reset to null
        if (!targetedItem.isWatched()) {
            targetedItem.setWatched(true);
            targetedItem.setWatchedAt(null); 
        }
    }

    public void deleteItem(String username, Long itemId) {
        WatchlistItem toRemove = null;
        for (WatchlistItem item : watchlistDb) {
            if (item.getId().equals(itemId)) {
                toRemove = item;
                break;
            }
        }

        if (toRemove == null) {
            throw new NoSuchElementException("404");
        }

        // BUG 2: Deletes without verifying user ownership
        watchlistDb.remove(toRemove); 
    }
}
