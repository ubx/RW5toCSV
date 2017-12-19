package ch.luethi.rw5tocsv;

import java.util.ArrayList;
import java.util.List;

public class Vrec {

    protected enum State {
        Valid,
        HSDVorVSDVnotInRange {
            public String toString() {
                if (hsdv & vsdv)
                    return "HSDV and VSDV not in Range";
                else if (hsdv)
                    return "HSDV not in Range";
                return "VSDV not in Range";
            }
        },
        FloatingFormatError {
            public String toString() {
                return "Floating Format Error";
            }
        },
        RW5FormatError {
            public String toString() {
                return "RW5 Format Error";
            }
        },
        DriftExceedsLimits {
            public String toString() {
                return "Drift Exceeds Limits";
            }
        };
        boolean hsdv, vsdv;
    }

    protected static class SrcDesc {
        String name;
        State state;

        SrcDesc(String name, State state) {
            this.name = name;
            this.state = state;
        }
    }

    protected double easting;
    protected double northing;
    protected float elevation;
    protected float hsdv;
    protected float vsdv;

    // these value are additions fot the text output
    protected int numberOfMeasurements;
    protected float sats, satsMin, satsMax;
    protected String date;
    protected String time;
    protected State state;
    protected String srcPN;
    protected double distTopPrev = 0.0;
    protected List<SrcDesc> srcDescs = new ArrayList<>();
}
