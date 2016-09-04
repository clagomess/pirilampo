package br.com.pirilampo.main;

import br.com.pirilampo.util.ExceptionUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
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
        launch(args);
    }
}
