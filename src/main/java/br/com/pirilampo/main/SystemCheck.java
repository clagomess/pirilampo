package br.com.pirilampo.main;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class SystemCheck {
    private static final String MSG_TITLE = "System Check";
    private static final String MSG_JAVAFX = "Não foi identificado a biblioteca JavaFX em seu sistema.\n" +
            "Certifique se está instalado o 'Oracle Java 8' ou se está com o 'OpenJFX + OpenJDK 8' instalado.";
    private static final String MSG_JAVAVERSION = String.format(
            "A versão do Java não é compatível com o Pirilampo.\n" +
            "Favor certifique se está instalado o Java 8.\n" +
            "Java do sistema: %s - %s",
            System.getProperty("java.runtime.name"),
            System.getProperty("java.version")
    );

    public static void check(){
        // Java 8 Validation
        if(!"1.8".equals(System.getProperty("java.specification.version"))){
            JOptionPane.showConfirmDialog(null, MSG_JAVAVERSION, MSG_TITLE, JOptionPane.OK_CANCEL_OPTION);
            System.exit(0);
        }

        // JavaFX Validation
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException|NoClassDefFoundError e) {
            JOptionPane.showConfirmDialog(null, MSG_JAVAFX, MSG_TITLE, JOptionPane.OK_CANCEL_OPTION);
            System.exit(0);
        }
    }
}
