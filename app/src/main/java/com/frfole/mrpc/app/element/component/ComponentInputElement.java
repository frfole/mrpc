package com.frfole.mrpc.app.element.component;

import com.frfole.mrpc.app.translation.I18n;
import com.frfole.mrpc.pack.text.MutableComponent;
import com.frfole.mrpc.pack.text.MutableTextComponent;
import com.frfole.mrpc.pack.text.style.MutableStyleColor;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

public class ComponentInputElement {
    private static final String[] CHILD_NEW_TYPES = new String[] {
            I18n.translate("element.component_input.type.text")
    };
    private static final String[] DECORATION_STATES = new String[] {
            I18n.translate("element.component_input.decoration_state.not_set"),
            I18n.translate("element.component_input.decoration_state.true"),
            I18n.translate("element.component_input.decoration_state.false"),
    };
    private static final ImInt CHILD_NEW_TYPE = new ImInt(0);
    private final List<ComponentInputElement> children;
    private final ComponentData<?> data;

    public ComponentInputElement(MutableComponent component) {
        this.children = new ArrayList<>();
        for (MutableComponent child : component.getChildren()) {
            this.children.add(new ComponentInputElement(child));
        }
        switch (component) {
            case MutableTextComponent textComponent -> data = new TextComponentData(textComponent);
            default -> throw new RuntimeException("unsupported component type: " + component.getClass());
        }
    }

    public MutableComponent getComponent() {
        return this.data.getComponent();
    }

    public void draw() {
        if (ImGui.treeNodeEx(I18n.translate("element.component_input.type.text") + "##" + this.hashCode(), ImGuiTreeNodeFlags.DefaultOpen)) {
            // display component dependant fields
            switch (data) {
                case TextComponentData textData -> {
                    if (ImGui.inputText(I18n.translate("element.component_input.text.text"), textData.getContent(), ImGuiInputTextFlags.None)) {
                        textData.save();
                    }
                }
                default -> {
                }
            }

            // display color customization
            MutableStyleColor color = getComponent().getStyle().getColor();
            if (ImGui.checkbox(I18n.translate("element.component_input.style.has_color"), color.isSet())) {
                if (color.isSet()) {
                    color.unset();
                } else {
                    color.setColor(255, 255, 255);
                    data.loadColor();
                }
            }
            if (color.isSet() && ImGui.colorEdit3(I18n.translate("element.component_input.style.color"), data.getColor())) {
                data.saveColor();
            }

            // display decoration customization
            for (TextDecoration type : TextDecoration.values()) {
                if (ImGui.combo(switch (type) {
                    case OBFUSCATED -> I18n.translate("element.component_input.decoration.obfuscated");
                    case BOLD -> I18n.translate("element.component_input.decoration.bold");
                    case STRIKETHROUGH -> I18n.translate("element.component_input.decoration.strikethrough");
                    case UNDERLINED -> I18n.translate("element.component_input.decoration.underlined");
                    case ITALIC -> I18n.translate("element.component_input.decoration.italic");
                }, data.getDecoration(type), DECORATION_STATES)) {
                    data.saveDecoration(type);
                }
            }

            // display children
            ImGui.text(I18n.translate("element.component_input.children"));
            for (int i = 0; i < children.size(); i++) {
                if (ImGui.button(I18n.translate("element.component_input.children.remove") + "##" + i)) {
                    System.out.println(i);
                    children.remove(i);
                    this.data.getComponent().getChildren().remove(i);
                    i--;
                } else {
                    ImGui.sameLine();
                    children.get(i).draw();
                }
            }

            // display child add button
            if (ImGui.button(I18n.translate("element.component_input.children.add"))) {
                MutableComponent newChild = switch (CHILD_NEW_TYPE.get()) {
                    case 0 -> new MutableTextComponent();
                    default -> MutableComponent.empty();
                };
                this.data.getComponent().getChildren().add(newChild);
                this.children.add(new ComponentInputElement(newChild));
            }

            // display new child type
            ImGui.sameLine();
            ImGui.pushItemWidth(100f);
            ImGui.combo(I18n.translate("element.component_input.children.type"), CHILD_NEW_TYPE, CHILD_NEW_TYPES);
            ImGui.popItemWidth();
            ImGui.treePop();
        }
    }
}
