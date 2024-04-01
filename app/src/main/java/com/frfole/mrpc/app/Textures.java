package com.frfole.mrpc.app;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Texture loading utility.
 */
public class Textures {

    private static final Map<String, Texture> textures = new HashMap<>();

    /**
     * Gets and optionally load app texture from resources.
     * @param path the path of the texture
     * @return the texture
     */
    public static @NotNull Texture getAppTexture(@NotNull String path) {
        // check if we have the texture already loaded
        Texture texture = textures.get(path);
        if (texture != null) return texture;
        // load the texture if not loaded and cache it
        texture = new Texture(path);
        textures.put(path, texture);
        return texture;
    }

    /**
     * Represents a texture that is loaded from resources and ready to use during rendering.
     */
    public static class Texture {
        private final int id;
        private final int w;
        private final int h;

        protected Texture(@NotNull String path) {
            // read encoded texture data from resources
            ByteBuffer inData;
            try (InputStream is = this.getClass().getResourceAsStream(path)) {
                if (is == null) throw new RuntimeException("failed to find texture " + path);
                byte[] data = is.readAllBytes();
                inData = MemoryUtil.memAlloc(data.length);
                inData.put(data);
                inData.rewind();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // decode the image
            MemoryStack stack = MemoryStack.stackPush();
            IntBuffer w    = stack.ints(0);
            IntBuffer h    = stack.ints(0);
            IntBuffer comp = stack.ints(0);
            ByteBuffer image = STBImage.stbi_load_from_memory(inData, w, h, comp, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            this.w = w.get(0);
            this.h = h.get(0);

            MemoryUtil.memFree(inData);
            stack.close();

            // bind the texture
            this.id = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.w, this.h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

            // free the decoded image buffer
            STBImage.stbi_image_free(image);
        }

        public int getId() {
            return this.id;
        }

        public int getWidth() {
            return this.w;
        }

        public int getHeight() {
            return this.h;
        }
    }
}
