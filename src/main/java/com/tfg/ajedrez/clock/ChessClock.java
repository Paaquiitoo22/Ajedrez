package com.tfg.ajedrez.clock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class ChessClock {
    private int whiteSeconds;
    private int blackSeconds;
    private boolean whiteTurn = true;
    private boolean paused = true;
    private boolean finished = false;
    private Timeline timeline;

    public ChessClock(int initialSeconds) {
        this.whiteSeconds = initialSeconds;
        this.blackSeconds = initialSeconds;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void tick() {
        if (paused || finished) return;

        if (whiteTurn) {
            if (whiteSeconds > 0) whiteSeconds--;
            if (whiteSeconds == 0) {
                finished = true;
                paused = true;
                timeline.stop();
            }
        } else {
            if (blackSeconds > 0) blackSeconds--;
            if (blackSeconds == 0) {
                finished = true;
                paused = true;
                timeline.stop();
            }
        }
    }

    public void start() {
        if (!finished) {
            paused = false;
            timeline.play();
        }
    }

    public void pause() {
        paused = true;
    }

    public void reset(int initialSeconds) {
        pause();
        whiteSeconds = initialSeconds;
        blackSeconds = initialSeconds;
        whiteTurn = true;
        finished = false;
    }

    public void switchTurn() {
        if (!paused && !finished) {
            whiteTurn = !whiteTurn;
        }
    }

    public String getWhiteTime() {
        return format(whiteSeconds);
    }

    public String getBlackTime() {
        return format(blackSeconds);
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getWinnerText() {
        if (!finished) return "";
        return whiteSeconds == 0 ? "Ganan negras por tiempo" : "Ganan blancas por tiempo";
    }

    public GameState toGameState() {
        GameState state = new GameState();
        state.whiteSeconds = this.whiteSeconds;
        state.blackSeconds = this.blackSeconds;
        state.whiteTurn = this.whiteTurn;
        state.paused = this.paused;
        state.finished = this.finished;
        return state;
    }

    public void loadGameState(GameState state) {
        pause();
        this.whiteSeconds = state.whiteSeconds;
        this.blackSeconds = state.blackSeconds;
        this.whiteTurn = state.whiteTurn;
        this.paused = state.paused;
        this.finished = state.finished;

        if (!paused && !finished) {
            timeline.play();
        }
    }

    private String format(int total) {
        int min = total / 60;
        int sec = total % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
