package br.com.pirilampo.util;


import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtil {
    private static final Logger logger = LoggerFactory.getLogger(Compilador.class);
    public static void showDialog(Exception e){
        logger.warn(ExceptionUtil.class.getName(), e);

        String msg = e.getMessage();

        if(!Compilador.LOG.equals("")){
            msg += "\n\n";
            msg += "LOG DE FEATURES COMPILADAS:\n";
            msg += Compilador.LOG;
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Erro ao executar a operação.");
        alert.setResizable(true);

        if(msg.contains("\n")){ //textão
            TextArea textArea = new TextArea(msg);
            textArea.setEditable(false);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(new Label("Descrição:"), 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
        }else{
            alert.setContentText(msg);
        }

        alert.showAndWait();
    }
}
