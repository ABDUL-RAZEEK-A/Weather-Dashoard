import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class WeatherApp {

    private static final String API_KEY = "036939265f38b7943c9d6acc5a5db0b0";

    public static void main(String[] args) {

        // Frame
        JFrame frame = new JFrame("Weather Dashboard");
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Weather Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        // Top Panel (Input)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(30, 30, 30));

        JTextField cityField = new JTextField();
        cityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cityField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton getWeatherBtn = new JButton("Get Weather");
        getWeatherBtn.setBackground(new Color(0, 150, 255));
        getWeatherBtn.setForeground(Color.WHITE);
        getWeatherBtn.setFocusPainted(false);
        getWeatherBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        topPanel.add(cityField, BorderLayout.CENTER);
        topPanel.add(getWeatherBtn, BorderLayout.EAST);

        // Weather Card Panel
        JPanel weatherPanel = new JPanel();
        weatherPanel.setBackground(new Color(45, 45, 45));
        weatherPanel.setLayout(new GridLayout(3, 1, 10, 10));
        weatherPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tempLabel = createLabel("Temperature: -- °C");
        JLabel humidityLabel = createLabel("Humidity: -- %");
        JLabel conditionLabel = createLabel("Condition: --");

        weatherPanel.add(tempLabel);
        weatherPanel.add(humidityLabel);
        weatherPanel.add(conditionLabel);

        // Add to main panel
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(weatherPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);

        // Action
        getWeatherBtn.addActionListener(e -> {

            String city = cityField.getText().trim();

            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter a city name!");
                return;
            }

            // Run in background thread
            new Thread(() -> {
                try {
                    String response = getWeatherData(city);
                    JSONObject json = new JSONObject(response);

                    double temp = json.getJSONObject("main").getDouble("temp");
                    int humidity = json.getJSONObject("main").getInt("humidity");
                    String condition = json.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description");

                    SwingUtilities.invokeLater(() -> {
                        tempLabel.setText("Temperature: " + temp + " °C");
                        humidityLabel.setText("Humidity: " + humidity + " %");
                        conditionLabel.setText("Condition: " + condition);
                    });

                } catch (Exception ex) {
                    SwingUtilities
                            .invokeLater(() -> JOptionPane.showMessageDialog(frame, "City not found or API error!"));
                }
            }).start();
        });

        frame.setVisible(true);
    }

    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private static String getWeatherData(String city) throws Exception {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + API_KEY + "&units=metric";

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }
}