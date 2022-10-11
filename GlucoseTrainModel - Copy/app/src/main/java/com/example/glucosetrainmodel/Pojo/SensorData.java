package com.example.glucosetrainmodel.Pojo;

public class SensorData {
//    22,4.63,1014.43,30.41,70.00,31.20,88.16,100.65,-

    private float mq3_ppm;//ppm
    private float mq3_kohm;//Kohm
    private float bmp_pressure;//hPa
    private float bmp_temperature;//Celcius
    private float dht_humidity;//Humidity
    private float dht_celcius;//Celcius
    private float dht_fahrenheit;//Fahrenheit
    private float dht_heatindex;//Heat index

    public SensorData() {
    }

    public SensorData(float mq3_ppm, float mq3_kohm, float bmp_pressure, float bmp_temperature, float dht_humidity, float dht_celcius, float dht_fahrenheit, float dht_heatindex) {
        this.mq3_ppm = mq3_ppm;
        this.mq3_kohm = mq3_kohm;
        this.bmp_pressure = bmp_pressure;
        this.bmp_temperature = bmp_temperature;
        this.dht_humidity = dht_humidity;
        this.dht_celcius = dht_celcius;
        this.dht_fahrenheit = dht_fahrenheit;
        this.dht_heatindex = dht_heatindex;
    }

    public float getMq3_ppm() {
        return mq3_ppm;
    }

    public void setMq3_ppm(float mq3_ppm) {
        this.mq3_ppm = mq3_ppm;
    }

    public float getMq3_kohm() {
        return mq3_kohm;
    }

    public void setMq3_kohm(float mq3_kohm) {
        this.mq3_kohm = mq3_kohm;
    }

    public float getBmp_pressure() {
        return bmp_pressure;
    }

    public void setBmp_pressure(float bmp_pressure) {
        this.bmp_pressure = bmp_pressure;
    }

    public float getBmp_temperature() {
        return bmp_temperature;
    }

    public void setBmp_temperature(float bmp_temperature) {
        this.bmp_temperature = bmp_temperature;
    }

    public float getDht_humidity() {
        return dht_humidity;
    }

    public void setDht_humidity(float dht_humidity) {
        this.dht_humidity = dht_humidity;
    }

    public float getDht_celcius() {
        return dht_celcius;
    }

    public void setDht_celcius(float dht_celcius) {
        this.dht_celcius = dht_celcius;
    }

    public float getDht_fahrenheit() {
        return dht_fahrenheit;
    }

    public void setDht_fahrenheit(float dht_fahrenheit) {
        this.dht_fahrenheit = dht_fahrenheit;
    }

    public float getDht_heatindex() {
        return dht_heatindex;
    }

    public void setDht_heatindex(float dht_heatindex) {
        this.dht_heatindex = dht_heatindex;
    }
}
