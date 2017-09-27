package com.elmoselhy.ahmed.agp;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by ahmedelmoselhy on 9/3/2017.
 */

public class Fonts {
    Typeface arefRuqaaBold, lobsterRegular, dancingScriptRegular, robotoLighte, robotoMeduim, robotoRegular, robotoThin;

    public Fonts(Context c) {

        dancingScriptRegular = Typeface.createFromAsset(c.getAssets(), "fonts/DancingScript-Regular.ttf");
        lobsterRegular = Typeface.createFromAsset(c.getAssets(), "fonts/SedgwickAve-Regular.ttf");
        robotoLighte = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Light.ttf");
        arefRuqaaBold = Typeface.createFromAsset(c.getAssets(), "fonts/Harmattan-Regular.ttf");
        robotoThin = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Thin.ttf");
        robotoRegular = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Regular.ttf");
        robotoMeduim = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Medium.ttf");
    }
}
