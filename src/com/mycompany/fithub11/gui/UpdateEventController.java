package com.mycompany.fithub11.gui;

import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.fithub11.services.EventService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static com.mycompany.fithub11.Utils.Consts.IMG_PATH_LOAD;

public class UpdateEventController {

    @FXML
    private TextField nomEventTextField;

    @FXML
    private Label idLabel;

    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;

    @FXML
    private TextField typeTextField;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private Label imageLabel;
    private File selectedImageFile;

    private String folderPath = "C:\\Users\\Lenovo\\Documents\\Fithub\\public\\images\\events";
    @FXML
    private ImageView imageView;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button updateEventButton;
    @FXML
    private Button btnGoBack;

    private Event selectedEvent;
    EventService se = new EventService () ;

    private Connection connection = MyDB.getInstance().getConx();
    private String imagePath;




    @FXML
    public void initialize() {
        selectedEvent = DisplayEventsController.selectedEvent;
        System.out.println(selectedEvent);

        //System.out.println (ManageEventController.eventRecup.getPhotoPost()) ;
        nomEventTextField.setText(DisplayEventsController.selectedEvent.getNomEvent());
        descriptionTextField.setText(DisplayEventsController.selectedEvent.getDescription());
        //Display Post photo
        //Image image = new Image((IMG_PATH_LOAD + DisplayEventsController.selectedEvent.getImage()));
        //imageView.setImage(image);

        dateDatePicker.setValue(DisplayEventsController.selectedEvent.getDate().toLocalDate());
        locationTextField.setText(DisplayEventsController.selectedEvent.getLocation());
        typeTextField.setText(DisplayEventsController.selectedEvent.getType());


        /*btnGoBack.setOnAction(event -> {
            try {
                Parent page1 = FXMLLoader.load(getClass().getResource("/com/mycompany/fithub11/DisplayEvents.fxml"));
                Scene scene = new Scene(page1);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(UpdateEventController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });*/
    }





    @FXML
    private void handleChooseImage(ActionEvent event) {
        // Handle the choose image button action
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Event Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        selectedImageFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (selectedImageFile != null) {
            imagePath = selectedImageFile.getName();
            Image image = new Image(selectedImageFile.toURI().toString());
            imageView.setImage(image);
            if (imageLabel != null) {
                imageLabel.setText(selectedImageFile.getName());
            }
        }
    }







    @FXML
    private void handleUpdateEventButtonClick() throws IOException, SQLException {
        if (nomEventTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty() || locationTextField.getText().isEmpty() || typeTextField.getText().isEmpty() || dateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        // Update the event details in the database
        String sql = "UPDATE event SET nom_event=?, description=?, location=?, type=?, date=?, image=? WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomEventTextField.getText());
            statement.setString(2, descriptionTextField.getText());
            statement.setString(3, locationTextField.getText());
            statement.setString(4, typeTextField.getText());
            statement.setDate(5, java.sql.Date.valueOf(dateDatePicker.getValue()));

            // Check if a new image has been selected
            if (selectedImageFile != null) {
                String fileName = selectedImageFile.getName();
                imagePath = fileName;
                // Copy the image file to the images directory
                Path sourcePath = Paths.get(selectedImageFile.getPath());
                Path targetPath = Paths.get(folderPath, fileName);
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // If a new image hasn't been selected, use the path to the previously selected image (if there is one)
                if (selectedEvent.getImage() != null) {
                    imagePath = selectedEvent.getImage();
                }
            }

            statement.setString(6, imagePath);
            statement.setInt(7, selectedEvent.getId());
            statement.executeUpdate();
        }
        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DisplayEvents.fxml"));
        Parent root = loader.load();
        // Set the new scene
        Scene scene = new Scene(root);
        Stage stage = (Stage) updateEventButton.getScene().getWindow();
        stage.setScene(scene);
    }



    public Event getSelectedEvent() {
        return selectedEvent;
    }
}
