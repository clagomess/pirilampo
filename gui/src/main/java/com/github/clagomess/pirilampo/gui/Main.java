package com.github.clagomess.pirilampo.gui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.github.clagomess.pirilampo.gui.ui.MainUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(MainUI::new);
    }
}
