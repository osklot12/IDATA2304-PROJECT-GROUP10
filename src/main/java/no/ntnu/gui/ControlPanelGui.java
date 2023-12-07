package no.ntnu.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.controlpanel.ControlPanelListener;
import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.controlpanel.virtual.VirtualFieldNodeListener;
import no.ntnu.network.representation.FieldNodeAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * GUI for a {@code ControlPanel}.
 */
public class ControlPanelGui extends Application implements ControlPanelListener, FieldNodeViewRemover {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private final ControlPanel controlPanel;
    private final Map<Integer, FieldNodeView> fieldNodeViews;
    private GridPane fieldNodeViewGrid;
    private ListView<HBox> fieldNodePoolList;


    /**
     * Creates a new ControlPanelGui.
     *
     * @param controlPanel the control panel
     */
    public ControlPanelGui(ControlPanel controlPanel) {
        super();
        if (controlPanel == null) {
            throw new IllegalStateException("ControlPanel not initialized.");
        }

        this.controlPanel = controlPanel;
        this.fieldNodeViews = new HashMap<>();
    }

    @Override
    public void start(Stage stage) {
        VBox rootPane = initializeRootPane();

        Scene scene = initializeScene(rootPane);

        configureStage(stage);
        stage.setScene(scene);
        stage.show();

        sendFieldNodePoolRequest();
    }

    /**
     * Initializes the root pane of the application.
     *
     * @return the root pane
     */
    private VBox initializeRootPane() {
        VBox rootPane = new VBox();

        Node fieldNodeManager = initializeFieldNodeManager();
        rootPane.getChildren().addAll(fieldNodeManager);

        return rootPane;
    }

    /**
     * Initializes the scene of the application.
     *
     * @param rootPane the root pane to add to the scene
     * @return the initialized scene
     */
    private Scene initializeScene(VBox rootPane) {
        Scene scene = new Scene(rootPane);

        String css = this.getClass().getResource("/css/controlpanel.css").toExternalForm();
        scene.getStylesheets().add(css);

        return scene;
    }

    /**
     * Configures the stage of the application.
     *
     * @param stage the stage to configure
     */
    private void configureStage(Stage stage) {
        stage.setTitle("Control Panel - " + controlPanel.getFieldNodeSourceAsString());
        stage.setResizable(false);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
    }

    /**
     * Initializes the field node manager.
     *
     * @return the field node manager
     */
    private Node initializeFieldNodeManager() {
        HBox fieldNodeManager = new HBox();

        fieldNodeManager.getChildren().addAll(getFieldNodeViewSection(), initializeFieldNodePoolSection());
        HBox.setHgrow(fieldNodeManager, Priority.ALWAYS);
        VBox.setVgrow(fieldNodeManager, Priority.ALWAYS);

        return fieldNodeManager;
    }

    /**
     * Initializes the field node pool section.
     *
     * @return the field node pool section
     */
    private VBox initializeFieldNodePoolSection() {
        VBox fieldNodePoolSection = new VBox();

        fieldNodePoolSection.getStyleClass().add("field-node-pool-section");
        fieldNodePoolList = getFieldNodePoolList();
        fieldNodePoolSection.getChildren().addAll(getFieldNodePoolHeader(), fieldNodePoolList);

        return fieldNodePoolSection;
    }

    /**
     * Returns a new field node pool list.
     *
     * @return the field node pool list
     */
    private static ListView<HBox> getFieldNodePoolList() {
        ListView<HBox> fieldNodeList = new ListView<>();

        fieldNodeList.getStyleClass().add("field-node-list");
        VBox.setVgrow(fieldNodeList, Priority.ALWAYS);

        return fieldNodeList;
    }

    /**
     * Returns a new field node pool header.
     *
     * @return the field node pool header
     */
    private HBox getFieldNodePoolHeader() {
        HBox fieldNodePoolHeader = new HBox(10, getPoolLabel(), getRefreshButton());

        fieldNodePoolHeader.getStyleClass().add("field-node-pool-header");

        return fieldNodePoolHeader;
    }

    /**
     * Returns a new field node pool label.
     *
     * @return the field node pool label
     */
    private Label getPoolLabel() {
        Label poolLabel = new Label("Available field nodes");
        poolLabel.getStyleClass().add("pool-label");

        return poolLabel;
    }

    /**
     * Returns a new refresh button.
     *
     * @return the refresh button
     */
    private Button getRefreshButton() {
        Button refreshButton = new Button("Refresh");

        refreshButton.getStyleClass().add("standard-button");
        refreshButton.setOnAction(e -> sendFieldNodePoolRequest());

        return refreshButton;
    }

    /**
     * Returns a new field node pool box for a given field node.
     *
     * @param address the address of the field node
     * @param name the name of the field node
     * @return the field node pool box
     */
    private HBox createFieldNodePoolBox(int address, String name) {
        HBox fieldNodePoolBox = new HBox(10, getPoolBoxLabel(name), getSpacer(), getPoolBoxAddButton(address));

        fieldNodePoolBox.getStyleClass().add("field-node-pool-box");

        return fieldNodePoolBox;
    }

    /**
     * Returns a new label for a field node pool box.
     *
     * @param name the name of the field node
     * @return the field node pool box label
     */
    private static Label getPoolBoxLabel(String name) {
        Label label = new Label(name);

        HBox.setHgrow(label, Priority.ALWAYS);
        label.getStyleClass().add("field-node-pool-box-label");

        return label;
    }

    /**
     * Returns a new add button for a field node pool box.
     * The button tries to add the field node to the main view once clicked.
     *
     * @param address the address of the field node
     * @return the field node pool box add button
     */
    private Button getPoolBoxAddButton(int address) {
        Button addButton = new Button("Add");

        addButton.setOnAction(e -> requestAddingFieldNode(address));
        addButton.getStyleClass().add("standard-button");

        return addButton;
    }

    /**
     * Returns a new spacer, used for creating space between elements.
     *
     * @return the spacer
     */
    private static Pane getSpacer() {
        Pane spacer = new Pane();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        return spacer;
    }

    /**
     * Returns a new field node view section.
     *
     * @return the field node view section
     */
    private ScrollPane getFieldNodeViewSection() {
        ScrollPane gridScrollPane = new ScrollPane();

        gridScrollPane.getStyleClass().add("field-node-view-section-scroll-pane");
        fieldNodeViewGrid = getFieldNodeViewGrid();
        gridScrollPane.setContent(fieldNodeViewGrid);
        gridScrollPane.setFitToWidth(true);
        HBox.setHgrow(gridScrollPane, Priority.ALWAYS);

        return gridScrollPane;
    }

    /**
     * Returns a new field node view grid.
     *
     * @return the field node view grid
     */
    private GridPane getFieldNodeViewGrid() {
        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        grid.getStyleClass().add("field-node-view-grid");

        return grid;
    }

    /**
     * Renders all available field nodes into the field node pool list.
     */
    private void renderFieldNodePoolList() {
        Platform.runLater(() -> {
            fieldNodePoolList.getItems().clear();

            for (Map.Entry<Integer, String> entry : controlPanel.getFieldNodePool().entrySet()) {
                // only adding it to the field node pool if it is not already added to the control panel
                if (controlPanel.getVirtualFieldNode(entry.getKey()) == null) {
                    HBox fieldNodeBox = createFieldNodePoolBox(entry.getKey(), entry.getValue());
                    fieldNodePoolList.getItems().add(fieldNodeBox);
                }
            }
        });
    }

    /**
     * Renders all field nodes subscribed to into the field node view grid.
     */
    private void renderFieldNodeGrid() {
        Platform.runLater(() -> {
            fieldNodeViewGrid.getChildren().clear(); // Clear existing views
            int row = 0;
            int col = 0;

            for (FieldNodeView fieldNodeView : fieldNodeViews.values()) {
                fieldNodeViewGrid.add(fieldNodeView, col, row);

                // Increment column. If it reaches 2, reset it to 0 and move to the next row.
                col++;
                if (col >= 2) {
                    col = 0;
                    row++;
                }
            }
        });

    }

    /**
     * Renders the whole field node manager.
     */
    private void renderManager() {
        renderFieldNodeGrid();
        renderFieldNodePoolList();
    }

    /**
     * Sends a request for the field node pool.
     */
    private void sendFieldNodePoolRequest() {
        getFieldNodeAgent().requestFieldNodePool();
    }

    /**
     * Requests to add an available field node to the main view.
     *
     * @param fieldNodeAddress the address of the field node to add
     */
    private void requestAddingFieldNode(int fieldNodeAddress) {
        getFieldNodeAgent().subscribeToFieldNode(fieldNodeAddress);
    }

    @Override
    public void fieldNodePoolReceived(ControlPanel controlPanel) {
        renderFieldNodePoolList();
    }

    @Override
    public void fieldNodeAdded(int fieldNodeAddress) {
        VirtualFieldNode fieldNode = controlPanel.getVirtualFieldNode(fieldNodeAddress);
        if (fieldNode != null) {
            addFieldNodeView(fieldNodeAddress, fieldNode);
        }
    }

    /**
     * Adds a field node to the main view.
     *
     * @param fieldNodeAddress the address of the field node
     * @param fieldNode the virtual field node to add
     */
    private void addFieldNodeView(int fieldNodeAddress, VirtualFieldNode fieldNode) {
        FieldNodeView fieldNodeView = new FieldNodeView(fieldNode);
        fieldNodeViews.put(fieldNodeAddress, fieldNodeView);
        fieldNodeView.setRemover(fieldNodeAddress, this);
        renderManager();
    }

    @Override
    public void fieldNodeRemoved(int fieldNodeAddress) {
        fieldNodeViews.remove(fieldNodeAddress);
        renderManager();
    }

    @Override
    public void removeFieldNodeView(int fieldNodeAddress) {
        getFieldNodeAgent().unsubscribeFromFieldNode(fieldNodeAddress);
    }

    /**
     * Returns the field node agent.
     *
     * @return the field node agent
     */
    private FieldNodeAgent getFieldNodeAgent() {
        return controlPanel.getFieldNodeAgent();
    }
}
