package ch.luethi.rw5tocsv;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class ProcessData {

    protected static double norhingLim = RW5toCSV.DEFAULT_NORHING_LIM;
    protected static double eastingLim = RW5toCSV.DEFAULT_EASTING_LIM;
    protected static double elevationLim = RW5toCSV.DEFAULT_ELEVATION_LIM;
    protected static boolean extraComment = false;

    private static final DecimalFormat form3 = new DecimalFormat("0.000");
    private static final String SEP = ",";
    private static final double HSDV_LIM = 0.04;
    private static final double VSDV_LIM = 0.06;
    private static final double POINT_LIM = 0.5;
    private static final double N_DEC7_LIMIT = 1000000.0;
    private static List<Vrec> vRecs = null;

    public static List<String> getCSVRecs(List<Rrec> rRecs) {
        vRecs = new ArrayList<>();
        for (Rrec rrec : rRecs) {
            VrecSrc vrecSrc = getLastVrec(rrec);
            checkCoordinate(vrecSrc, rrec);
            if ((vrecSrc.state == VrecSrc.State.Valid) || isXSDVnotInRange(vrecSrc)) {
                addVrec(vRecs, vrecSrc);
            } else {
                Vrec vrecNew = new Vrec();
                vrecNew.vrecSrcs.add(vrecSrc);
                vrecNew.state = vrecSrc.state;
                vRecs.add(vrecNew);
            }
        }

        List<String> csvRecs = new ArrayList<>();
        int cnt = 1;
        for (Vrec vrec : vRecs) {
            mergePoints(vrec);
            checkNotDriftExceedsLimits(vrec);
            boolean error = isError(vrec);
            csvRecs.add(getGCP(cnt++) + SEP + f3(vrec.easting, error) + SEP + f3(vrec.northing, error) + SEP + f3(vrec.elevation, error)
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv) + getErrorText(vrec));
        }
        return csvRecs;
    }

    private static void addVrec(List<Vrec> vRecs, VrecSrc vrecSrcNew) {
        boolean nf = false;
        for (Vrec vrec : vRecs) {
            for (VrecSrc vrecSrc : vrec.vrecSrcs) {
                if ((distance(vrecSrc, vrecSrcNew) < POINT_LIM)) {
                    vrec.vrecSrcs.add(vrecSrcNew);
                    nf = true;
                    break;
                }
            }
            if (nf) break;
        }
        if (!nf) {
            Vrec vrecNew = new Vrec();
            vrecNew.vrecSrcs.add(vrecSrcNew);
            vRecs.add(vrecNew);
        }
    }


    public static List<String> getCSVRecsWithComment() {
        List<String> csvRecs = new ArrayList<>();
        int cnt = 1;
        for (Vrec vrec : vRecs) {
            boolean error = isError(vrec);
            StringBuffer sb = new StringBuffer();
            sb.append(getGCP(cnt++) + SEP + f3(vrec.easting, error) + SEP + f3(vrec.northing, error) + SEP + f3(vrec.elevation, error)
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv)
                    + "  -  #" + vrec.getNumberOfMeasurements() + " / SATS: " + String.format("%02d-%02d", vrec.satsMin, vrec.satsMax)
                    + " / " + vrec.date + " " + vrec.time + getErrorText2(vrec) + getSrcPNs(vrec));
            if (extraComment) {
                for (VrecSrc vrecSrc : vrec.vrecSrcs) {
                    sb.append("\n " + vrecSrc.rrec.gs);
                }
            }
            csvRecs.add(sb.toString());
        }
        return csvRecs;
    }

    private static String getGCP(int cnt) {
        return String.format("%s%02d", "GCP", cnt);
    }

    private static boolean isError(Vrec vrec) {
        for (VrecSrc srcVrecS : vrec.vrecSrcs) {
            if (srcVrecS.state == VrecSrc.State.DriftExceedsLimits || srcVrecS.state == VrecSrc.State.HSDVandVSDVnotInRange || srcVrecS.state == VrecSrc.State.HSDVnotInRange || srcVrecS.state == VrecSrc.State.VSDVnotInRange) {
                return true;
            }
        }
        //vrec.state = VrecSrc.State.Valid;
        return false;
    }

    private static boolean isXSDVnotInRange(VrecSrc vrecS) {
        return vrecS.state == VrecSrc.State.HSDVandVSDVnotInRange || vrecS.state == VrecSrc.State.HSDVnotInRange || vrecS.state == VrecSrc.State.VSDVnotInRange;
    }

    private static String getSrcPNs(Vrec vrec) {
        int ecnt = 0;
        StringBuilder sb = new StringBuilder();
        for (VrecSrc vrecSrc : vrec.vrecSrcs) {
            if (sb.length() > 0) sb.append(',');
            sb.append(vrecSrc.srcPN);
            if (vrecSrc.state != VrecSrc.State.Valid) {
                sb.append('(').append(vrecSrc.state == VrecSrc.State.DriftExceedsLimits
                        ? String.format(vrecSrc.state.toString(), vrecSrc.driftExceedsLimitX ? "X":"", vrecSrc.driftExceedsLimitY ? "Y":"", vrecSrc.driftExceedsLimitZ ? "Z":"")
                        : vrecSrc.state).append(')');
                ecnt++;
            }
        }
        return sb.insert(0, ecnt > 0 ? " ERROR [" : " [").append("]").toString();
    }


    private static VrecSrc getLastVrec(Rrec rrec) {
        VrecSrc vrecSrc = new VrecSrc();
        vrecSrc.rrec = rrec;
        String[] strs = rrec.gs.split(",");
        // --GS,PN1,N 1200261.6916,E 2608973.7195,EL583.6008,--
        try {
            vrecSrc.northing = Double.valueOf(strs[2].split(" ")[1]);
            vrecSrc.easting = Double.valueOf(strs[3].split(" ")[1]);
            vrecSrc.elevation = Float.valueOf(strs[4].substring(2));
            vrecSrc.srcPN = strs[1];
            // --HSDV:0.011, VSDV:0.014, STATUS:FIXED, SATS:13, AGE:0.6, PDOP:1.853, HDOP:1.100, VDOP:1.491, TDOP:1.116, GDOP:1.479, NSDV
            strs = rrec.hsdv.split(",");
            vrecSrc.hsdv = Float.valueOf(strs[0].split(":")[1]);
            vrecSrc.vsdv = Float.valueOf(strs[1].split(":")[1]);
            vrecSrc.sats = Integer.valueOf(strs[3].split(":")[1]);
            // --DT10-01-2015
            // --TM00:04:50
            vrecSrc.date = rrec.dt.substring(4);
            vrecSrc.time = rrec.tm.substring(4);
            // valid ?
            if ((vrecSrc.hsdv < HSDV_LIM) & (vrecSrc.vsdv < VSDV_LIM)) {
                vrecSrc.state = VrecSrc.State.Valid;
            } else if ((vrecSrc.hsdv >= HSDV_LIM) & (vrecSrc.vsdv >= VSDV_LIM)) {
                vrecSrc.state = VrecSrc.State.HSDVandVSDVnotInRange;
            } else if (vrecSrc.hsdv >= HSDV_LIM) {
                vrecSrc.state = VrecSrc.State.HSDVnotInRange;
            } else {
                vrecSrc.state = VrecSrc.State.VSDVnotInRange;
            }
        } catch (NumberFormatException ex) {
            vrecSrc.state = VrecSrc.State.FloatingFormatError;
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException ex) {
            vrecSrc.state = VrecSrc.State.RW5FormatError;
        }
        return vrecSrc;
    }

    private static void checkNotDriftExceedsLimits(Vrec vrec) {
        VrecSrc vrecSrcLast = null;
        for (VrecSrc vrecSrc : vrec.vrecSrcs) {
            if (vrecSrcLast != null) {
                if (vrecSrc.state == VrecSrc.State.Valid || isXSDVnotInRange(vrecSrc)) {
                    vrecSrc.driftExceedsLimitY = Math.abs(vrecSrcLast.northing - vrecSrc.northing) > norhingLim;
                    vrecSrc.driftExceedsLimitX = Math.abs(vrecSrcLast.easting - vrecSrc.easting) > eastingLim;
                    vrecSrc.driftExceedsLimitZ = Math.abs(vrecSrcLast.elevation - vrecSrc.elevation) > elevationLim;
                    if (vrecSrc.driftExceedsLimitY | vrecSrc.driftExceedsLimitX | vrecSrc.driftExceedsLimitZ) {
                        vrecSrc.state = VrecSrc.State.DriftExceedsLimits;
                    }
                }
            }
            vrecSrcLast = vrecSrc;
        }
    }


    private static void mergePoints(Vrec vrec) {
        vrec.easting = 0;
        vrec.northing = 0;
        vrec.elevation = 0;
        vrec.satsMin = Integer.MAX_VALUE;
        vrec.satsMax = 0;
        vrec.hsdv = 0;
        vrec.vsdv = 0;
        vrec.coordinateState = Vrec.CoordinateState.OK;
        vrec.driftExceedsLimitX = false;
        vrec.driftExceedsLimitZ = false;
        vrec.driftExceedsLimitY = false;
        if (vrec.state == null) {
            vrec.state = Vrec.State.Valid;
        }
        vrec.coord6dec = false;
        vrec.rtkMethod = vrec.vrecSrcs.get(0).rtkMethod;
        vrec.coordSys = vrec.vrecSrcs.get(0).coordSys;
        vrec.date = vrec.vrecSrcs.get(0).date;
        vrec.time = vrec.vrecSrcs.get(0).time;

        for (VrecSrc vr : vrec.vrecSrcs) {
            vrec.easting += vr.easting;
            vrec.northing += vr.northing;
            vrec.elevation += vr.elevation;
            vrec.satsMin = Math.min(vrec.satsMin, vr.sats);
            vrec.satsMax = Math.max(vrec.satsMax, vr.sats);
            vrec.hsdv = Math.max(vrec.hsdv, vr.hsdv);
            vrec.vsdv = Math.max(vrec.vsdv, vr.vsdv);
        }
        vrec.easting /= vrec.getNumberOfMeasurements();
        vrec.northing /= vrec.getNumberOfMeasurements();
        vrec.elevation /= vrec.getNumberOfMeasurements();
    }


    private static void checkCoordinate(VrecSrc vrecSrc, Rrec rrec) {
        vrecSrc.coordinateState = VrecSrc.CoordinateState.OK;
        if (rrec.rtkMethod.contains("LHN95")) {
            vrecSrc.coordinateState = VrecSrc.CoordinateState.InvalidRTKNetwork;
            String[] t = rrec.rtkMethod.split("_");
            vrecSrc.rtkMethod = t[t.length - 1];
            return;
        }

        if (rrec.userDefined.length() == 0) return; // if no userDefined found!

        if (!((rrec.rtkMethod.contains("GISGEO_LV03LN02") & rrec.userDefined.contains("CH1903")) & (vrecSrc.northing < N_DEC7_LIMIT)) &&
                !((rrec.rtkMethod.contains("GISGEO_LV95LN02") & rrec.userDefined.contains("CH1903+")) & (vrecSrc.northing > N_DEC7_LIMIT))) {
            vrecSrc.coordinateState = VrecSrc.CoordinateState.MismatchbetweenCoordinateSystem;
            String[] t = rrec.rtkMethod.split("_");
            vrecSrc.rtkMethod = t[t.length - 1];
            t = rrec.userDefined.split("/");
            vrecSrc.coordSys = t[t.length - 1];
            vrecSrc.coord6dec = vrecSrc.northing < N_DEC7_LIMIT;
        }
    }

    private static String getErrorText(Vrec vrec) {
        return (vrec.state == VrecSrc.State.Valid & vrec.coordinateState == VrecSrc.CoordinateState.OK) ? "" : " *** "
                + (vrec.coordinateState != VrecSrc.CoordinateState.OK ? ((vrec.coordinateState == VrecSrc.CoordinateState.MismatchbetweenCoordinateSystem ? String.format(vrec.coordinateState.toString(),
                vrec.coordSys, vrec.rtkMethod, vrec.coord6dec ? "6" : "7") : vrec.coordinateState.toString())) : vrec.state) + " ***";
    }

    private static String getErrorText2(Vrec vrec) {
        return (vrec.state == VrecSrc.State.Valid & vrec.coordinateState == VrecSrc.CoordinateState.OK) ? "" : (vrec.coordinateState == VrecSrc.CoordinateState.OK ? "" :
                " " + (vrec.coordinateState == VrecSrc.CoordinateState.MismatchbetweenCoordinateSystem ? String.format(vrec.coordinateState.toString(),
                        vrec.coordSys, vrec.rtkMethod, vrec.coord6dec ? "6" : "7") : vrec.coordinateState.toString()));
    }

    private static String f3(double val) {
        return form3.format(val);
    }

    private static String f3(double val, boolean error) {
        if (error) {
            String s = form3.format(val);
            return s.substring(0, s.length() - 3) + "***";
        }
        return f3(val);
    }

    private static String f3(float val) {
        return form3.format(val);
    }

    private static String f3(float val, boolean error) {
        if (error) {
            String s = form3.format(val);
            return s.substring(0, s.length() - 3) + "***";
        }
        return f3(val);
    }

    private static double distance(VrecSrc v0, VrecSrc v1) {
        return Math.sqrt(Math.pow(v0.easting - v1.easting, 2) + Math.pow(v0.northing - v1.northing, 2));
    }

}
