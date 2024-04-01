package com.frfole.mrpc.app.view;

public interface View {
    default void onOpen() {}

    default void process() {}

    default void onClose() {}
}
