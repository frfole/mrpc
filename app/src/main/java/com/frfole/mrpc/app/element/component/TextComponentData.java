package com.frfole.mrpc.app.element.component;

import com.frfole.mrpc.pack.text.MutableTextComponent;
import imgui.type.ImString;
import org.jetbrains.annotations.NotNull;

public class TextComponentData extends ComponentData<MutableTextComponent> {
    private final ImString content;

    public TextComponentData(@NotNull MutableTextComponent component) {
        super(component);
        this.content = new ImString(component.getContent());
        this.content.resize(255);
    }

    public @NotNull ImString getContent() {
        return content;
    }

    @Override
    public void save() {
        component.setContent(this.content.get());
    }
}
