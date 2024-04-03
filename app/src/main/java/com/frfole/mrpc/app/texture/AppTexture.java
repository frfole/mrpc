package com.frfole.mrpc.app.texture;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Represents a texture that is loaded from resources and ready to use during rendering.
 */
public class AppTexture implements Texture {
    private final int id;
    private final int w;
    private final int h;

    protected AppTexture(@NotNull String path) {
        // read encoded texture data from resources
        ByteBuffer inData;
        try (InputStream is = this.getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("failed to find texture " + path);
            }
            byte[] data = is.readAllBytes();
            inData = MemoryUtil.memAlloc(data.length);
            inData.put(data);
            inData.rewind();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // decode the image
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer w = stack.ints(0);
        IntBuffer h = stack.ints(0);
        IntBuffer comp = stack.ints(0);
        ByteBuffer image = STBImage.stbi_load_from_memory(inData, w, h, comp, 0);
        if (image == null) {
            stack.close();
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

    @Override
    public void load() {
        // there is no need to load texture that should not be changed and is already load
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getWidth() {
        return this.w;
    }

    @Override
    public int getHeight() {
        return this.h;
    }
}
