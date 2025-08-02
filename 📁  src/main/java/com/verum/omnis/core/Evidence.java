package com.verum.omnis.core;

import android.content.Context;
import android.net.Uri;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.*;
import java.security.*;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Evidence {
    public final File file;
    public final String sha512;

    private Evidence(File file, String sha512) {
        this.file = file;
        this.sha512 = sha512;
    }

    public static Evidence fromUri(Context ctx, Uri uri) throws Exception {
        File out = new File(ctx.getFilesDir(), "evidence_" + System.currentTimeMillis());
        try (InputStream in = ctx.getContentResolver().openInputStream(uri);
             OutputStream os = new FileOutputStream(out)) {

            Mac mac = Mac.getInstance("HmacSHA512");
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            String alias = "forensic_key";
            if (!ks.containsAlias(alias)) {
                KeyGenerator kg = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_HMAC_SHA512, "AndroidKeyStore");
                kg.init(new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_SIGN).build());
                kg.generateKey();
            }
            mac.init((Key) ks.getKey(alias, null));

            byte[] buf = new byte[8192];
            int r;
            while ((r = Objects.requireNonNull(in).read(buf)) != -1) {
                mac.update(buf, 0, r);
                os.write(buf, 0, r);
            }
            String hash = bytesToHex(mac.doFinal());
            AuditLogger.log("EVIDENCE_SECURED", hash);
            return new Evidence(out, hash);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
