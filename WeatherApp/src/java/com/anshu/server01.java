package com.anshu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author harshit
 */
public class server01 extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet server01</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet server01 at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //Set-up the API
        String apiKey = "1bbe0ef1e5ef398f2dbb1bf94e4bcb08";
        String city = request.getParameter("inputCity");
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
        
        try {
            //API Integeration
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            //Read Data from internet
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            //store in string
            StringBuilder responseContent = new StringBuilder();

            //input lene ke liye from the reader, will create scanner
            Scanner scanner = new Scanner(reader);

            //iterate through scanner and append the input into SB.
            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }
            scanner.close();

            //Typecasting = Parsing the string data into JSON.
            // Parse the JSON response to extract temperature, date, and humidity
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

            //Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();

            //Temperature
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int temperatureCelsius = (int) (temperatureKelvin - 273.15);

            //Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

            //Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            //Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // Set the data as request attributes (for sending to the jsp page)
            request.setAttribute("date", date);
            request.setAttribute("city", city);
            request.setAttribute("temperature", temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition);
            request.setAttribute("humidity", humidity);
            request.setAttribute("windSpeed", windSpeed);
            request.setAttribute("weatherData", responseContent.toString());

            connection.disconnect();
        }catch(JsonSyntaxException | IOException e)
        {
            e.printStackTrace();
        }
        //Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
