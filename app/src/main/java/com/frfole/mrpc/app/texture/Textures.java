package com.frfole.mrpc.app.texture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Texture loading utility.
 */
public class Textures {
    private static final Map<String, AppTexture> APP_TEXTURES = new HashMap<>();
    private static final Map<Path, PackTexture> PACK_TEXTURES = new HashMap<>();
    private static @Nullable PackTexture packIcon;

    /**
     * Gets and optionally load app texture from resources.
     * @param path the path of the texture
     * @return the texture
     */
    public static @NotNull AppTexture getAppTexture(@NotNull String path) {
        // check if we have the texture already loaded
        AppTexture texture = APP_TEXTURES.get(path);
        if (texture != null) return texture;
        // load the texture if not loaded and cache it
        texture = new AppTexture(path);
        APP_TEXTURES.put(path, texture);
        return texture;
    }

    public static void loadPackIcon(@NotNull Path rootPath) {
        if (packIcon != null) {
            packIcon.unload();
        }
        Path iconPath = rootPath.resolve("pack.png");
        if (Files.exists(iconPath)) {
            packIcon = new PackTexture(iconPath);
        }
    }

    public static void unloadPackIcon() {
        if (packIcon != null) {
            packIcon.unload();
            packIcon = null;
        }
    }

    public static @Nullable PackTexture getPackIcon() {
        return packIcon;
    }

    /**
     * Loads a texture from resource pack.
     * @param relativePath the path to the texture relative to the resource pack root
     * @param rootPath the root path of the resource pack
     */
    public static void loadPackTexture(@NotNull Path relativePath, @NotNull Path rootPath) {
        // check if we had the texture already loaded
        PackTexture texture = PACK_TEXTURES.get(relativePath);
        if (texture != null) {
            // reload it
            texture.load();
            return;
        }
        // load the texture
        texture = new PackTexture(rootPath.resolve(relativePath));
        PACK_TEXTURES.put(relativePath, texture);
    }

    /**
     * Gets resource pack texture.
     * @param relativePath the path to the texture relative to the resource pack root
     * @return the texture
     */
    public static @NotNull PackTexture getPackTexture(@NotNull Path relativePath) {
        PackTexture texture = PACK_TEXTURES.get(relativePath);
        if (texture != null) return texture;
        // all resource pack textures should be already loaded
        throw new RuntimeException("Texture not loaded: " + relativePath);
    }

    /**
     * Unloads resource pack texture.
     * @param path the path to the texture relative to the resource pack root.
     */
    public static void unloadPackTexture(@NotNull Path path) {
        PackTexture removed = PACK_TEXTURES.remove(path);
        if (removed == null) return;
        removed.unload();
    }

    /**
     * Unloads all resource pack textures including pack icon.
     */
    public static void unloadPackTextures() {
        for (PackTexture texture : PACK_TEXTURES.values()) {
            texture.unload();
        }
        PACK_TEXTURES.clear();
        if (packIcon != null) packIcon.unload();
    }
}
