package br.com.pirilampo.gui.util;


import br.com.pirilampo.core.exception.FeatureException;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class ExceptionUtil {
    private ExceptionUtil(){}

    public static void showDialog(Throwable e){
        log.error(log.getName(), e);

        String titulo = e.getMessage();
        String log = null;

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setResizable(true);

        if(e instanceof FeatureException){
            titulo = "Erro ao executar a operação.";
            log = e.getMessage();
            alert.setContentText("FEATURE: " + ((FeatureException) e).getFeature().getAbsolutePath());
        }else{
            if(e.getStackTrace().length > 0) {
                StringBuilder trace = new StringBuilder();

                for (StackTraceElement item : e.getStackTrace()) {
                    trace.append(item.toString()).append("\n");
                }

                log = trace.toString();
            }
        }

        alert.setHeaderText(titulo);

        if(StringUtils.isNotEmpty(log)){
            TextArea textArea = new TextArea(log);
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
        }

        alert.showAndWait();
    }
}
