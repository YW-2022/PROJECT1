package uk.ac.warwick.cs126.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class HaversineDistanceCalculator {

    private final static float R = 6372.8f;
    private final static float kilometresInAMile = 1.609344f;

    public static float inKilometres(float lat1, float lon1, float lat2, float lon2) {
        lat1*=(Math.PI/180);
        lon1*=(Math.PI/180);
        lat2*=(Math.PI/180);
        lon2*=(Math.PI/180);
        double a=Math.pow(Math.sin((lat2-lat1)/2),2)
                +Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin((lon2-lon1)/2),2);
        double c=  2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return FloatTo1.floatTo1((float)(R*c));
    }

    public static float inMiles(float lat1, float lon1, float lat2, float lon2) {
        lat1*=(Math.PI/180);
        lon1*=(Math.PI/180);
        lat2*=(Math.PI/180);
        lon2*=(Math.PI/180);
        double a=Math.pow(Math.sin((lat2-lat1)/2),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin((lon2-lon1)/2),2);
        double c=  2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return FloatTo1.floatTo1((float) (R*c/kilometresInAMile));
    }
}