package Entity;

import java.sql.Date;
import java.sql.Time;

public class DataItem {
    private int id;
    private String dateOfWeek;
    private Date dateForecast;
    private Time timeForecast;
    private String cityName;
    private double mainTemp;
    private int mainPressure;
    private int mainHumidity;
    private int cloudsAll;
    private double windSpeed;
    private int visibility;
    private int rain3h;
    private String weatherDescription;
    private String weatherIcon;

    // Constructor, getters, and setters

    public DataItem(int id, String dateOfWeek, Date dateForecast, Time timeForecast, String cityName,
                    double mainTemp, int mainPressure, int mainHumidity, int cloudsAll,
                    double windSpeed, int visibility, int rain3h, String weatherDescription, String weatherIcon) {
        this.id = id;
        this.dateOfWeek = dateOfWeek;
        this.dateForecast = dateForecast;
        this.timeForecast = timeForecast;
        this.cityName = cityName;
        this.mainTemp = mainTemp;
        this.mainPressure = mainPressure;
        this.mainHumidity = mainHumidity;
        this.cloudsAll = cloudsAll;
        this.windSpeed = windSpeed;
        this.visibility = visibility;
        this.rain3h = rain3h;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateOfWeek() {
        return dateOfWeek;
    }

    public void setDateOfWeek(String dateOfWeek) {
        this.dateOfWeek = dateOfWeek;
    }

    public Date getDateForecast() {
        return dateForecast;
    }

    public void setDateForecast(Date dateForecast) {
        this.dateForecast = dateForecast;
    }

    public Time getTimeForecast() {
        return timeForecast;
    }

    public void setTimeForecast(Time timeForecast) {
        this.timeForecast = timeForecast;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getMainTemp() {
        return mainTemp;
    }

    public void setMainTemp(double mainTemp) {
        this.mainTemp = mainTemp;
    }

    public int getMainPressure() {
        return mainPressure;
    }

    public void setMainPressure(int mainPressure) {
        this.mainPressure = mainPressure;
    }

    public int getMainHumidity() {
        return mainHumidity;
    }

    public void setMainHumidity(int mainHumidity) {
        this.mainHumidity = mainHumidity;
    }

    public int getCloudsAll() {
        return cloudsAll;
    }

    public void setCloudsAll(int cloudsAll) {
        this.cloudsAll = cloudsAll;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getRain3h() {
        return rain3h;
    }

    public void setRain3h(int rain3h) {
        this.rain3h = rain3h;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
}
