package com.verum.omnis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.verum.omnis.core.Analysis;
import com.verum.omnis.core.Evidence;
import com.verum.omnis.core.Escalation;
import com.verum.omnis.core.Jurisdiction;
import com.verum.omnis.core.Report;
import com.verum.omnis.core.Utils;

import java.io.File;

public class ForensicActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> pickLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    process(result.getData().getData());
                }
            });

    private File reportFile;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_forensic);
        findViewById(R.id.btn_pick).setOnClickListener(v -> pick());
        findViewById(R.id.btn_share).setOnClickListener(v -> share());
    }

    private void pick() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickLauncher.launch(i);
    }

    private void process(Uri uri) {
        Utils.status(this, "33", "Securing evidence...");
        new Thread(() -> {
            try {
                Evidence evidence = Evidence.fromUri(this, uri);
                Utils.status(this, "66", "Analyzing...");
                Analysis analysis = new Analysis(evidence);
                reportFile = new Report(this, analysis).file;

                Utils.status(this, "100", "Complete");
                runOnUiThread(() -> findViewById(R.id.btn_share).setVisibility(View.VISIBLE));

                if (analysis.riskScore > Jurisdiction.current().escalationThreshold) {
                    Escalation.email(this, analysis);
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void share() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("application/pdf");
        i.putExtra(Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(this, getPackageName() + ".provider", reportFile));
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(i, "Send Report"));
    }
}
