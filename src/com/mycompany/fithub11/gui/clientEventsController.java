package com.mycompany.fithub11.gui;

import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;
import java.io.BufferedReader;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BackgroundPosition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.time.Period;

public class clientEventsController {


    public static Event selectedEvent;
    
    @FXML
private TilePane eventPane;
    
    @FXML
private TextField searchField;

 
   

    @FXML
    private final Connection connection = MyDB.getInstance().getConx();


    @FXML
    public void initialize() throws SQLException {

    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        searchEvent(newValue);
    });
     ListEvent();

    }

private void searchEvent(String query) {
    List<Event> events = new ArrayList<>();
String sql = "SELECT * FROM event WHERE nom_event LIKE '%" + query + "%' OR location LIKE '%" + query + "%' OR type LIKE '%"+" %' OR date LIKE '%" + query + "%'";
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
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Clear the event pane
    eventPane.getChildren().clear();

    // Add the filtered events to the event pane
    for (Event event : events) {
        VBox eventCard = createEventCard(event);
        eventPane.getChildren().add(eventCard);
    }
}
public void ListEvent() throws SQLException {
    searchEvent("");
}




/*
   public void ListEvent() throws SQLException {
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

            VBox eventCard = createEventCard(event);
            eventPane.getChildren().add(eventCard);
        }
    }
   }
  */  
  

   
    public String getWeatherForecast(Event event) {
    try {
        // Construct the API URL using the event's location and date
        String apiKey = "00d19db4d6bed88c4065ae6372d90aac";
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast";
        //String city = event.getLocation();
        String city= "Tunisia";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(event.getDate());
        String url = apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric";

        // Send the API request and parse the response
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject jsonObject = new JSONObject(response.toString());
        JSONArray jsonArray = jsonObject.getJSONArray("list");

        // Find the weather forecast for the event's date
        JSONObject weatherObject = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject forecast = jsonArray.getJSONObject(i);
            String forecastDate = forecast.getString("dt_txt").split(" ")[0];
            if (forecastDate.equals(date)) {
                weatherObject = forecast.getJSONArray("weather").getJSONObject(0);
                break;
            }
        }

        // Construct and return the weather forecast string
        if (weatherObject != null) {
            String description = weatherObject.getString("description");
            double temperature = jsonArray.getJSONObject(0).getJSONObject("main").getDouble("temp");
            return "Weather forecast for " + event.getDate() + ": " + description + ", " + temperature + "°C";
        } else {
            return "Weather forecast not found for " + event.getDate();
        }
    } catch (Exception e) {
        return "Error fetching weather forecast: " + e.getMessage();
    }
}
    
    private VBox createEventCard(Event event) {
    VBox card = new VBox();
    card.setSpacing(20);
    card.setAlignment(Pos.TOP_CENTER);

    ImageView imageView = new ImageView();
    imageView.setFitWidth(200);
    imageView.setFitHeight(200);
    String imagePath = "C:/Users/Lenovo/Documents/Fithub/public/images/events/" + event.getImage();
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
    card.getChildren().add(imageView);

    Text title = new Text(event.getNomEvent());
    card.getChildren().add(title);

    Text description = new Text(event.getDescription());
    card.getChildren().add(description);

    Text location = new Text("Location: " + event.getLocation());
    card.getChildren().add(location);

    Text type = new Text("Type: " + event.getType());
    card.getChildren().add(type);

    Text date = new Text("Date: " + event.getDate().toString());
    card.getChildren().add(date);
    // Add the weather forecast to the card
    Text weatherForecast = new Text(getWeatherForecast(event));
    card.getChildren().add(weatherForecast);

        // Convert java.sql.Date to java.time.LocalDate
        LocalDate eventDateLocal = event.getDate().toLocalDate();

        // Get the current date
        LocalDate currentDateLocal = LocalDate.now();

        // Calculate the difference in days using java.time.Period
        Period periodLocal = Period.between(currentDateLocal, eventDateLocal);
        long daysUntilEventLocal = periodLocal.getDays();

        // Add the days until the event to the card
        Text daysUntilEventText = new Text("Days until event: " + daysUntilEventLocal);
        card.getChildren().add(daysUntilEventText);


    
    // Add an image under the weather forecast based on the forecast text
    ImageView weatherImage = new ImageView();
    String weatherImageFilename = "";
    if (weatherForecast.getText().toLowerCase().contains("clouds")) {
     weatherImageFilename = "clouds.jpg"; // Default image}
    }
    else if (weatherForecast.getText().toLowerCase().contains("clear sky")) {
        weatherImageFilename = "clearsky.jpg";
    } else if (weatherForecast.getText().toLowerCase().contains("rain")) {
        weatherImageFilename = "rain.jpg";
    }
    String weatherImagePath = "C:\\Users\\Lenovo\\Documents\\NetBeansProjects\\fithub00\\src\\com\\mycompany\\fithub11\\images/" + weatherImageFilename;
    File weatherImageFile = new File(weatherImagePath);
    if (weatherImageFile.exists()) {
        try {
            Image weatherImageFileImage = new Image(new FileInputStream(weatherImageFile));
            weatherImage.setImage(weatherImageFileImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    } else {
        System.out.println("Weather image not found: " + weatherImagePath);
    }
    weatherImage.setFitWidth(300);
    weatherImage.setFitHeight(100);
    card.getChildren().add(weatherImage);
    /*
    Button btn = new Button("Add Ticket");
    btn.setUserData(event);
    btn.setUserData(event);
btn.setOnAction((ActionEvent e) -> {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservation.fxml"));
        Parent root = loader.load();
        ReservationController controller = loader.getController();
        controller.setSelectedEvent((Event)btn.getUserData());
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
});
    card.getChildren().add(btn);
*/
Button btn_det = new Button("Details");
btn_det.setUserData(event);
btn_det.setOnAction((ActionEvent e) -> {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EventDetails.fxml"));
        Parent root = loader.load();
        EventDetailsController controller = loader.getController();
        controller.setSelectedEvent((Event)btn_det.getUserData());
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
});

    
    card.getChildren().add(btn_det);
    
    
    return card;
}



}