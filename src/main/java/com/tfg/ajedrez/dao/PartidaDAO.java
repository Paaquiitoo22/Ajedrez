package com.tfg.ajedrez.dao;

import com.tfg.ajedrez.model.PartidaGuardada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {

    public boolean guardarPartida(Integer usuarioId,
                                  String turno,
                                  int tiempoBlancas,
                                  int tiempoNegras,
                                  String estadoTablero,
                                  String resultado) {

        String sql = """
                INSERT INTO partidas(usuario_id, fecha, turno, tiempo_blancas, tiempo_negras, estado_tablero, resultado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (usuarioId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, usuarioId);
            }

            ps.setString(2, LocalDateTime.now().toString());
            ps.setString(3, turno);
            ps.setInt(4, tiempoBlancas);
            ps.setInt(5, tiempoNegras);
            ps.setString(6, estadoTablero);
            ps.setString(7, resultado);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public PartidaGuardada obtenerUltimaPartida(Integer usuarioId) {
        String sql;

        if (usuarioId == null) {
            sql = """
                    SELECT * FROM partidas
                    WHERE usuario_id IS NULL
                    ORDER BY id DESC
                    LIMIT 1
                    """;
        } else {
            sql = """
                    SELECT * FROM partidas
                    WHERE usuario_id = ?
                    ORDER BY id DESC
                    LIMIT 1
                    """;
        }

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (usuarioId != null) {
                ps.setInt(1, usuarioId);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Integer userId = rs.getObject("usuario_id") == null
                        ? null
                        : rs.getInt("usuario_id");

                return new PartidaGuardada(
                        rs.getInt("id"),
                        userId,
                        rs.getString("fecha"),
                        rs.getString("turno"),
                        rs.getInt("tiempo_blancas"),
                        rs.getInt("tiempo_negras"),
                        rs.getString("estado_tablero"),
                        rs.getString("resultado")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PartidaGuardada> obtenerTodasLasPartidas(Integer usuarioId) {
        List<PartidaGuardada> lista = new ArrayList<>();

        String sql;
        if (usuarioId == null) {
            sql = """
                    SELECT * FROM partidas
                    WHERE usuario_id IS NULL
                    ORDER BY id DESC
                    """;
        } else {
            sql = """
                    SELECT * FROM partidas
                    WHERE usuario_id = ?
                    ORDER BY id DESC
                    """;
        }

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (usuarioId != null) {
                ps.setInt(1, usuarioId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer userId = rs.getObject("usuario_id") == null
                        ? null
                        : rs.getInt("usuario_id");

                lista.add(new PartidaGuardada(
                        rs.getInt("id"),
                        userId,
                        rs.getString("fecha"),
                        rs.getString("turno"),
                        rs.getInt("tiempo_blancas"),
                        rs.getInt("tiempo_negras"),
                        rs.getString("estado_tablero"),
                        rs.getString("resultado")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public PartidaGuardada obtenerPartidaPorId(int id) {
        String sql = "SELECT * FROM partidas WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Integer userId = rs.getObject("usuario_id") == null
                        ? null
                        : rs.getInt("usuario_id");

                return new PartidaGuardada(
                        rs.getInt("id"),
                        userId,
                        rs.getString("fecha"),
                        rs.getString("turno"),
                        rs.getInt("tiempo_blancas"),
                        rs.getInt("tiempo_negras"),
                        rs.getString("estado_tablero"),
                        rs.getString("resultado")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean borrarPartida(int id) {
        String sql = "DELETE FROM partidas WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}