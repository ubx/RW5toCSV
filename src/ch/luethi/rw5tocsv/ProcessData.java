package ch.luethi.rw5tocsv;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProcessData {

    private static final DecimalFormat form3 = new DecimalFormat("0.000");
    private static final String SEP = ",";
    private static List<Vrec> vRecs = null;

    public static List<String> getCSVRecs(List<Rrec> rRecs) {
        List<String> csvRecs = new ArrayList<>();
        vRecs = new ArrayList<>();
        List<Vrec> vRecsShort = new ArrayList<>();
        Vrec vrecLast = null;
        int cnt = 1;
        for (Rrec rrec : rRecs) {
            Vrec vrec = getVrec(rrec);
            if (vrec.state == Vrec.State.Valid) {
                if (vrecLast == null) {
                    vRecs.add(vrec);
                } else {
                    if ((distance(vrecLast, vrec) < 0.5) && validate(vrecLast, vrec)) {
                        vRecsShort.add(vrec);
                    } else {
                        average(vRecs.get(vRecs.size() - 1), vRecsShort);
                        vRecsShort.clear();
                        vRecs.add(vrec);
                    }
                }
                vrecLast = vrec;
            } else {
                vRecs.add(vrec);
            }
        }
        if (vRecsShort.size() > 0) {
            average(vRecs.get(vRecs.size() - 1), vRecsShort);
        }

        for (Vrec vrec : vRecs) {
            csvRecs.add("GCP" + cnt++ + SEP + f3(vrec.easting) + SEP + f3(vrec.northing) + SEP + f3(vrec.elevation)
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv) + (vrec.state == Vrec.State.Valid ? "" : " *** " + vrec.state + " ***"));
        }
        return csvRecs;
    }


    public static List<String> getCSVRecsWithComment() {
        List<String> csvRecs = new ArrayList<>();
        int cnt = 1;
        for (Vrec vrec : vRecs) {
            csvRecs.add("GCP" + cnt++ + SEP + f3(vrec.easting) + SEP + f3(vrec.northing) + SEP + f3(vrec.elevation)
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv)
                    + "  -  #" + vrec.numberOfMeasurements + " / PDOP: " + f3(vrec.pdopMin) + "-" + f3(vrec.pdopMax)
                    + " / " + vrec.date + " " + vrec.time + (vrec.state == Vrec.State.Valid ? "" : "  " + vrec.state.toString())
                    + " from: " + vrec.srcPNs);
        }
        return csvRecs;
    }


    private static Vrec getVrec(Rrec rrec) {
        Vrec vrec = new Vrec();
        String strs[] = rrec.gs.split(",");
        // --GS,PN1,N 1200261.6916,E 2608973.7195,EL583.6008,--
        try {
            vrec.northing = Double.valueOf(strs[2].split(" ")[1]);
            vrec.easting = Double.valueOf(strs[3].split(" ")[1]);
            vrec.elevation = Float.valueOf(strs[4].substring(2));
            vrec.srcPNs = strs[1];
            // --HSDV:0.011, VSDV:0.014, STATUS:FIXED, SATS:13, AGE:0.6, PDOP:1.853, HDOP:1.100, VDOP:1.491, TDOP:1.116, GDOP:1.479, NSDV
            strs = rrec.hsdv.split(",");
            vrec.hsdv = Float.valueOf(strs[0].split(":")[1]);
            vrec.vsdv = Float.valueOf(strs[1].split(":")[1]);
            vrec.pdop = Float.valueOf(strs[5].split(":")[1]);
            // --DT10-01-2015
            // --TM00:04:50
            vrec.date = rrec.dt.substring(4);
            vrec.time = rrec.tm.substring(4);
            // valid ?
            vrec.state = (vrec.hsdv < 0.04 & vrec.vsdv < 0.06) ? Vrec.State.Valid : Vrec.State.HSDVorVSDVnotInRange;
        } catch (NumberFormatException ex) {
            vrec.state = Vrec.State.FloatingFormatError;
        } catch (ArrayIndexOutOfBoundsException ex) {
            vrec.state = Vrec.State.RW5FormatError;
        } catch (StringIndexOutOfBoundsException ex) {
            vrec.state = Vrec.State.RW5FormatError;
        }
        return vrec;
    }

    private static boolean validate(Vrec lastVrec, Vrec vrec) {
        if (vrec.state == Vrec.State.Valid) {
            if (lastVrec != null) {
                if ((Math.abs(lastVrec.northing - vrec.northing) > 0.04)
                        | (Math.abs(lastVrec.easting - vrec.easting) > 0.04)
                        | (Math.abs(lastVrec.elevation - vrec.elevation) > 0.06)) {
                    vrec.state = Vrec.State.DriftExceedsLimits;
                }
            }
        }
        return vrec.state == Vrec.State.Valid;
    }


    private static void average(Vrec vrec, List<Vrec> vRecsShort) {
        if (vrec.state != Vrec.State.Valid) return;
        vrec.numberOfMeasurements = 1 + vRecsShort.size();
        vrec.pdopMax = vrec.pdop;
        vrec.pdopMin = vrec.pdop;
        if (vRecsShort.size() == 0) return;
        for (Vrec vr : vRecsShort) {
            vrec.easting += vr.easting;
            vrec.northing += vr.northing;
            vrec.elevation += vr.elevation;
            vrec.pdopMin = Math.min(vrec.pdopMin, vr.pdop);
            vrec.pdopMax = Math.max(vrec.pdopMax, vr.pdop);
            vrec.srcPNs += "," + vr.srcPNs;
        }
        vrec.easting /= vrec.numberOfMeasurements;
        vrec.northing /= vrec.numberOfMeasurements;
        vrec.elevation /= vrec.numberOfMeasurements;
    }



    private static String f3(double val) {
        return form3.format(val);
    }

    private static String f3(float val) {
        return form3.format(val);
    }

    private static double distance(Vrec v0, Vrec v1) {
        return Math.sqrt(Math.pow(v0.easting - v1.easting, 2) + Math.pow(v0.northing - v1.northing, 2));

    }

}
