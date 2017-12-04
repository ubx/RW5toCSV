package ch.luethi.rw5tocsv;

public class Vrec {

    protected enum State {VALID, HSDVorVSDVnotInRange}

    // these values goes into CSV file
    protected String gcp;
    protected double easting;
    protected double northing;
    protected float elevation;
    protected float hsdv;
    protected float vsdv;

    // these value are additions fot the text output
    protected int numberOfMeasurements;
    protected float pdop, pdopMin, pdopMax;
    protected String date;
    protected String time;
    protected State state;
}
