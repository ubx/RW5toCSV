package ch.luethi.rw5tocsv;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class ProcessData {

    private static final DecimalFormat form3 = new DecimalFormat("0.000");
    private static final String SEP = ",";
    private static final double HSDV_LIM = 0.04;
    private static final double VSDV_LIM = 0.06;
    private static final double NORHING_LIM = 0.04;
    private static final double EASTING_LIM = 0.04;
    private static final double ELEVATION_LIM = 0.06;
    private static final double POINT_LIM = 0.5;
    private static final double N_DEC7_LIMIT = 1000000.0;
    private static List<Vrec> vRecs = null;

    public static List<String> getCSVRecs(List<Rrec> rRecs) {
        vRecs = new ArrayList<>();
        List<Vrec> vRecsShort = new ArrayList<>();
        for (Rrec rrec : rRecs) {
            Vrec vrec = getLastVrec(rrec);
            checkCoordinate(vrec, rrec);
            if (vRecs.size() > 0) {
                if ((vrec.state == Vrec.State.Valid) || isXSDVnotInRange(vrec)) {
                    if ((distance(getLastVrec(), vrec) < POINT_LIM)) {
                        checkNotDriftExceedsLimits(getLastVrec(), vrec);
                        vRecsShort.add(vrec);
                        continue;
                    } else {
                        average(getLastVrec(), vRecsShort);
                        vRecsShort.clear();
                    }
                } else {
                    average(getLastVrec(), vRecsShort);
                }
            }
            vRecs.add(vrec);
        }
        addShort(vRecsShort);

        List<String> csvRecs = new ArrayList<>();
        int cnt = 1;
        for (Vrec vrec : vRecs) {
            boolean error = isError(vrec);
            csvRecs.add(getGCP(cnt++) + SEP + f3(vrec.easting, error) + SEP + f3(vrec.northing, error) + SEP + f3(vrec.elevation, error)
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv) + getErrorText(vrec));
        }
        return csvRecs;
    }



    public static List<String> getCSVRecsWithComment() {
        List<String> csvRecs = new ArrayList<>();
        int cnt = 1;
        for (Vrec vrec : vRecs) {
            boolean error = isError(vrec);
            csvRecs.add(getGCP(cnt++) + SEP + f3(vrec.easting, error) + SEP + f3(vrec.northing, error) + SEP + f3(vrec.elevation, error)
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv)
                    + "  -  #" + vrec.numberOfMeasurements + " / SATS: " + String.format("%02d-%02d",vrec.satsMin,vrec.satsMax)
                    + " / " + vrec.date + " " + vrec.time + getErrorText2(vrec) + getSrcPNs(vrec));
        }
        return csvRecs;
    }

    private static Vrec getLastVrec() {
        return vRecs.get(vRecs.size() - 1);
    }

    private static String getGCP(int cnt) {
        return String.format("%s%02d", "GCP", cnt);
    }

    private static boolean isError(Vrec vrec) {
        for (Vrec srcVrec : vrec.srcVrecs) {
            if (srcVrec.state == Vrec.State.DriftExceedsLimits || srcVrec.state == Vrec.State.HSDVandVSDVnotInRange || srcVrec.state == Vrec.State.HSDVnotInRange || srcVrec.state == Vrec.State.VSDVnotInRange) {
                return true;
            }
        }
        return false;
    }

    private static boolean isXSDVnotInRange(Vrec vrec) {
        return vrec.state == Vrec.State.HSDVandVSDVnotInRange || vrec.state == Vrec.State.HSDVnotInRange || vrec.state == Vrec.State.VSDVnotInRange;
    }

    private static void addShort(List<Vrec> vRecsShort) {
        if (vRecsShort.size() > 0) {
            average(getLastVrec(), vRecsShort);
        }
    }

    private static String getSrcPNs(Vrec vrec) {
        int ecnt = 0;
        StringBuilder sb = new StringBuilder();
        for (Vrec srcVrec : vrec.srcVrecs) {
            if (sb.length() > 0) sb.append(',');
            sb.append(srcVrec.srcPN);
            if (srcVrec.state != Vrec.State.Valid) {
                sb.append('(').append(srcVrec.state == Vrec.State.DriftExceedsLimits
                        ? String.format(srcVrec.state.toString(), srcVrec.driftExceedsLimitX ? "X":"", srcVrec.driftExceedsLimitY ? "Y":"", srcVrec.driftExceedsLimitZ ? "Z":"")
                        : srcVrec.state).append(')');
                ecnt++;
            }
        }
        return sb.insert(0, ecnt > 0 ? " ERROR [" : " [").append("]").toString();
    }


    private static Vrec getLastVrec(Rrec rrec) {
        Vrec vrec = new Vrec();
        String strs[] = rrec.gs.split(",");
        // --GS,PN1,N 1200261.6916,E 2608973.7195,EL583.6008,--
        try {
            vrec.northing = Double.valueOf(strs[2].split(" ")[1]);
            vrec.easting = Double.valueOf(strs[3].split(" ")[1]);
            vrec.elevation = Float.valueOf(strs[4].substring(2));
            vrec.srcPN = strs[1];
            vrec.numberOfMeasurements = 1;
            // --HSDV:0.011, VSDV:0.014, STATUS:FIXED, SATS:13, AGE:0.6, PDOP:1.853, HDOP:1.100, VDOP:1.491, TDOP:1.116, GDOP:1.479, NSDV
            strs = rrec.hsdv.split(",");
            vrec.hsdv = Float.valueOf(strs[0].split(":")[1]);
            vrec.vsdv = Float.valueOf(strs[1].split(":")[1]);
            vrec.sats = Integer.valueOf(strs[3].split(":")[1]);
            vrec.satsMin = vrec.sats;
            vrec.satsMax = vrec.sats;
            // --DT10-01-2015
            // --TM00:04:50
            vrec.date = rrec.dt.substring(4);
            vrec.time = rrec.tm.substring(4);
            // valid ?
            if ((vrec.hsdv < HSDV_LIM) & (vrec.vsdv < VSDV_LIM)) {
                vrec.state = Vrec.State.Valid;
            } else if ((vrec.hsdv >= HSDV_LIM) & (vrec.vsdv >= VSDV_LIM)) {
                vrec.state = Vrec.State.HSDVandVSDVnotInRange;
            } else if (vrec.hsdv >= HSDV_LIM) {
                vrec.state = Vrec.State.HSDVnotInRange;
            } else {
                vrec.state = Vrec.State.VSDVnotInRange;
            }
        } catch (NumberFormatException ex) {
            vrec.state = Vrec.State.FloatingFormatError;
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException ex) {
            vrec.state = Vrec.State.RW5FormatError;
        }
        vrec.srcVrecs.add(vrec);
        return vrec;
    }

    private static void checkNotDriftExceedsLimits(Vrec lastVrec, Vrec vrec) {
        if (vrec.state == Vrec.State.Valid || isXSDVnotInRange(vrec)) {
            if (lastVrec != null) {
                vrec.driftExceedsLimitY = (Math.abs(lastVrec.northing - vrec.northing) > NORHING_LIM);
                vrec.driftExceedsLimitX = Math.abs(lastVrec.easting - vrec.easting) > EASTING_LIM;
                vrec.driftExceedsLimitZ = Math.abs(lastVrec.elevation - vrec.elevation) > ELEVATION_LIM;
                if (vrec.driftExceedsLimitY | vrec.driftExceedsLimitX | vrec.driftExceedsLimitZ) {
                    vrec.state = Vrec.State.DriftExceedsLimits;
                }
            }
        }
    }


    private static void average(Vrec vrec, List<Vrec> vRecsShort) {
        vrec.numberOfMeasurements = 1 + vRecsShort.size();
        vrec.satsMax = vrec.sats;
        vrec.satsMin = vrec.sats;
        if (vRecsShort.size() == 0) {
            return;
        }
        for (Vrec vr : vRecsShort) {
            vrec.easting += vr.easting;
            vrec.northing += vr.northing;
            vrec.elevation += vr.elevation;
            vrec.satsMin = Math.min(vrec.satsMin, vr.sats);
            vrec.satsMax = Math.max(vrec.satsMax, vr.sats);
            vrec.hsdv = Math.max(vrec.hsdv, vr.hsdv);
            vrec.vsdv = Math.max(vrec.vsdv, vr.vsdv);
            vrec.srcVrecs.add(vr);
        }
        vrec.easting /= vrec.numberOfMeasurements;
        vrec.northing /= vrec.numberOfMeasurements;
        vrec.elevation /= vrec.numberOfMeasurements;
    }


    private static void checkCoordinate(Vrec vrec, Rrec rrec) {
        vrec.coordinateState = Vrec.CoordinateState.OK;
        if (rrec.rtkMethod.contains("LHN95")) {
            vrec.coordinateState = Vrec.CoordinateState.InvalidRTKNetwork;
            String t[] = rrec.rtkMethod.split("_");
            vrec.rtkMethod = t[t.length - 1];
            return;
        }

        if (rrec.userDefined.length() == 0) return; // if no userDefined found!

        if (!((rrec.rtkMethod.contains("GISGEO_LV03LN02") & rrec.userDefined.contains("CH1903")) & (vrec.northing < N_DEC7_LIMIT)) &&
                !((rrec.rtkMethod.contains("GISGEO_LV95LN02") & rrec.userDefined.contains("CH1903+")) & (vrec.northing > N_DEC7_LIMIT))) {
            vrec.coordinateState = Vrec.CoordinateState.MismatchbetweenCoordinateSystem;
            String t[] = rrec.rtkMethod.split("_");
            vrec.rtkMethod = t[t.length - 1];
            t = rrec.userDefined.split("/");
            vrec.coordSys = t[t.length - 1];
            vrec.coord6dec = vrec.northing < N_DEC7_LIMIT;
        }
    }

    private static String getErrorText(Vrec vrec) {
        return (vrec.state == Vrec.State.Valid & vrec.coordinateState == Vrec.CoordinateState.OK) ? "" : " *** "
                + (vrec.coordinateState != Vrec.CoordinateState.OK ? ((vrec.coordinateState == Vrec.CoordinateState.MismatchbetweenCoordinateSystem ? String.format(vrec.coordinateState.toString(),
                vrec.coordSys, vrec.rtkMethod, vrec.coord6dec ? "6" : "7") : vrec.coordinateState.toString())) : vrec.state) + " ***";
    }

    private static String getErrorText2(Vrec vrec) {
        return (vrec.state == Vrec.State.Valid & vrec.coordinateState == Vrec.CoordinateState.OK) ? "" : (vrec.coordinateState == Vrec.CoordinateState.OK ? "" :
                " " + (vrec.coordinateState == Vrec.CoordinateState.MismatchbetweenCoordinateSystem ? String.format(vrec.coordinateState.toString(),
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

    private static double distance(Vrec v0, Vrec v1) {
        return Math.sqrt(Math.pow(v0.easting - v1.easting, 2) + Math.pow(v0.northing - v1.northing, 2));
    }

}
