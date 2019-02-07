package ch.luethi.rw5tocsv;

public class VrecSrc {

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
                return "Invalid RTK Network: LV95LHN95";
            }
        },
        MismatchbetweenCoordinateSystem {
            public String toString() {
                return "Mismatch between Coordinate System (%s), RTK Network (%s) and Coordinate Format %s decimals";
            }
        }
    }

    double easting;
    double northing;
    float elevation;
    float hsdv;
    float vsdv;

    // these value are additions fot the text output
    int sats;
    String date;
    String time;
    State state;
    CoordinateState coordinateState;
    String srcPN;

    // additional variables
    boolean driftExceedsLimitX;
    boolean driftExceedsLimitY;
    boolean driftExceedsLimitZ;
    boolean coord6dec;
    String rtkMethod;
    String coordSys;
}
