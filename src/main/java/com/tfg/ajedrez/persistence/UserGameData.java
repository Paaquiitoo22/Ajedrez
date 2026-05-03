package com.tfg.ajedrez.persistence;

import java.util.ArrayList;
import java.util.List;

public class UserGameData {

    public List<GameRecord> partidas = new ArrayList<>();
    public SavedGame partidaEnCurso;
    public UserProfile perfil = new UserProfile();

    public UserGameData() {
    }
}
