package com.verum.omnis.core;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.verum.omnis.R;

public class Utils {
    public static Context ctx;

    public static void status(Context c, String percent, String msg) {
        ctx = c;
        ((ProgressBar) c.findViewById(R.id.progress)).setProgress(Integer.parseInt(percent));
        ((TextView) c.findViewById(R.id.status)).setText(msg);
    }
}
