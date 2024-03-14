package com.github.clagomess.pirilampo.gui.component;

import javax.swing.*;
import java.util.Objects;

public final class IconComponent {
    private IconComponent() {}

    public static final ImageIcon FAVICON = new ImageIcon(Objects.requireNonNull(
            IconComponent.class.getResource("favicon-32.png")
    ));
}
