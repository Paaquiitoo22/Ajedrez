package com.tfg.ajedrez.util;

import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.nio.file.Files;
import java.nio.file.Path;

public final class AvatarUtil {

    private AvatarUtil() {
    }

    public static void aplicarAvatar(Labeled target, String initials, String photoPath, double size) {
        if (photoPath != null && !photoPath.isBlank() && Files.exists(Path.of(photoPath))) {
            ImageView imageView = new ImageView(new Image(Path.of(photoPath).toUri().toString(), size, size, false, true));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);
            imageView.setClip(new Circle(size / 2.0, size / 2.0, size / 2.0));

            target.setText("");
            target.setGraphic(imageView);
            return;
        }

        target.setGraphic(null);
        target.setText(initials);
    }
}
