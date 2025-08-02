package com.verum.omnis.core;

import java.util.ArrayList;
import java.util.List;

public class Analysis {
    public final Evidence evidence;
    public final double riskScore;
    public final List<String> triggers = new ArrayList<>();

    public Analysis(Evidence e) {
        this.evidence = e;
        this.riskScore = (e.file.length() % 1000) / 10.0;
        if (riskScore > 7) triggers.add("DOCUMENT_TAMPERING");
    }
}
