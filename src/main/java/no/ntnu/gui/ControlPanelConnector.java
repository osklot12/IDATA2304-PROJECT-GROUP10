package no.ntnu.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.client.ControlPanelClient;
import no.ntnu.tools.logger.SystemOutLogger;

import java.io.IOException;

/**
 * A graphical user interface (GUI) for connecting a control panel to a central server.
 */
public class ControlPanelConnector extends Application {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 400;
    private static ControlPanelClient client;
    private GridPane rootPane;
    private Label errorLog;

    /**
     * Creates a new ControlPanelConnector.
     */
    public ControlPanelConnector() {
        super();
        if (client == null) {
            throw new IllegalStateException("ControlPanel not initialized.");
        }
    }

    /**
     * Launches the application.
     *
     * @param controlPanel the control panel to connect
     */
    public static void start(ControlPanel controlPanel) {
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot launch app, because control panel is null.");
        }

        ControlPanelConnector.client = new ControlPanelClient(controlPanel);
        client.addLogger(new SystemOutLogger());
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Control Panel Connector");
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setResizable(false);

        rootPane = new GridPane();
        rootPane.getChildren().add(getConnectionForm());
        rootPane.getStyleClass().add("root-pane");

        Scene scene = new Scene(rootPane);
        String css = this.getClass().getResource("/css/connector.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a new form for connecting.
     *
     * @return the connection form
     */
    private VBox getConnectionForm() {
        VBox form = new VBox(15);

        TextField addressField = getAddressField();
        Button connectButton = getConnectButton(addressField);
        errorLog = getErrorLog();

        form.getChildren().addAll(addressField, connectButton, errorLog);
        form.getStyleClass().add("connector-form");

        return form;
    }

    /**
     * Creates a new text field for writing addresses.
     *
     * @return the address field
     */
    private static TextField getAddressField() {
        TextField addressField = new TextField();

        addressField.setPromptText("Enter server address");
        addressField.getStyleClass().add("text-field");

        return addressField;
    }

    /**
     * Creates a new connect button.
     *
     * @param addressField the text field to read address from
     * @return the connect button
     */
    private Button getConnectButton(TextField addressField) {
        Button connectButton = new Button("Connect");

        connectButton.getStyleClass().add("button");
        connectButton.setOnAction(event -> {
            String serverAddress = addressField.getText();
            if (connectToServer(serverAddress)) {
                runControlPanelGui();
                closeConnector();
            }
        });

        return connectButton;
    }

    /**
     * Creates a new label for error logging.
     *
     * @return the error log label
     */
    private Label getErrorLog() {
        Label result = new Label("");

        result.getStyleClass().add("error-log");
        result.setWrapText(true);

        return result;
    }

    /**
     * Tries to connect to the server.
     *
     * @param ipAddress the ip address of the server
     * @return true if successfully connected, false otherwise
     */
    private boolean connectToServer(String ipAddress) {
        boolean success = false;

        try {
            client.connect(ipAddress);
            success = true;
        } catch (IOException e) {
            errorLog.setText(e.getMessage());
        }

        return success;
    }

    /**
     * Runs the GUI for the control panel.
     */
    private static void runControlPanelGui() {
        Platform.runLater(() -> {
            Stage mainStage = new Stage();
            ControlPanel controlPanel = client.getControlPanel();
            ControlPanelGui mainGui = new ControlPanelGui(controlPanel);
            controlPanel.addListener(mainGui);
            mainGui.start(mainStage);
        });
    }

    /**
     * Closes the connector.
     */
    private void closeConnector() {
        Stage currentStage = (Stage) rootPane.getScene().getWindow();
        currentStage.close();
    }
}
