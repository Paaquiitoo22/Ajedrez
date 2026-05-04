package com.tfg.ajedrez.controller;

import com.tfg.ajedrez.state.AppSession;
import com.tfg.ajedrez.state.DificultadIA;
import com.tfg.ajedrez.state.NuevaPartidaSettings;
import com.tfg.ajedrez.state.ThemeManager;
import com.tfg.ajedrez.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class NuevaPartidaController {

    @FXML private Button btnClasica, btnBlitz, btnRapida;
    @FXML private Button btnContraIA, btnDosJugadores;
    @FXML private Button btnFacil, btnNormal, btnDificil;
    @FXML private Button btnBlancas, btnNegras, btnAleatorio;

    @FXML private Label lblTiempoPrincipal, lblTiempoSub;
    @FXML private Slider sliderSonido;
    @FXML private Label lblSonidoValor;

    @FXML private ToggleButton toggleCoordenadas, toggleResaltar, toggleAnimaciones, toggleDeshacer, toggleGuardar;

    @FXML private Label chipModo, chipTipo, chipColor, chipDificultad, chipTiempo;

    @FXML private VBox bloqueDificultadIA;
    @FXML private VBox menuDesplegable;
    @FXML private Region RegionMenu;
    @FXML private Button btnAjustes;

    private NuevaPartidaSettings settings = NuevaPartidaSettings.defaults();

    @FXML
    private void initialize() {
        seleccionar(btnClasica, btnClasica, btnBlitz, btnRapida);
        seleccionar(btnContraIA, btnContraIA, btnDosJugadores);
        seleccionar(btnNormal, btnFacil, btnNormal, btnDificil);
        seleccionar(btnBlancas, btnBlancas, btnNegras, btnAleatorio);

        sliderSonido.setValue(settings.getSonido());
        lblSonidoValor.setText(settings.getSonido() + "%");

        sliderSonido.valueProperty().addListener((obs, oldValue, newValue) -> {
            settings.setSonido((int) Math.round(newValue.doubleValue()));
            lblSonidoValor.setText(settings.getSonido() + "%");
        });

        toggleCoordenadas.setSelected(settings.isMostrarCoordenadas());
        toggleResaltar.setSelected(settings.isResaltarUltimoMovimiento());
        toggleAnimaciones.setSelected(settings.isAnimaciones());
        toggleDeshacer.setSelected(settings.isPermitirDeshacer());
        toggleGuardar.setSelected(settings.isGuardarAutomaticamente());

        toggleCoordenadas.selectedProperty().addListener((obs, oldValue, selected) ->
                settings.setMostrarCoordenadas(selected));

        toggleResaltar.selectedProperty().addListener((obs, oldValue, selected) ->
                settings.setResaltarUltimoMovimiento(selected));

        toggleAnimaciones.selectedProperty().addListener((obs, oldValue, selected) ->
                settings.setAnimaciones(selected));

        toggleDeshacer.selectedProperty().addListener((obs, oldValue, selected) ->
                settings.setPermitirDeshacer(selected));

        toggleGuardar.selectedProperty().addListener((obs, oldValue, selected) ->
                settings.setGuardarAutomaticamente(selected));

        actualizarDisponibilidadDeshacer();
        actualizarVisibilidadDificultadIA();
        actualizarResumen();
        actualizarTextoTema();
    }

    @FXML
    public void onModoJuego(ActionEvent e) {
        Button pulsado = (Button) e.getSource();
        seleccionar(pulsado, btnClasica, btnBlitz, btnRapida);

        if (pulsado == btnBlitz) {
            settings.setModoJuego(NuevaPartidaSettings.MODO_BLITZ);
            settings.setTiempoSegundos(5 * 60);
        } else if (pulsado == btnRapida) {
            settings.setModoJuego(NuevaPartidaSettings.MODO_RAPIDA);
            settings.setTiempoSegundos(10 * 60);
        } else {
            settings.setModoJuego(NuevaPartidaSettings.MODO_CLASICA);
            settings.setTiempoSegundos(30 * 60);
        }

        actualizarResumen();
    }

    @FXML
    public void onTipoPartida(ActionEvent e) {
        Button pulsado = (Button) e.getSource();
        seleccionar(pulsado, btnContraIA, btnDosJugadores);

        if (pulsado == btnDosJugadores) {
            settings.setTipoPartida(NuevaPartidaSettings.TIPO_LOCAL);
        } else {
            settings.setTipoPartida(NuevaPartidaSettings.TIPO_CONTRA_IA);
        }

        actualizarDisponibilidadDeshacer();
        actualizarVisibilidadDificultadIA();
        actualizarResumen();
    }

    @FXML
    public void onDificultadIA(ActionEvent e) {
        Button pulsado = (Button) e.getSource();
        seleccionar(pulsado, btnFacil, btnNormal, btnDificil);

        if (pulsado == btnFacil) {
            settings.setDificultadIA(DificultadIA.FACIL);
        } else if (pulsado == btnDificil) {
            settings.setDificultadIA(DificultadIA.DIFICIL);
        } else {
            settings.setDificultadIA(DificultadIA.NORMAL);
        }

        actualizarResumen();
    }

    @FXML
    public void onJugarCon(ActionEvent e) {
        Button pulsado = (Button) e.getSource();
        seleccionar(pulsado, btnBlancas, btnNegras, btnAleatorio);

        if (pulsado == btnNegras) {
            settings.setColorJugador(NuevaPartidaSettings.COLOR_NEGRAS);
        } else if (pulsado == btnAleatorio) {
            settings.setColorJugador(NuevaPartidaSettings.COLOR_ALEATORIO);
        } else {
            settings.setColorJugador(NuevaPartidaSettings.COLOR_BLANCAS);
        }

        actualizarResumen();
    }

    @FXML
    public void onVolver() {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/menu-principal.fxml");
    }

    @FXML
    public void onPartida() {
        AppSession.setNuevaPartidaSettings(settings.copy());
        SceneManager.navegarA("/com/tfg/ajedrez/vista/partida.fxml");
    }

    @FXML
    public void onTiempoPartida() {
        actualizarResumen();
    }

    @FXML
    public void onDesplegable(ActionEvent event) {
        boolean visible = !menuDesplegable.isVisible();
        menuDesplegable.setVisible(visible);
        RegionMenu.setVisible(visible);
    }

    @FXML
    public void onCerrarMenu() {
        if (menuDesplegable.isVisible() && RegionMenu.isVisible()) {
            menuDesplegable.setVisible(false);
            RegionMenu.setVisible(false);
        }
    }

    @FXML
    public void onCambiarTema(ActionEvent event) {
        ThemeManager.toggleTheme();

        if (menuDesplegable.getScene() != null) {
            ThemeManager.applyTheme(menuDesplegable.getScene().getRoot());
        }

        actualizarTextoTema();
        onCerrarMenu();
    }

    @FXML
    public void onCerrarSesion(ActionEvent event) {
        SceneManager.navegarA("/com/tfg/ajedrez/vista/login.fxml");
    }

    private void seleccionar(Button seleccionado, Button... grupo) {
        for (Button button : grupo) {
            button.getStyleClass().remove("seleccionado");
        }

        if (!seleccionado.getStyleClass().contains("seleccionado")) {
            seleccionado.getStyleClass().add("seleccionado");
        }
    }

    private void actualizarDisponibilidadDeshacer() {
        boolean contraIa = NuevaPartidaSettings.TIPO_CONTRA_IA.equals(settings.getTipoPartida());

        toggleDeshacer.setDisable(!contraIa);

        if (!contraIa) {
            toggleDeshacer.setSelected(false);
            settings.setPermitirDeshacer(false);
        }
    }

    private void actualizarVisibilidadDificultadIA() {
        boolean contraIa = NuevaPartidaSettings.TIPO_CONTRA_IA.equals(settings.getTipoPartida());

        if (bloqueDificultadIA != null) {
            bloqueDificultadIA.setVisible(contraIa);
            bloqueDificultadIA.setManaged(contraIa);
        }

        if (chipDificultad != null) {
            chipDificultad.setVisible(contraIa);
            chipDificultad.setManaged(contraIa);
        }
    }

    private void actualizarResumen() {
        lblTiempoPrincipal.setText(settings.getTiempoPrincipalLabel());
        lblTiempoSub.setText(settings.getTiempoSubLabel());

        chipModo.setText(settings.getModoLabel());
        chipTipo.setText(settings.getTipoLabel());
        chipColor.setText(settings.getColorLabel());

        if (chipDificultad != null) {
            chipDificultad.setText(settings.getDificultadLabel());
        }

        chipTiempo.setText(settings.getTiempoLabel());
    }

    private void actualizarTextoTema() {
        btnAjustes.setText(ThemeManager.getMenuLabel());
    }
}