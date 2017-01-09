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

public class MainUi extends Application {
    @Override
    public void start(final Stage primaryStage) {
        MainUi mainUi = new MainUi();
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource("../fxml/main.fxml"));
        }catch (Exception ea){
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource(Main.SYS_PATH + "fxml/main.fxml");
                root = FXMLLoader.load(url);
            }catch (Exception eb) {
                ExceptionUtil.showDialog(ea);
                ExceptionUtil.showDialog(eb);
            }
        }

        if(root != null) {
            primaryStage.setTitle("Pirilampo - Ver.: " + mainUi.getVersion());
            primaryStage.setScene(new Scene(root, 600, 600));
            primaryStage.getIcons().add(new Image(Main.SYS_ICON));
            primaryStage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();
        }
    }

    private synchronized String getVersion(){
        return getClass().getPackage().getImplementationVersion();
    }
}
