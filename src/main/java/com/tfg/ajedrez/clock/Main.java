package com.tfg.ajedrez.clock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class Main extends Application {

    private ChessClock chessClock;
    private final File saveFile = new File("savegame.json");
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void start(Stage stage) {
        chessClock = new ChessClock(600);

        Label titleLabel = new Label("♔ CRONÓMETRO DE AJEDREZ");
        titleLabel.setStyle("""
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                -fx-text-fill: #f5f5f5;
                """);

        Label subtitleLabel = new Label("Modo partida virtual");
        subtitleLabel.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #9ca3af;
                """);

        Label whiteTitle = new Label("BLANCAS");
        whiteTitle.setStyle("""
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-text-fill: #e5e7eb;
                """);

        Label whiteTimeLabel = new Label();
        whiteTimeLabel.setStyle("""
                -fx-font-size: 44px;
                -fx-font-weight: bold;
                -fx-text-fill: #ffffff;
                -fx-background-color: #1f2937;
                -fx-padding: 18 30 18 30;
                -fx-background-radius: 16;
                -fx-border-radius: 16;
                -fx-border-color: #374151;
                -fx-border-width: 2;
                """);

        VBox whiteBox = new VBox(10, whiteTitle, whiteTimeLabel);
        whiteBox.setAlignment(Pos.CENTER);
        whiteBox.setPadding(new Insets(20));
        whiteBox.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #111827, #1f2937);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: #4b5563;
                -fx-border-width: 1.5;
                """);

        Label blackTitle = new Label("NEGRAS");
        blackTitle.setStyle("""
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-text-fill: #fbbf24;
                """);

        Label blackTimeLabel = new Label();
        blackTimeLabel.setStyle("""
                -fx-font-size: 44px;
                -fx-font-weight: bold;
                -fx-text-fill: #fbbf24;
                -fx-background-color: #1f2937;
                -fx-padding: 18 30 18 30;
                -fx-background-radius: 16;
                -fx-border-radius: 16;
                -fx-border-color: #6b7280;
                -fx-border-width: 2;
                """);

        VBox blackBox = new VBox(10, blackTitle, blackTimeLabel);
        blackBox.setAlignment(Pos.CENTER);
        blackBox.setPadding(new Insets(20));
        blackBox.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #111827, #1f2937);
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: #4b5563;
                -fx-border-width: 1.5;
                """);

        HBox clocksRow = new HBox(20, whiteBox, blackBox);
        clocksRow.setAlignment(Pos.CENTER);

        Label turnLabel = new Label();
        turnLabel.setStyle("""
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-text-fill: #93c5fd;
                """);

        Label statusLabel = new Label();
        statusLabel.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #d1d5db;
                """);

        VBox infoBox = new VBox(8, turnLabel, statusLabel);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(14));
        infoBox.setStyle("""
                -fx-background-color: #111827;
                -fx-background-radius: 16;
                -fx-border-radius: 16;
                -fx-border-color: #374151;
                -fx-border-width: 1;
                """);

        ComboBox<Integer> timeSelector = new ComboBox<>();
        timeSelector.getItems().addAll(1, 3, 5, 10, 15);
        timeSelector.setValue(10);
        timeSelector.setStyle("""
                -fx-font-size: 14px;
                -fx-background-color: #1f2937;
                -fx-text-fill: white;
                """);

        Button applyTimeButton = createButton("Aplicar tiempo", "#7c3aed");
        Button startButton = createButton("▶ Iniciar", "#16a34a");
        Button pauseButton = createButton("⏸ Pausar", "#dc2626");
        Button switchButton = createButton("⇄ Cambiar turno", "#2563eb");
        Button resetButton = createButton("↺ Reset", "#d97706");
        Button saveButton = createButton("💾 Guardar", "#0f766e");
        Button loadButton = createButton("📂 Cargar", "#4f46e5");

        applyTimeButton.setOnAction(e -> {
            int minutes = timeSelector.getValue();
            chessClock.reset(minutes * 60);
        });

        startButton.setOnAction(e -> chessClock.start());
        pauseButton.setOnAction(e -> chessClock.pause());
        switchButton.setOnAction(e -> chessClock.switchTurn());
        resetButton.setOnAction(e -> chessClock.reset(timeSelector.getValue() * 60));

        saveButton.setOnAction(e -> {
            try {
                objectMapper.writeValue(saveFile, chessClock.toGameState());
                statusLabel.setText("Partida guardada en savegame.json");
            } catch (Exception ex) {
                statusLabel.setText("Error al guardar");
                ex.printStackTrace();
            }
        });

        loadButton.setOnAction(e -> {
            try {
                if (saveFile.exists()) {
                    GameState state = objectMapper.readValue(saveFile, GameState.class);
                    chessClock.loadGameState(state);
                    statusLabel.setText("Partida cargada correctamente");
                } else {
                    statusLabel.setText("No existe savegame.json");
                }
            } catch (Exception ex) {
                statusLabel.setText("Error al cargar");
                ex.printStackTrace();
            }
        });

        HBox topControls = new HBox(12, new Label("Minutos:"), timeSelector, applyTimeButton);
        topControls.setAlignment(Pos.CENTER);
        topControls.setStyle("-fx-text-fill: white;");

        HBox buttonsRow1 = new HBox(15, startButton, pauseButton, switchButton, resetButton);
        buttonsRow1.setAlignment(Pos.CENTER);

        HBox buttonsRow2 = new HBox(15, saveButton, loadButton);
        buttonsRow2.setAlignment(Pos.CENTER);

        VBox root = new VBox(22, titleLabel, subtitleLabel, topControls, clocksRow, infoBox, buttonsRow1, buttonsRow2);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #0f172a, #111827, #1e293b);
                """);

        Timeline uiUpdater = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            whiteTimeLabel.setText(chessClock.getWhiteTime());
            blackTimeLabel.setText(chessClock.getBlackTime());

            if (chessClock.isFinished()) {
                turnLabel.setText("PARTIDA TERMINADA");
                statusLabel.setText(chessClock.getWinnerText());
            } else {
                turnLabel.setText("Turno: " + (chessClock.isWhiteTurn() ? "Blancas" : "Negras"));
                if (chessClock.isPaused()) {
                    if (!statusLabel.getText().contains("guardada") &&
                            !statusLabel.getText().contains("cargada") &&
                            !statusLabel.getText().contains("Error") &&
                            !statusLabel.getText().contains("No existe")) {
                        statusLabel.setText("Estado: pausado");
                    }
                } else {
                    statusLabel.setText("Estado: en marcha");
                }
            }
        }));
        uiUpdater.setCycleCount(Timeline.INDEFINITE);
        uiUpdater.play();

        whiteTimeLabel.setText(chessClock.getWhiteTime());
        blackTimeLabel.setText(chessClock.getBlackTime());
        turnLabel.setText("Turno: Blancas");
        statusLabel.setText("Estado: pausado");

        Scene scene = new Scene(root, 980, 620);
        stage.setTitle("Cronómetro");
        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setMinWidth(160);
        button.setMinHeight(48);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 14;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
