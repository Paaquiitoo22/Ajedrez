package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.app.TableroView;
import com.tfg.ajedrez.dao.PartidaDAO;
import com.tfg.ajedrez.model.*;
import com.tfg.ajedrez.service.*;
import com.tfg.ajedrez.util.NotacionAjedrezUtil;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PartidaController {

    @FXML private BorderPane rootPane;
    @FXML private Label lblTurno;
    @FXML private Label lblTiempoBlancas;
    @FXML private Label lblTiempoNegras;
    @FXML private Label lblEstado;
    @FXML private VBox panelMovimientos;

    private TableroView tableroView;
    private CronometroService cronometroService;

    private final PartidaDAO partidaDAO = new PartidaDAO();
    private final IAService iaService = new IAService();

    private boolean modoIA = false;
    private boolean partidaTerminada = false;
    private int contadorMovimientos = 0;

    @FXML
    public void initialize() {
        tableroView = new TableroView();

        VBox contenedorTablero = new VBox(tableroView);
        contenedorTablero.setAlignment(Pos.CENTER);
        contenedorTablero.getStyleClass().add("board-wrapper");

        rootPane.setCenter(contenedorTablero);

        int tiempoInicial = 600;

        ConfiguracionPartida config = ConfiguracionPartidaService.getConfiguracionActual();
        PartidaGuardada partidaGuardada = PartidaSesionService.getPartidaActual();

        if (config != null) {
            tiempoInicial = config.getTiempoInicialSegundos();
            modoIA = "Jugador vs IA".equalsIgnoreCase(config.getModoJuego());
        }

        cronometroService = new CronometroService(
                tiempoInicial,
                this::actualizarCronometros,
                this::finPorTiempo
        );

        if (partidaGuardada != null) {
            tableroView.cargarEstadoTablero(partidaGuardada.getEstadoTablero());
            tableroView.setTurnoActualDesdeTexto(partidaGuardada.getTurno());

            cronometroService.setTiempoBlancas(partidaGuardada.getTiempoBlancas());
            cronometroService.setTiempoNegras(partidaGuardada.getTiempoNegras());

            lblEstado.setText("Partida cargada");
        } else {
            lblEstado.setText("Jugando");
            lblTurno.setText("Turno: Blancas");
        }

        tableroView.setOnCambioTurno(texto -> lblTurno.setText(texto));
        tableroView.setOnMovimientoInfo(this::agregarMovimiento);
        tableroView.setOnMovimientoRealizado(() -> {
            cronometroService.cambiarTurno();
            actualizarCronometros();
            comprobarEstado();

            if (!partidaTerminada && modoIA && tableroView.getTurnoActual() == ColorPieza.NEGRA) {
                ejecutarIA();
            }
        });

        actualizarCronometros();
        cronometroService.iniciar();
    }

    private void ejecutarIA() {
        PauseTransition pausa = new PauseTransition(Duration.millis(600));

        pausa.setOnFinished(e -> {
            if (partidaTerminada) return;

            MovimientoIA mov = iaService.calcularMovimiento(tableroView.getTablero(), ColorPieza.NEGRA);

            if (mov != null) {
                tableroView.moverDesdeFuera(
                        mov.getFilaOrigen(),
                        mov.getColOrigen(),
                        mov.getFilaDestino(),
                        mov.getColDestino()
                );
            }
        });

        pausa.play();
    }

    private void agregarMovimiento(MovimientoInfo mov) {
        contadorMovimientos++;

        Label label = new Label(NotacionAjedrezUtil.formatearMovimiento(contadorMovimientos, mov));
        label.getStyleClass().add("move-item");

        panelMovimientos.getChildren().add(label);
    }

    private void actualizarCronometros() {
        lblTiempoBlancas.setText(cronometroService.getTiempoBlancasFormateado());
        lblTiempoNegras.setText(cronometroService.getTiempoNegrasFormateado());
    }

    private void comprobarEstado() {
        boolean blancasJaque = tableroView.estanBlancasEnJaque();
        boolean negrasJaque = tableroView.estanNegrasEnJaque();

        boolean blancasMov = tableroView.tieneMovimientosLegalesBlancas();
        boolean negrasMov = tableroView.tieneMovimientosLegalesNegras();

        if (blancasJaque && !blancasMov) {
            finalizar("Jaque mate", "Ganan Negras");
        } else if (negrasJaque && !negrasMov) {
            finalizar("Jaque mate", "Ganan Blancas");
        } else if (!blancasJaque && !blancasMov) {
            finalizar("Tablas", "Tablas por ahogado");
        } else if (!negrasJaque && !negrasMov) {
            finalizar("Tablas", "Tablas por ahogado");
        } else if (blancasJaque) {
            lblEstado.setText("Jaque a Blancas");
        } else if (negrasJaque) {
            lblEstado.setText("Jaque a Negras");
        } else {
            lblEstado.setText("Jugando");
        }
    }

    private void finalizar(String titulo, String mensaje) {
        partidaTerminada = true;
        cronometroService.detener();
        lblEstado.setText(mensaje);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de partida");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void finPorTiempo() {
        partidaTerminada = true;
        cronometroService.detener();

        String ganador = cronometroService.isTurnoBlancas() ? "Negras" : "Blancas";
        finalizar("Fin por tiempo", "Ganan " + ganador);
    }

    @FXML
    public void guardarPartida() {
        Integer usuarioId = null;

        if (!SesionService.isInvitado() && SesionService.getUsuarioActual() != null) {
            usuarioId = SesionService.getUsuarioActual().getId();
        }

        boolean ok = partidaDAO.guardarPartida(
                usuarioId,
                tableroView.getTurnoActualTexto(),
                cronometroService.getTiempoBlancas(),
                cronometroService.getTiempoNegras(),
                tableroView.exportarEstadoTablero(),
                lblEstado.getText()
        );

        Alert alert = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(ok ? "Partida guardada correctamente." : "Error al guardar partida.");
        alert.showAndWait();
    }

    @FXML
    public void pausarPartida() {
        cronometroService.pausar();
        lblEstado.setText("Partida pausada");
    }

    @FXML
    public void reanudarPartida() {
        cronometroService.reanudar();
        lblEstado.setText("Jugando");
    }

    @FXML
    public void volverMenu() {
        cronometroService.detener();
        PartidaSesionService.limpiar();

        Stage stage = (Stage) rootPane.getScene().getWindow();
        NavegacionService.cambiarEscena(stage, "/com/tfg/ajedrez/fxml/menu.fxml", "Menú principal");
    }

    @FXML
    public void onGuardarPartida() {
        guardarPartida();
    }

    @FXML
    public void onVolver() {
        volverMenu();
    }
}