package br.com.pirilampo.main;

import br.com.pirilampo.util.ExceptionUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final String SYS_TITLE = "Pirilampo";
    public static final String SYS_PATH = "br/com/pirilampo/";
    public static final String SYS_ICON = SYS_PATH + "resources/img_01.png";

    @Override
    public void start(final Stage primaryStage) {
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource("../fxml/main.fxml"));
        }catch (Exception ea){
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource(SYS_PATH + "fxml/main.fxml");
                root = FXMLLoader.load(url);
            }catch (Exception eb) {
                ExceptionUtil.showDialog(ea);
                ExceptionUtil.showDialog(eb);
            }
        }

        if(root != null) {
            primaryStage.setTitle(SYS_TITLE);
            primaryStage.setScene(new Scene(root, 600, 600));
            primaryStage.getIcons().add(new Image(SYS_ICON));
            primaryStage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();
        }
    }


    public static void main(String[] args) {
        if(args.length > 0){
            CommandLine cmd = consoleOptions(args);
        }else{
            launch(args);
        }
    }

    private static CommandLine consoleOptions(String[] args){
        Options options = new Options();
        Option option;

        options.addOption(new Option("feature", true, "Arquivo *.feature"));
        options.addOption(new Option("feature_master", true, "Arquivo *.feature master"));
        options.addOption(new Option("feature_path", true, "Diretório contendo arquivos *.feature"));
        options.addOption(new Option("feature_path_master", true, "Diretório contendo arquivos *.feature master"));
        options.addOption(new Option("output", true, "Diretório de saída"));

        option = new Option("name", true, "Nome do projeto");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("version", true, "Versão");
        option.setRequired(true);
        options.addOption(option);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.info(e.getMessage());
            formatter.printHelp("Pirilampo", options);

            System.exit(1);
        }

        return cmd;
    }
}
