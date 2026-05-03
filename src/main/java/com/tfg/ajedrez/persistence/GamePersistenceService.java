package com.tfg.ajedrez.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class GamePersistenceService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Path DATA_DIR = Path.of(System.getProperty("user.home"), ".tfg-ajedrez");

    private GamePersistenceService() {
    }

    public static GameStats calcularEstadisticas(String userId) {
        int victorias = 0;
        int derrotas = 0;
        int tablas = 0;

        for (GameRecord record : cargarHistorial(userId)) {
            if (GameRecord.RESULTADO_VICTORIA.equals(record.resultado)) {
                victorias++;
            } else if (GameRecord.RESULTADO_DERROTA.equals(record.resultado)) {
                derrotas++;
            } else if (GameRecord.RESULTADO_TABLAS.equals(record.resultado)) {
                tablas++;
            }
        }

        return new GameStats(victorias, derrotas, tablas);
    }

    public static List<GameRecord> cargarHistorial(String userId) {
        UserGameData data = cargarDatosUsuario(userId);
        List<GameRecord> partidas = new ArrayList<>(data.partidas);
        partidas.sort(Comparator.comparing((GameRecord record) -> record.fechaIso == null ? "" : record.fechaIso).reversed());
        return partidas;
    }

    public static Optional<SavedGame> cargarPartidaEnCurso(String userId) {
        return Optional.ofNullable(cargarDatosUsuario(userId).partidaEnCurso);
    }

    public static boolean existePartidaEnCurso(String userId) {
        return cargarPartidaEnCurso(userId).isPresent();
    }

    public static void guardarPartidaEnCurso(String userId, SavedGame savedGame) {
        if (savedGame == null) {
            return;
        }

        UserGameData data = cargarDatosUsuario(userId);
        data.partidaEnCurso = savedGame;
        guardarDatosUsuario(userId, data);
    }

    public static void eliminarPartidaEnCurso(String userId) {
        UserGameData data = cargarDatosUsuario(userId);
        data.partidaEnCurso = null;
        guardarDatosUsuario(userId, data);
    }

    public static void registrarPartidaFinalizada(String userId, GameRecord record) {
        if (record == null) {
            return;
        }

        UserGameData data = cargarDatosUsuario(userId);
        data.partidaEnCurso = null;
        data.partidas.add(record);
        guardarDatosUsuario(userId, data);
    }

    public static UserProfile cargarPerfil(String userId) {
        UserGameData data = cargarDatosUsuario(userId);
        if (data.perfil == null) {
            data.perfil = new UserProfile();
        }
        return data.perfil;
    }

    public static void guardarFotoPerfil(String userId, Path origen) {
        if (origen == null || !Files.exists(origen)) {
            return;
        }

        try {
            Files.createDirectories(DATA_DIR);
            Path destino = DATA_DIR.resolve(safeFileName(userId) + "-avatar" + extension(origen));
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);

            UserGameData data = cargarDatosUsuario(userId);
            if (data.perfil == null) {
                data.perfil = new UserProfile();
            }
            data.perfil.photoPath = destino.toString();
            guardarDatosUsuario(userId, data);
        } catch (IOException e) {
            System.err.println("[PERSISTENCIA] No se pudo guardar la foto de perfil: " + e.getMessage());
        }
    }

    public static void eliminarFotoPerfil(String userId) {
        UserGameData data = cargarDatosUsuario(userId);
        if (data.perfil == null) {
            data.perfil = new UserProfile();
        }
        data.perfil.photoPath = null;
        guardarDatosUsuario(userId, data);
    }

    public static String fechaTexto(String fechaIso) {
        if (fechaIso == null || fechaIso.isBlank()) {
            return "";
        }

        try {
            return LocalDateTime.parse(fechaIso).format(DISPLAY_DATE);
        } catch (Exception e) {
            return fechaIso;
        }
    }

    public static String nowIso() {
        return LocalDateTime.now().toString();
    }

    private static UserGameData cargarDatosUsuario(String userId) {
        Path file = userFile(userId);
        if (!Files.exists(file)) {
            return new UserGameData();
        }

        try {
            UserGameData data = OBJECT_MAPPER.readValue(file.toFile(), UserGameData.class);
            if (data.partidas == null) {
                data.partidas = new ArrayList<>();
            }
            if (data.perfil == null) {
                data.perfil = new UserProfile();
            }
            return data;
        } catch (IOException e) {
            System.err.println("[PERSISTENCIA] No se pudieron cargar los datos: " + e.getMessage());
            return new UserGameData();
        }
    }

    private static void guardarDatosUsuario(String userId, UserGameData data) {
        try {
            Files.createDirectories(DATA_DIR);
            OBJECT_MAPPER.writeValue(userFile(userId).toFile(), data);
        } catch (IOException e) {
            System.err.println("[PERSISTENCIA] No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    private static Path userFile(String userId) {
        return DATA_DIR.resolve(safeFileName(userId) + ".json");
    }

    private static String safeFileName(String userId) {
        String base = userId == null || userId.isBlank() ? "guest" : userId;
        return base.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
    }

    private static String extension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return ".png";
        }

        String extension = fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
        return extension.matches("\\.(png|jpg|jpeg|gif)") ? extension : ".png";
    }
}
