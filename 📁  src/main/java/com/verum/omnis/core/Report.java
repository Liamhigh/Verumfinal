package com.verum.omnis.core;

import android.content.Context;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class Report {
    public final File file;

    public Report(Context ctx, Analysis a) {
        file = new File(ctx.getFilesDir(), "report.pdf");
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();
            doc.add(new Paragraph("Verum-Omnis Forensic Report"));
            doc.add(new Paragraph("SHA-512: " + a.evidence.sha512));
            doc.add(new Paragraph("Risk Score: " + a.riskScore));
            doc.add(new Paragraph("Jurisdiction: " + Jurisdiction.current().name));

            BarcodeQRCode qr = new BarcodeQRCode(a.evidence.sha512, 200, 200, null);
            doc.add(qr.getImage());

            doc.close();
            AuditLogger.log("REPORT_GENERATED", a.evidence.sha512);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
