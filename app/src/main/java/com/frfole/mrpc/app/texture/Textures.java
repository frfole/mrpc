package com.frfole.mrpc.app.texture;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Texture loading utility.
 */
public class Textures {

    private static final Map<String, Texture> APP_TEXTURES = new HashMap<>();
    private static final Map<Path, PackTexture> PACK_TEXTURES = new HashMap<>();

    /**
     * Gets and optionally load app texture from resources.
     * @param path the path of the texture
     * @return the texture
     */
    public static @NotNull Texture getAppTexture(@NotNull String path) {
        // check if we have the texture already loaded
        Texture texture = APP_TEXTURES.get(path);
        if (texture != null) return texture;
        // load the texture if not loaded and cache it
        texture = new Texture(path);
        APP_TEXTURES.put(path, texture);
        return texture;
    }

    public static @NotNull PackTexture getPackTexture(@NotNull Path path) {
        PackTexture texture = PACK_TEXTURES.get(path);
        if (texture != null) return texture;
        // load the texture if not loaded and cache it
        texture = new PackTexture(path);
        PACK_TEXTURES.put(path, texture);
        return texture;
    }

    public static void removePackTexture(@NotNull Path path) {
        PackTexture removed = PACK_TEXTURES.remove(path);
        if (removed == null) return;
        removed.unload();
    }
}
