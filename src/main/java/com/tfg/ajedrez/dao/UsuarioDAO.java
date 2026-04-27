package com.tfg.ajedrez.dao;

import com.tfg.ajedrez.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public boolean registrar(String nombre, String password) {
        String sql = "INSERT INTO usuarios(nombre, password) VALUES(?, ?)";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Usuario login(String nombre, String password) {
        String sql = "SELECT * FROM usuarios WHERE nombre = ? AND password = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("password")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
