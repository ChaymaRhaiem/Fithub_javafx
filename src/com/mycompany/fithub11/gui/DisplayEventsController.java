package com.mycompany.fithub11.gui;

import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.fithub11.entities.Ticket;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleObjectProperty;

public class DisplayEventsController {

    public static Event selectedEvent;
    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, Integer> idColumn;

    @FXML
    private TableColumn<Event, String> nomEventColumn;

    @FXML
    private TableColumn<Event, String> descriptionColumn;

    @FXML
    private TableColumn<Event, String> locationColumn;

    @FXML
    private TableColumn<Event, String> typeColumn;

    @FXML
    private TableColumn<Event, Date> dateColumn;

    @FXML
    private TableColumn<Event, ImageView> imageColumn;

    @FXML
    private Button updateBtn;

    @FXML
    private TableColumn<Event, Void> deleteColumn;

    @FXML
    private Button deleteBtn;


    @FXML
    private TextField nomEventField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField locationField;

    @FXML
    private DatePicker dateField;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button btnGoBack;

    private File selectedImageFile;
    @FXML
    private ListView<Ticket> ticketListView;

    @FXML
    private final Connection connection = MyDB.getInstance().getConx();
    @FXML
    private TextField nomTicketField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField prixField;

    @FXML
    private TextField nombreMaxField;
    @FXML
    private CheckBox disponibiliteCheckbox;

    @FXML
    public void initialize() throws SQLException {


        // Initialize the columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomEventColumn.setCellValueFactory(new PropertyValueFactory<>("nomEvent"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        //imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        // Define the cell factory for the image column
        imageColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Event, ImageView>, ObservableValue<ImageView>>() {
            @Override
            public ObservableValue<ImageView> call(TableColumn.CellDataFeatures<Event, ImageView> param) {
                Event event = param.getValue();
                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                String imagePath = "C:\\Users\\Lenovo\\Documents\\Fithub\\public\\images\\events\\" + event.getImage();
                //C:\\Users\\Lenovo\\Documents\\Fithub\\public\\images\\events
                File file = new File(imagePath);
                if (file.exists()) {
                    try {
                        Image image = new Image(new FileInputStream(file));
                        imageView.setImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Image not found: " + imagePath);
                }
                return new SimpleObjectProperty<ImageView>(imageView);
            }

        });


        addButtonUpdateToTableEvent();
        addButtonAddTicketToTableEvent();
        addButtonDisplayTickets();

        URL resourceUrl = getClass().getResource("updateEvent.fxml");
        if (resourceUrl != null) {
            String resourcePath = resourceUrl.getPath();
            System.out.println("Location path: " + resourcePath);
        } else {
            System.out.println("Resource not found.");
        }

        // Retrieve the events from the database
        List<Event> events = ListEvent();

        // Populate the table with the events
        ObservableList<Event> eventList = FXCollections.observableArrayList(events);
        eventTable.setItems(eventList);

        // Get the selected event when a row is clicked
        eventTable.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    selectedEvent = row.getItem();
                }
            });
            return row;
        });
    }


    @FXML
    private void addButtonUpdateToTableEvent() {
        TableColumn<Event, Void> colBtn = new TableColumn("Update");

        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> cellFactory = new Callback<TableColumn<Event, Void>, TableCell<Event, Void>>() {

            @Override
            public TableCell<Event, Void> call(final TableColumn<Event, Void> param) {

                final TableCell<Event, Void> cell = new TableCell<Event, Void>() {
                    private final Button btn = new Button("Update");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            selectedEvent = getTableView().getItems().get(getIndex());
                            try {
                                Parent page1 = FXMLLoader.load(getClass().getResource("updateEvent.fxml"));


                                Scene scene = new Scene(page1);
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            } catch (IOException ex) {
                                Logger.getLogger(DisplayEventsController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        eventTable.getColumns().add(colBtn);
    }

    public List<Event> ListEvent() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Event event = new Event();
                event.setId(resultSet.getInt("id"));
                event.setNomEvent(resultSet.getString("nom_event"));
                event.setDescription(resultSet.getString("description"));
                event.setLocation(resultSet.getString("location"));
                event.setType(resultSet.getString("type"));
                event.setDate(resultSet.getDate("date"));
                event.setImage(resultSet.getString("image"));
                events.add(event);
            }
        }
        return events;
    }

    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        int selectedIndex = eventTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Event selectedEvent = eventTable.getItems().get(selectedIndex);
            boolean confirmDelete = confirmDelete(selectedEvent);
            if (confirmDelete) {
                // Delete the event from the database
                try {
                    String deleteQuery = "DELETE FROM event WHERE id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                    preparedStatement.setInt(1, selectedEvent.getId());
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Remove the event from the table
                eventTable.getItems().remove(selectedIndex);
            }
        } else {
            // Nothing selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Event Selected");
            alert.setContentText("Please select an event in the table.");
            alert.showAndWait();
        }
    }

    private boolean confirmDelete(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Event: " + event.getNomEvent());
        alert.setContentText("Are you sure you want to delete this event?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }


    @FXML
    private void handleAddEventButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Event.fxml"));
        Parent root = loader.load();
        EventController eventController = loader.getController();
        // Optionally, you can pass data to the eventController here
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        // Handle the save button action
        String nomEvent = nomEventField.getText();
        String type = typeField.getText();
        String description = descriptionField.getText();
        String location = locationField.getText();
        String date = null;

        // Check if all fields are filled in
        if (nomEvent.isEmpty() || type.isEmpty() || location.isEmpty() || dateField.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Form");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }
        else if (nomEvent.length() < 5 || type.length() < 5 || location.length() < 5) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Input values must be at least 5 characters long.");
            alert.showAndWait();
            return;
        }
        else {
            date = dateField.getValue().toString();
        }

        // Save the image to the images directory
        if (selectedImageFile != null) {
            try {
                File imagesDirectory = new File("C:\\Users\\Lenovo\\Documents\\Fithub\\public\\images\\events");
                if (!imagesDirectory.exists()) {
                    imagesDirectory.mkdir();
                }

                String imageName = selectedImageFile.getName();
                File destinationFile = new File(imagesDirectory.getAbsolutePath() + File.separator + imageName);

                Files.copy(selectedImageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Image Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while saving the event image.");
                alert.showAndWait();
                return;
            }
        }

        // Prepare the insert statement
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO event (nom_event, description, type, location, date, image) VALUES (?,?, ?, ?, ?, ?)")) {
            statement.setString(1, nomEvent);
            statement.setString(2, description);
            statement.setString(3, type);
            statement.setString(4, location);
            statement.setString(5, date);
            if (selectedImageFile != null) {
                statement.setString(6, selectedImageFile.getName());
            } else {
                statement.setString(6, "");
            }
            statement.executeUpdate();


            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Event Created");
            alert.setHeaderText(null);
            alert.setContentText("Event created successfully.");
            alert.showAndWait();
// Refresh the table view
            eventTable.getItems().clear();
            eventTable.getItems().addAll(ListEvent());
            eventTable.refresh();
            // Clear the form fields
            nomEventField.setText("");
            descriptionField.setText("");
            typeField.setText("");
            locationField.setText("");
            dateField.setValue(null);
            selectedImageFile = null;
            eventTable.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while saving the event to the database.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Image Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while saving the event image to the database.");
            alert.showAndWait();
        }

    }


    @FXML
    private void handleChooseImage(ActionEvent event) {
        // Handle the choose image button action
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Event Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        selectedImageFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
    }


    @FXML
    private void addButtonAddTicketToTableEvent() {
            TableColumn<Event, Void> colBtn = new TableColumn("Tickets");

            Callback<TableColumn<Event, Void>, TableCell<Event, Void>> cellFactory = new Callback<TableColumn<Event, Void>, TableCell<Event, Void>>() {

                @Override
                public TableCell<Event, Void> call(final TableColumn<Event, Void> param) {

                    final TableCell<Event, Void> cell = new TableCell<Event, Void>() {
                        private final Button btn = new Button("add ticket");

                        {
                            btn.setOnAction((ActionEvent event) -> {
                                selectedEvent = getTableView().getItems().get(getIndex());
                                try {
                                    Parent page1 = FXMLLoader.load(getClass().getResource("addTicket.fxml"));


                                    Scene scene = new Scene(page1);
                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.setScene(scene);
                                    stage.show();
                                } catch (IOException ex) {
                                    Logger.getLogger(DisplayEventsController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            };
            colBtn.setCellFactory(cellFactory);
            eventTable.getColumns().add(colBtn);
        }

    @FXML
    private void addButtonDisplayTickets() {
        TableColumn<Event, Void> colBtn = new TableColumn("Tickets");

        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> cellFactory = new Callback<TableColumn<Event, Void>, TableCell<Event, Void>>() {

            @Override
            public TableCell<Event, Void> call(final TableColumn<Event, Void> param) {

                final TableCell<Event, Void> cell = new TableCell<Event, Void>() {
                    private final Button btn = new Button("show ticket");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            selectedEvent = getTableView().getItems().get(getIndex());
                            try {
                                Parent page1 = FXMLLoader.load(getClass().getResource("Ticket.fxml"));
                                if (page1 == null) {
                                    System.out.println("Unable to load FXML file");
                                    return;
                                }
                                Scene scene = new Scene(page1);
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }


                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        eventTable.getColumns().add(colBtn);
    }

    private void showTickets(Event event) {
        try {
            // Query the database to get a list of tickets for the selected event
            String query = "SELECT * FROM ticket WHERE event_id = " + selectedEvent.getId();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Create a list of Ticket objects from the ResultSet
            List<Ticket> ticketList = new ArrayList<>();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setNom(rs.getString("nom"));

                ticket.setEmail(rs.getString("email"));
                ticket.setPrix(rs.getInt("prix"));
                ticket.setNombreMax(rs.getInt("nombre_max"));
                ticket.setDisponibilite(rs.getBoolean("disponibilite"));
                ticket.setEvent(event);
                ticketList.add(ticket);
            }

            // Use an ObservableList to display the list of tickets in the ListView
            ObservableList<Ticket> observableList = FXCollections.observableArrayList(ticketList);
            ticketListView.setItems(observableList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}