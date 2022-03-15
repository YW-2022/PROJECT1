package uk.ac.warwick.cs126.util;

import java.text.DecimalFormat;

public class FloatTo1 {
    static DecimalFormat df = new DecimalFormat();

    public static float floatTo1(float f){
        df.setMaximumFractionDigits(1);
        return Float.parseFloat(df.format(f));
    }
}
