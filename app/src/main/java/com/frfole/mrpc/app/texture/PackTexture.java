package com.frfole.mrpc.app.texture;

import com.frfole.mrpc.pack.resource.FileResource;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

/**
 * Represents a texture that has been loaded from resource pack.
 */
public class PackTexture extends FileResource implements Texture {
    private final int id;
    private int w;
    private int h;

    /**
     * Creates a new texture instance backed by a file and loads it.
     * @param texturePath path to the texture
     */
    public PackTexture(@NotNull Path texturePath) {
        super(texturePath);
        this.id = GL11.glGenTextures();
        load();
    }

    @Override
    public void load() {
        // decode the image
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer w = stack.ints(0);
        IntBuffer h = stack.ints(0);
        IntBuffer comp = stack.ints(0);
        ByteBuffer image = STBImage.stbi_load(backingFile.toString(), w, h, comp, 0);
        if (image == null) {
            stack.close();
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }
        this.w = w.get(0);
        this.h = h.get(0);
        int comp1 = comp.get(0);
        stack.close();

        // bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        int format;
        if (comp1 == 1) {
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 3);
            format = GL11.GL_LUMINANCE;
        } else if (comp1 == 2) {
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2);
            format = GL11.GL_LUMINANCE_ALPHA;
        } else if (comp1 == 3) {
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            format = GL11.GL_RGB;
        } else if (comp1 == 4) {
            format = GL11.GL_RGBA;
        } else {
            System.out.println(comp1 + ":" + backingFile);
            format = GL11.GL_RGBA;
        }
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, this.w, this.h, 0, format, GL11.GL_UNSIGNED_BYTE, image);

        // free the decoded image buffer
        STBImage.stbi_image_free(image);
    }

    public void unload() {
        GL11.glBindTexture(this.id, 0);
        GL11.glDeleteTextures(this.id);
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

    @Override
    public void save() {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public void load(@NotNull BufferedReader reader) {
        // TODO: use reader instead of primary implementation
        load();
    }

    @Override
    public void save(@NotNull BufferedWriter writer) {
        throw new RuntimeException("Operation not supported");
    }
}
