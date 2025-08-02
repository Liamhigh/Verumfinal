package com.verum.omnis.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Escalation {
    public static void email(Context ctx, Analysis a) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"un-escalate@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Rule-39 Escalation");
        i.putExtra(Intent.EXTRA_TEXT,
                "Violation detected: " + a.triggers + "\nEvidence: " + a.evidence.sha512);
        ctx.startActivity(Intent.createChooser(i, "Escalate"));
    }
}
