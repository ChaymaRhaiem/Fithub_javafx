package com.mycompany.fithub11.gui;

import com.mycompany.fithub11.Utils.MyDB;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EventController implements Initializable {

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

    private final Connection connection = MyDB.getInstance().getConx();

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        btnGoBack.setOnAction(event -> {
            try {
                Parent page1 = FXMLLoader.load(getClass().getResource("DisplayEvents.fxml"));
                Scene scene = new Scene(page1);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } catch (IOException ex) {
                Logger.getLogger(UpdateEventController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
    private void copyPhoto(File selectedImageFile) {
        // Define the directory path
        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource("C:\\Users\\Lenovo\\Documents\\Fithub\\public\\images\\events");
        String directoryPath = resourceUrl.getPath();
        File directory = new File(directoryPath);

        // Check if the directory exists, create it if not
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the image to the directory
        String imageName = selectedImageFile.getName();
        File imageFile = new File(directoryPath + imageName);
        try {
            Files.copy(selectedImageFile.toPath(), imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Image Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while saving the event image.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        // Handle the save button action
        String nomEvent = nomEventField.getText();
        String type = typeField.getText();
        String description = descriptionField.getText();
        String location = locationField.getText();
        String date = null;

        if (nomEvent.isEmpty() || type.isEmpty() || location.isEmpty() || dateField.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Form");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        } else {
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
                statement.setString(6,  selectedImageFile.getName());
            } else {
                statement.setString(6, "");
            }
            statement.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Event Created");
            alert.setHeaderText(null);
            alert.setContentText("Event created successfully.");
            alert.showAndWait();

            // Clear the form fields
            nomEventField.setText("");
            descriptionField.setText("");
            typeField.setText("");
            locationField.setText("");
            dateField.setValue(null);
            selectedImageFile = null;
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

}



