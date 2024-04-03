package com.frfole.mrpc.pack.meta;

import com.frfole.mrpc.pack.Project;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PackMeta extends Meta {
    public static final int DEFAULT_FORMAT = 22;
    private int formatNative = DEFAULT_FORMAT;
    private int formatMin = DEFAULT_FORMAT;
    private int formatMax = DEFAULT_FORMAT;
    private Component description = Component.empty();

    public PackMeta(@NotNull Path backingFile) {
        super(backingFile);
    }

    @Override
    public void load() throws IOException {
        if (Files.notExists(this.getBackingFile())) {
            Files.createFile(this.getBackingFile());
        }
        super.load();
    }

    @Override
    void loadJson(@NotNull JsonElement je) {
        if (je.isJsonObject()) {
            JsonObject jo = je.getAsJsonObject();

            // parse pack object
            JsonObject objPack = jo.getAsJsonObject("pack");
            if (objPack != null) {
                this.formatNative = objPack.get("pack_format").getAsInt();

                JsonElement elFormats = objPack.get("supported_formats");
                if (elFormats != null && elFormats.isJsonObject()) {
                    this.formatMin = elFormats.getAsJsonObject().get("min_inclusive").getAsInt();
                    this.formatMax = elFormats.getAsJsonObject().get("max_inclusive").getAsInt();
                } else if (elFormats != null && elFormats.isJsonArray()) {
                    this.formatMin = elFormats.getAsJsonArray().get(0).getAsInt();
                    this.formatMax = elFormats.getAsJsonArray().get(1).getAsInt();
                } else {
                    this.formatMin = this.formatNative;
                    this.formatMax = this.formatNative;
                }

                JsonElement elDescription = objPack.get("description");
                if (elDescription instanceof JsonPrimitive jDescription) {
                    this.description = LegacyComponentSerializer.legacySection().deserialize(jDescription.getAsString());
                } else if (elDescription != null) {
                    this.description = GsonComponentSerializer.gson().deserializeFromTree(elDescription);
                } else {
                    this.description = Component.empty();
                }
            }
        }
    }

    @Override
    void saveJson(@NotNull JsonWriter writer) throws IOException {
        writer.beginObject();
        {
            writer.name("pack").beginObject();
            {
                writer.name("pack_format").value(this.formatNative);
                writer.name("supported_formats").beginObject()
                        .name("min_inclusive").value(this.formatMin)
                        .name("max_inclusive").value(this.formatMax)
                        .endObject();
                writer.name("description");
                Streams.write(GsonComponentSerializer.gson().serializeToTree(description), writer);
            }
            writer.endObject();
        }
        writer.endObject();
    }

    public int getFormatNative() {
        return formatNative;
    }

    public int getFormatMin() {
        return formatMin;
    }

    public int getFormatMax() {
        return formatMax;
    }

    public void setFormatNative(int formatNative) {
        this.formatNative = formatNative;
    }

    public void setFormatMin(int formatMin) {
        this.formatMin = formatMin;
    }

    public void setFormatMax(int formatMax) {
        this.formatMax = formatMax;
    }

    public @NotNull Component getDescription() {
        return this.description;
    }

    public void setDescription(@NotNull Component description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PackMeta{" +
                "formatNative=" + formatNative +
                ", formatMin=" + formatMin +
                ", formatMax=" + formatMax +
                ", description=" + description +
                '}';
    }
}
