package com.shatteredpixel.shatteredpixeldungeon.utils;

public class DownloadResponse {

    private final boolean success;
    private final String message;

    public DownloadResponse(boolean success, String message) {

        this.success = success;
        this.message = message;
    }

    public boolean success() {

        return success;
    }

    public String getMessage() {

        return message;
    }
}