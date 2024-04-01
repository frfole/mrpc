package com.frfole.mrpc.pack.text.style;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

public class MutableStyleColor {
    private int color;
    private boolean isSet;

    public MutableStyleColor(int color, boolean isSet) {
        this.color = color & 0xffffff;
        this.isSet = isSet;
    }

    public MutableStyleColor(int red, int green, int blue) {
        this((red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff), true);
    }

    public MutableStyleColor(int color) {
        this(color, false);
    }

    public MutableStyleColor() {
        this(0x000000, false);
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int red, int green, int blue) {
        this.color = (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
        this.isSet = true;
    }

    public boolean isSet() {
        return isSet;
    }

    public void unset() {
        this.isSet = false;
    }

    public @Nullable TextColor toAdventure() {
        if (isSet) {
            return TextColor.color(this.color);
        } else {
            return null;
        }
    }

    public int getRed() {
        return (color >> 16) & 0xff;
    }

    public int getGreen() {
        return (color >> 8) & 0xff;
    }

    public int getBlue() {
        return color & 0xff;
    }
}
