package com.justanotherpanel.app.helper;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Emerald on 3/16/2018.
 */

public class Utils {

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String beautifyDouble(double d) {
        DecimalFormat format = new DecimalFormat("0.########");
        return format.format(d);
    }

    public static String beautifyDate(Date date, boolean time) {
        SimpleDateFormat formatter = null;
        if (time)
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        else
            formatter = new SimpleDateFormat("yyyy-MM-dd");

        String res = formatter.format(date);

        return res;
    }

    public static Date stringToDate(String dt) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dt);
            return date;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
