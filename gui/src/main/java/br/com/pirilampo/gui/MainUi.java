package br.com.pirilampo.gui;

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
            URL url = Thread.currentThread().getContextClassLoader().getResource("fxml/main.fxml");

            if(url == null){
                throw new Exception("Falha ao carregar o MAIN_FXML");
            }

            root = FXMLLoader.load(url);
        }catch (Throwable e){
            ExceptionUtil.showDialog(e);
        }

        if(root != null) {
            primaryStage.setTitle("Pirilampo - Ver.: " + mainUi.getVersion());
            primaryStage.setScene(new Scene(root, 600, 650));
            primaryStage.getIcons().add(new Image("img_01.png"));
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
