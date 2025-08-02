package com.verum.omnis.core;

public class Jurisdiction {
    public final String name;
    public final double escalationThreshold;

    private Jurisdiction(String n, double t) {
        name = n;
        escalationThreshold = t;
    }

    public static Jurisdiction current() {
        // TODO: Persist user selection
        return new Jurisdiction("UAE", 8.5);
    }
}
