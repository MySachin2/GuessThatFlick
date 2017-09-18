package com.phacsin.gd.guessthatmovie;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by GD on 8/26/2017.
 */

public class Utility {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 50);
        return noOfColumns;
    }
}