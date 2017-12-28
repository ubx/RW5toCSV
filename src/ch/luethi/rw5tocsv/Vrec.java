package ch.luethi.rw5tocsv;

import java.util.ArrayList;
import java.util.List;

public class Vrec {

    protected enum State {

        Valid,

        HSDVandVSDVnotInRange {
            public String toString() {
                return "HSDV and VSDV not in Range";
            }
        },
        HSDVnotInRange {
            public String toString() {
                return "HSDV not in Range";
            }
        },
        VSDVnotInRange {
            public String toString() {
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
                return "Drift %s%s%s Exceeds Limits";
            }
        }
    }

    protected enum CoordinateState {

        OK,
        InvalidRTKNetwork {
            public String toString() {
                return "Invalid RTK Network: VRS_GISGEO_LV95LHN95";
            }
        },
        MismatchbetweenCoordinateSystem {
            public String toString() {
                return "Mismatch between Coordinate System (%s), RTK Network (%s) and Coordinate Format %s decimals";
            }
        }
    }

    protected double easting;
    protected double northing;
    protected float elevation;
    protected float hsdv;
    protected float vsdv;

    // these value are additions fot the text output
    protected int numberOfMeasurements;
    protected int sats, satsMin, satsMax;
    protected String date;
    protected String time;
    protected State state;
    protected CoordinateState coordinateState;
    protected String srcPN;
    // additional variables
    protected final List<Vrec> srcVrecs = new ArrayList<>();
    protected boolean driftExceedsLimitX;
    protected boolean driftExceedsLimitY;
    protected boolean driftExceedsLimitZ;
    protected boolean coord6dec;
    protected String rtkMethod;
    protected String coordSys;

}
