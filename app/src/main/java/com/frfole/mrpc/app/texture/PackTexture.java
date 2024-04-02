package com.frfole.mrpc.app.texture;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

public class PackTexture {
    private final Path backingFile;
    private final int id;
    private int w;
    private int h;

    public PackTexture(@NotNull Path backingFile) {
        this.backingFile = backingFile;

        // decode the image
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer w = stack.ints(0);
        IntBuffer h = stack.ints(0);
        IntBuffer comp = stack.ints(0);
        ByteBuffer image = STBImage.stbi_load(backingFile.toString(), w, h, comp, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }
        this.w = w.get(0);
        this.h = h.get(0);
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

    public void reload() {
        // decode the image
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer w = stack.ints(0);
        IntBuffer h = stack.ints(0);
        IntBuffer comp = stack.ints(0);
        ByteBuffer image = STBImage.stbi_load(backingFile.toString(), w, h, comp, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }
        this.w = w.get(0);
        this.h = h.get(0);
        stack.close();

        // bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.w, this.h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

        // free the decoded image buffer
        STBImage.stbi_image_free(image);
    }

    public void unload() {
        GL11.glBindTexture(this.id, 0);
        GL11.glDeleteTextures(this.id);
    }

    public int getId() {
        return this.id;
    }
}
