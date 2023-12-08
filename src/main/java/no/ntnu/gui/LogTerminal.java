package no.ntnu.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import no.ntnu.tools.logger.SimpleLogger;

/**
 * A JavaFX terminal that can log events.
 */
public class LogTerminal extends Application implements SimpleLogger {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 400;
    private static final int MAX_CAPACITY = 100; // Maximum number of log messages
    private final String name;
    private VBox logContainer;

    public LogTerminal(String name) {
        super();
        if (name == null) {
            throw new IllegalStateException("Name not set.");
        }

        this.name = name;
    }

    @Override
    public void start(Stage stage) {
        logContainer = new VBox();
        logContainer.setSpacing(5);

        ScrollPane scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 400, 300);

        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setResizable(false);
        stage.setTitle(name);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void logInfo(String message) {
        Platform.runLater(() -> addLogMessage(message, Color.WHITE));
    }

    @Override
    public void logError(String message) {
        Platform.runLater(() -> addLogMessage(message, Color.RED));
    }

    /**
     * Adds a log message to the terminal.
     *
     * @param message the message to add
     * @param color the color of the message
     */
    private void addLogMessage(String message, Color color) {
        if (logContainer.getChildren().size() >= MAX_CAPACITY) {
            logContainer.getChildren().remove(0);
        }

        Text text = new Text(message);
        text.setFill(color);
        logContainer.getChildren().add(text);
    }
}