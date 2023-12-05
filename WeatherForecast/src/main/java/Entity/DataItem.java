package Entity;

import java.sql.Date;
import java.sql.Time;

/**
 * Represents a data item related to weather forecasts for a specific city.
 */
public class DataItem {
    /**
     * Unique identifier for the data item.
     */
    private int id;

    /**
     * The day of the week associated with the forecast.
     */
    private String dateOfWeek;

    /**
     * The date of the forecast.
     */
    private Date dateForecast;

    /**
     * The time of the forecast.
     */
    private Time timeForecast;

    /**
     * The name of the city for which the forecast is made.
     */
    private String cityName;

    /**
     * The temperature for the forecast.
     */
    private double mainTemp;

    /**
     * The atmospheric pressure for the forecast.
     */
    private int mainPressure;

    /**
     * The humidity for the forecast.
     */
    private int mainHumidity;

    /**
     * The cloudiness percentage for the forecast.
     */
    private int cloudsAll;

    /**
     * The wind speed for the forecast.
     */
    private double windSpeed;

    /**
     * The visibility range for the forecast.
     */
    private int visibility;

    /**
     * The amount of rainfall for the last 3 hours in millimeters.
     */
    private int rain3h;

    /**
     * The description of the weather conditions.
     */
    private String weatherDescription;

    /**
     * The icon representing the weather conditions.
     */
    private String weatherIcon;

    /**
     * Constructs a DataItem object with provided weather forecast details.
     *
     * @param id                  The unique identifier for the data item.
     * @param dateOfWeek          The day of the week associated with the forecast.
     * @param dateForecast        The date of the forecast.
     * @param timeForecast        The time of the forecast.
     * @param cityName            The name of the city for which the forecast is made.
     * @param mainTemp            The temperature for the forecast.
     * @param mainPressure        The atmospheric pressure for the forecast.
     * @param mainHumidity        The humidity for the forecast.
     * @param cloudsAll           The cloudiness percentage for the forecast.
     * @param windSpeed           The wind speed for the forecast.
     * @param visibility          The visibility range for the forecast.
     * @param rain3h              The amount of rainfall for the last 3 hours in millimeters.
     * @param weatherDescription  The description of the weather conditions.
     * @param weatherIcon         The icon representing the weather conditions.
     */
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

    /**
     * Retrieves the unique identifier for the data item.
     *
     * @return The unique identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the data item.
     *
     * @param id The unique identifier to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the day of the week associated with the forecast.
     *
     * @return The day of the week.
     */
    public String getDateOfWeek() {
        return dateOfWeek;
    }

    /**
     * Sets the day of the week associated with the forecast.
     *
     * @param dateOfWeek The day of the week to set.
     */
    public void setDateOfWeek(String dateOfWeek) {
        this.dateOfWeek = dateOfWeek;
    }

    /**
     * Retrieves the date of the forecast.
     *
     * @return The date of the forecast.
     */
    public Date getDateForecast() {
        return dateForecast;
    }

    /**
     * Sets the date of the forecast.
     *
     * @param dateForecast The date of the forecast to set.
     */
    public void setDateForecast(Date dateForecast) {
        this.dateForecast = dateForecast;
    }

    /**
     * Retrieves the time of the forecast.
     *
     * @return The time of the forecast.
     */
    public Time getTimeForecast() {
        return timeForecast;
    }

    /**
     * Sets the time of the forecast.
     *
     * @param timeForecast The time of the forecast to set.
     */
    public void setTimeForecast(Time timeForecast) {
        this.timeForecast = timeForecast;
    }

    /**
     * Retrieves the name of the city for which the forecast is made.
     *
     * @return The city name.
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Sets the name of the city for which the forecast is made.
     *
     * @param cityName The city name to set.
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * Retrieves the temperature for the forecast.
     *
     * @return The forecasted temperature.
     */
    public double getMainTemp() {
        return mainTemp;
    }

    /**
     * Sets the temperature for the forecast.
     *
     * @param mainTemp The temperature to set.
     */
    public void setMainTemp(double mainTemp) {
        this.mainTemp = mainTemp;
    }

    /**
     * Retrieves the atmospheric pressure for the forecast.
     *
     * @return The atmospheric pressure.
     */
    public int getMainPressure() {
        return mainPressure;
    }

    /**
     * Sets the atmospheric pressure for the forecast.
     *
     * @param mainPressure The atmospheric pressure to set.
     */
    public void setMainPressure(int mainPressure) {
        this.mainPressure = mainPressure;
    }

    /**
     * Retrieves the humidity for the forecast.
     *
     * @return The humidity.
     */
    public int getMainHumidity() {
        return mainHumidity;
    }

    /**
     * Sets the humidity for the forecast.
     *
     * @param mainHumidity The humidity to set.
     */
    public void setMainHumidity(int mainHumidity) {
        this.mainHumidity = mainHumidity;
    }

    /**
     * Retrieves the cloudiness percentage for the forecast.
     *
     * @return The cloudiness percentage.
     */
    public int getCloudsAll() {
        return cloudsAll;
    }

    /**
     * Sets the cloudiness percentage for the forecast.
     *
     * @param cloudsAll The cloudiness percentage to set.
     */
    public void setCloudsAll(int cloudsAll) {
        this.cloudsAll = cloudsAll;
    }

    /**
     * Retrieves the wind speed for the forecast.
     *
     * @return The wind speed.
     */
    public double getWindSpeed() {
        return windSpeed;
    }

    /**
     * Sets the wind speed for the forecast.
     *
     * @param windSpeed The wind speed to set.
     */
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * Retrieves the visibility range for the forecast.
     *
     * @return The visibility range.
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility range for the forecast.
     *
     * @param visibility The visibility range to set.
     */
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    /**
     * Retrieves the amount of rainfall for the last 3 hours in millimeters.
     *
     * @return The rainfall in the last 3 hours.
     */
    public int getRain3h() {
        return rain3h;
    }

    /**
     * Sets the amount of rainfall for the last 3 hours in millimeters.
     *
     * @param rain3h The rainfall in the last 3 hours to set.
     */
    public void setRain3h(int rain3h) {
        this.rain3h = rain3h;
    }

    /**
     * Retrieves the description of the weather conditions.
     *
     * @return The weather description.
     */
    public String getWeatherDescription() {
        return weatherDescription;
    }

    /**
     * Sets the description of the weather conditions.
     *
     * @param weatherDescription The weather description to set.
     */
    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    /**
     * Retrieves the icon representing the weather conditions.
     *
     * @return The weather icon.
     */
    public String getWeatherIcon() {
        return weatherIcon;
    }

    /**
     * Sets the icon representing the weather conditions.
     *
     * @param weatherIcon The weather icon to set.
     */
    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
}
