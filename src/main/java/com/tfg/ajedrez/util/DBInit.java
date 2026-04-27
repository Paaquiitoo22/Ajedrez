package com.tfg.ajedrez.util;

import com.tfg.ajedrez.dao.ConexionSQLite;

import java.sql.Connection;
import java.sql.Statement;

public class DBInit {

    public static void inicializarBaseDeDatos() {
        try (Connection conn = ConexionSQLite.conectar();
             Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS partidas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    usuario_id INTEGER,
                    fecha TEXT NOT NULL,
                    turno TEXT NOT NULL,
                    tiempo_blancas INTEGER NOT NULL,
                    tiempo_negras INTEGER NOT NULL,
                    estado_tablero TEXT NOT NULL,
                    resultado TEXT,
                    FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
