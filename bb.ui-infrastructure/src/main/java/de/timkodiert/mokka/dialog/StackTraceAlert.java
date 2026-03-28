package de.timkodiert.mokka.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StackTraceAlert extends Alert {

    private static final Logger LOG = LoggerFactory.getLogger(StackTraceAlert.class.getName());

    private final Throwable throwable;

    private StackTraceAlert(String description, Throwable throwable) {
        super(AlertType.ERROR, description);
        this.throwable = throwable;
        init();
    }

    public static StackTraceAlert create(String description, Throwable exception) {
        return new StackTraceAlert(description, exception);
    }

    public static StackTraceAlert createAndLog(String description, Throwable exception) {
        LOG.error(description, exception);
        return new StackTraceAlert(description, exception);
    }

    private void init() {
        TextArea stackTraceArea = new TextArea(getStackTrace(throwable));
        stackTraceArea.setEditable(false);
        stackTraceArea.setWrapText(false);
        stackTraceArea.setMaxWidth(Double.MAX_VALUE);
        stackTraceArea.setMaxHeight(Double.MAX_VALUE);
        Label label = new Label("Stacktrace:");
        GridPane content = new GridPane();
        content.addColumn(0, label, stackTraceArea);
        GridPane.setVgrow(stackTraceArea, Priority.ALWAYS);
        GridPane.setHgrow(stackTraceArea, Priority.ALWAYS);
        getDialogPane().setExpandableContent(content);
    }

    private static String getStackTrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
