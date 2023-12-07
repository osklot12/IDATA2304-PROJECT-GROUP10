package no.ntnu.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.controlpanel.virtual.actuator.VActuatorListener;
import no.ntnu.controlpanel.virtual.actuator.VirtualStandardActuator;
import no.ntnu.controlpanel.virtual.sensor.VirtualSDUSensor;
import no.ntnu.controlpanel.virtual.sensor.VirtualSDUSensorListener;
import no.ntnu.fieldnode.device.DeviceClass;

import java.util.*;

/**
 * A graphical representation of a field node using JavaFX.
 */
public class FieldNodeView extends VBox implements VirtualSDUSensorListener {
    private static final Map<DeviceClass, String> SENSOR_DOMAINS = DeviceClassMapGenerator.getSensorDomainMap();
    private static final Map<DeviceClass, String> SENSOR_UNITS = DeviceClassMapGenerator.getSduSensorUnitMap();
    private static final Map<DeviceClass, String> ACTUATOR_DEVICE = DeviceClassMapGenerator.getActuatorDeviceMap();
    private final VirtualFieldNode fieldNode;
    private final Map<Integer, SduSensorDisplay> sduSensors;
    private final Map<Integer, StandardActuatorDisplay> actuators;
    private ViewRemoval viewRemoval;

    private record ViewRemoval(int address, FieldNodeViewRemover remover) {
    }

    /**
     * A display for SDU sensors.
     */
    private static class SduSensorDisplay extends VBox {
        private static final String DEFAULT_LABEL = "Unknown sensor";
        private final VirtualSDUSensor sensor;
        private final Label sduData;
        private final String dataUnit;

        private SduSensorDisplay(VirtualSDUSensor sensor) {
            if (sensor == null) {
                throw new IllegalArgumentException("Cannot create SduSensorDisplay, because sensor is null.");
            }

            this.sensor = sensor;
            DeviceClass deviceClass = sensor.getDeviceClass();
            this.dataUnit = Objects.requireNonNullElse(SENSOR_UNITS.get(deviceClass), "");
            getChildren().add(new Label(Objects.requireNonNullElse(SENSOR_DOMAINS.get(deviceClass), DEFAULT_LABEL)));
            this.sduData = new Label();
            sduData.getStyleClass().add("sdu-data-display");
            getChildren().add(sduData);
            setSpacing(10);
        }

        private void setSDuData() {
            Double data = sensor.pollNextAvailableData();
            if (data != null) {
                Platform.runLater(() -> sduData.setText(data + " " + dataUnit));
            }
        }
    }

    /**
     * A display for standard actuators.
     */
    private static class StandardActuatorDisplay extends VBox implements VActuatorListener {
        private static final String DEFAULT_NAME = "Unknown actuator";
        private final VirtualStandardActuator actuator;
        private final ActuatorOptionList options;
        private final ChoiceBox<ActuatorOption> optionBox;

        private record ActuatorOption(String name, int state) {
            private ActuatorOption {
                if (name == null) {
                    throw new IllegalArgumentException("Cannot create ActuatorOption, because name is null.");
                }

            }

            @Override
            public String toString() {
                return name;
            }
        }

        private static class ActuatorOptionList extends ArrayList<ActuatorOption> {
            private ActuatorOption getOptionForState(int state) {
                ActuatorOption result = null;

                Iterator<ActuatorOption> it = iterator();
                while (result == null && it.hasNext()) {
                    ActuatorOption currentOption = it.next();
                    if (state == currentOption.state()) {
                        result = currentOption;
                    }
                }

                return result;
            }
        }

        private StandardActuatorDisplay(VirtualStandardActuator actuator) {
            if (actuator == null) {
                throw new IllegalArgumentException("Cannot create StandardActuatorDisplay, because actuator is null.");
            }

            this.actuator = actuator;
            actuator.addListener(this);
            DeviceClass deviceClass = actuator.getDeviceClass();
            this.options = initializeOptions(deviceClass);
            this.optionBox = initializeComboBox();
            updateOption();
            setOptionBoxListener();
            getChildren().add(new Label(Objects.requireNonNullElse(ACTUATOR_DEVICE.get(deviceClass), DEFAULT_NAME)));
            getChildren().add(optionBox);
            setSpacing(10);
        }

        private ActuatorOptionList initializeOptions(DeviceClass deviceClass) {
            ActuatorOptionList result = new ActuatorOptionList();

            Map<String, Integer> optionsMap = DeviceClassMapGenerator.getActuatorOptions().get(deviceClass);
            if (optionsMap != null) {
                optionsMap.forEach((name, state) -> result.add(new ActuatorOption(name, state)));
            }

            return result;
        }

        private ChoiceBox<ActuatorOption> initializeComboBox() {
            ChoiceBox<ActuatorOption> choiceBox = new ChoiceBox<>();
            choiceBox.getItems().setAll(options);

            return choiceBox;
        }

        private void setOptionBoxListener() {
            optionBox.setOnAction(e -> {
                ActuatorOption selectedOption = optionBox.getValue();
                if (selectedOption != null) {
                    int state = selectedOption.state();
                    actuator.setState(state, this);
                }
            });
        }

        private void updateOption() {
            optionBox.setValue(options.getOptionForState(actuator.getState()));
        }

        @Override
        public void virtualActuatorStateChanged() {
            Platform.runLater(this::updateOption);
        }

        @Override
        public Object getVActuatorEventDestination() {
            return this;
        }
    }

    /**
     * Creates a new FieldNodeView.
     *
     * @param fieldNode the virtual field node to represent
     */
    public FieldNodeView(VirtualFieldNode fieldNode) {
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeView, because fieldNode is null");
        }

        this.fieldNode = fieldNode;
        this.sduSensors = new HashMap<>();
        this.actuators = new HashMap<>();
        initializeDevices();

        setSpacing(10);
        getChildren().addAll(getViewHeader(), getDeviceNavigator(getSensorGrid()), getDeviceNavigator(getActuatorGrid()));
        getStyleClass().add("field-node-view");
    }

    /**
     * Sets the remover of the field node view.
     *
     * @param address the address the remover associates with the view
     * @param remover the remover able to remove the field node view
     */
    public void setRemover(int address, FieldNodeViewRemover remover) {
        if (remover == null) {
            throw new IllegalArgumentException("Cannot set remover, because remover is null.");
        }

        this.viewRemoval = new ViewRemoval(address, remover);
    }

    private ScrollPane getDeviceNavigator(GridPane deviceGrid) {
        ScrollPane deviceNavigator = new ScrollPane(deviceGrid);
        deviceNavigator.setFitToWidth(true);
        deviceNavigator.getStyleClass().add("device-navigator");

        return deviceNavigator;
    }

    private GridPane getSensorGrid() {
        GridPane sensorGrid = new GridPane();
        sensorGrid.setAlignment(Pos.CENTER);
        sensorGrid.setHgap(10);
        sensorGrid.setVgap(10);

        int row = 0;
        int col = 0;
        for (SduSensorDisplay sensor : sduSensors.values()) {
            sensor.getStyleClass().add("display-grid-cell");
            sensorGrid.add(sensor, col, row);

            // Increment column. If it reaches 2, reset it to 0 and move to the next row.
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }

        return sensorGrid;
    }

    private GridPane getActuatorGrid() {
        GridPane actuatorGrid = new GridPane();
        actuatorGrid.setAlignment(Pos.CENTER);
        actuatorGrid.setHgap(10);
        actuatorGrid.setVgap(10);

        int row = 0;
        int col = 0;
        for (StandardActuatorDisplay actuator : actuators.values()) {
            actuator.getStyleClass().add("display-grid-cell");
            actuatorGrid.add(actuator, col, row);

            // Increment column. If it reaches 2, reset it to 0 and move to the next row.
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }

        return actuatorGrid;
    }


    private HBox getViewHeader() {
        HBox viewHeader = new HBox(getFieldNodeNameLabel(), getSpacer(), getRemoveButton());
        viewHeader.getStyleClass().add("field-node-view-header");

        return viewHeader;
    }

    private Button getRemoveButton() {
        Button removeButton = new Button();
        removeButton.setText("Remove");
        removeButton.getStyleClass().add("standard-button");

        removeButton.setOnAction(e -> {
            if (viewRemoval != null) {
                int address = viewRemoval.address();
                FieldNodeViewRemover remover = viewRemoval.remover();

                remover.removeFieldNodeView(address);
            }
        });

        return removeButton;
    }

    private Label getFieldNodeNameLabel() {
        Label nameLabel = new Label(fieldNode.getName());
        nameLabel.getStyleClass().add("field-node-name-label");

        return nameLabel;
    }

    private Pane getSpacer() {
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return spacer;
    }

    private void initializeDevices() {
        fieldNode.getVirtualSDUSensors().forEach((address, sensor) -> {
            sduSensors.put(address, new SduSensorDisplay(sensor));
            sensor.addListener(address, this);
        });

        fieldNode.getVirtualStandardActuators().forEach((address, actuator) -> {
            actuators.put(address, new StandardActuatorDisplay(actuator));
        });
    }

    @Override
    public void newSduData(int sensorAddress) {
        SduSensorDisplay display = sduSensors.get(sensorAddress);
        if (display != null) {
            display.setSDuData();
        }
    }
}
