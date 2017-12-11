package ch.luethi.rw5tocsv;

public class Vrec {

    protected enum State {Valid,
        HSDVorVSDVnotInRange{
            public String toString() {
                return "HSDV or VSDV not in Range";
            }
        },
        FloatingFormatError{
            public String toString() {
                return "Floating Format Error";
            }
        },
        RW5FormatError{
            public String toString() {
                return "RW5 Format Error";
            }
        },
        DriftExceedsLimits{
        public String toString() {
            return "Drift Exceeds Limits";
        }
    }}

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
    protected final StringBuffer srcPNs = new StringBuffer();
}
