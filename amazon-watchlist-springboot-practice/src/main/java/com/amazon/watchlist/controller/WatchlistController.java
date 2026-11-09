package com.amazon.watchlist.controller;

import com.amazon.watchlist.model.WatchlistItem;
import com.amazon.watchlist.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    private String authenticate(String xUser) {
        if (xUser == null || xUser.trim().isEmpty()) {
            throw new SecurityException("401");
        }
        return xUser;
    }

    @GetMapping("/")
    public ResponseEntity<?> getWatchlist(@RequestHeader(value = "X-User", required = false) String xUser) {
        try {
            String username = authenticate(xUser);
            return ResponseEntity.ok(watchlistService.getWatchlistAndStats(username));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing header");
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addMovie(@RequestHeader(value = "X-User", required = false) String xUser,
                                      @RequestBody Map<String, Long> body) {
        try {
            String username = authenticate(xUser);
            if (!body.containsKey("movie_id")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing movie_id");
            }
            WatchlistItem item = watchlistService.addMovie(username, body.get("movie_id"));
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{itemId}/watched/")
    public ResponseEntity<?> markWatched(@RequestHeader(value = "X-User", required = false) String xUser,
                                         @PathVariable Long itemId) {
        try {
            String username = authenticate(xUser);
            watchlistService.markAsWatched(username, itemId);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{itemId}/")
    public ResponseEntity<?> deleteItem(@RequestHeader(value = "X-User", required = false) String xUser,
                                        @PathVariable Long itemId) {
        try {
            String username = authenticate(xUser);
            watchlistService.deleteItem(username, itemId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
