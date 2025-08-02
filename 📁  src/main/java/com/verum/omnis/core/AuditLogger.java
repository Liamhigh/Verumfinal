package com.verum.omnis.core;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AuditLogger {
    private static final String TAG = "AuditLog";
    private static final SimpleDateFormat DF =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    private static String lastHash = "GENESIS";

    public static void log(String event, String detail) {
        try {
            String entry = String.format(
                    "{\"event\":\"%s\",\"time\":\"%s\",\"detail\":\"%s\",\"prevHash\":\"%s\"}",
                    event, DF.format(new Date()), detail.replace("\"", "'"), lastHash);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            lastHash = bytesToHex(md.digest(entry.getBytes()));
            Log.i(TAG, entry);

            File logFile = new File(Utils.ctx.getFilesDir(), "audit.log");
            try (FileWriter fw = new FileWriter(logFile, true)) {
                fw.append(entry).append('\n');
            }
        } catch (Exception e) {
            Log.e(TAG, "Audit fail", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
