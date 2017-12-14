package ch.luethi.rw5tocsv;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProcessData {

    private static final DecimalFormat form3 = new DecimalFormat("0.000");
    private static final DecimalFormat form3Error = new DecimalFormat("0.***");
    private static final String SEP = ",";
    private static List<Vrec> vRecs = null;

    public static List<String> getCSVRecs(List<Rrec> rRecs) {
        vRecs = new ArrayList<>();
        List<Vrec> vRecsShort = new ArrayList<>();
        for (Rrec rrec : rRecs) {
            Vrec vrec = getVrec(rrec);
            if (vRecs.size() > 0) {
                if (vrec.state == Vrec.State.Valid || vrec.state == Vrec.State.HSDVorVSDVnotInRange) {
                    vrec.distTopPrev = distance(vRecs.get(vRecs.size() - 1), vrec); // todo -- for test only
                    if ((distance(vRecs.get(vRecs.size() - 1), vrec) < 0.5)) {
                        checkNotDriftExceedsLimits(vRecs.get(vRecs.size() - 1), vrec);
                        vRecsShort.add(vrec);
                        continue;
                    } else {
                        average(vRecs.get(vRecs.size() - 1), vRecsShort);
                        vRecsShort.clear();
                    }
                } else {
                    average(vRecs.get(vRecs.size() - 1), vRecsShort);
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
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv) + (vrec.state == Vrec.State.Valid ? "" : " *** " + vrec.state + " ***"));
        }
        return csvRecs;
    }

    private static String getGCP(int cnt) {
        return String.format("%s%02d", "GCP", cnt);
    }


    public static List<String> getCSVRecsWithComment() {
        List<String> csvRecs = new ArrayList<>();
        int cnt = 1;
        for (Vrec vrec : vRecs) {
            boolean error = isError(vrec);
            csvRecs.add(getGCP(cnt++) + SEP + f3(vrec.easting, error) + SEP + f3(vrec.northing, error) + SEP + f3(vrec.elevation, error)
                    + SEP + f3(vrec.hsdv) + SEP + f3(vrec.vsdv)
                    + "  -  #" + vrec.numberOfMeasurements + " / PDOP: " + f3(vrec.pdopMin) + "-" + f3(vrec.pdopMax)
                    + " / " + vrec.date + " " + vrec.time + getsrcPNs(vrec));
        }
        return csvRecs;
    }

    private static boolean isError(Vrec vrec) {
        for (Vrec.SrcDesc desc : vrec.srcDescs) {
            if ((desc.state == Vrec.State.DriftExceedsLimits) || (desc.state == Vrec.State.HSDVorVSDVnotInRange))
                return true;
        }
        return false;
    }

    private static void addShort(List<Vrec> vRecsShort) {
        if (vRecsShort.size() > 0) {
            average(vRecs.get(vRecs.size() - 1), vRecsShort);
        }
    }

    private static String getsrcPNs(Vrec vrec) {
        int ecnt = 0;
        StringBuilder sb = new StringBuilder();
        for (Vrec.SrcDesc desc : vrec.srcDescs) {
            if (sb.length() > 0) sb.append(',');
            sb.append(desc.name);
            if (desc.state != Vrec.State.Valid) {
                sb.append('(').append(desc.state).append(')');
                ecnt++;
            }
        }
        return sb.insert(0, ecnt > 0 ? " ERROR from: " : " from: ").toString();
    }


    private static Vrec getVrec(Rrec rrec) {
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
            vrec.pdop = Float.valueOf(strs[5].split(":")[1]);
            vrec.pdopMin = vrec.pdop;
            vrec.pdopMax = vrec.pdop;
            // --DT10-01-2015
            // --TM00:04:50
            vrec.date = rrec.dt.substring(4);
            vrec.time = rrec.tm.substring(4);
            // valid ?
            vrec.state = ((vrec.hsdv < 0.04) & (vrec.vsdv < 0.06)) ? Vrec.State.Valid : Vrec.State.HSDVorVSDVnotInRange;
            vrec.srcDescs.add(new Vrec.SrcDesc(vrec.srcPN, vrec.state));
        } catch (NumberFormatException ex) {
            vrec.state = Vrec.State.FloatingFormatError;
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException ex) {
            vrec.state = Vrec.State.RW5FormatError;
        }
        return vrec;
    }

    private static void checkNotDriftExceedsLimits(Vrec lastVrec, Vrec vrec) {
        if (vrec.state == Vrec.State.Valid) {
            if (lastVrec != null) {
                if ((Math.abs(lastVrec.northing - vrec.northing) > 0.04)
                        | (Math.abs(lastVrec.easting - vrec.easting) > 0.04)
                        | (Math.abs(lastVrec.elevation - vrec.elevation) > 0.06)) {
                    vrec.state = Vrec.State.DriftExceedsLimits;
                }
            }
        }
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
            vrec.srcDescs.add(new Vrec.SrcDesc(vr.srcPN, vr.state));
        }
        vrec.easting /= vrec.numberOfMeasurements;
        vrec.northing /= vrec.numberOfMeasurements;
        vrec.elevation /= vrec.numberOfMeasurements;
    }


    private static String f3(double val) {
        return form3.format(val);
    }

    private static String f3(double val, boolean error) {
        if (error) return form3Error.format(val);
        return f3(val);
    }

    private static String f3(float val) {
        return form3.format(val);
    }

    private static String f3(float val, boolean error) {
        if (error) return form3Error.format(val);
        return f3(val);
    }

    private static double distance(Vrec v0, Vrec v1) {
        return Math.sqrt(Math.pow(v0.easting - v1.easting, 2) + Math.pow(v0.northing - v1.northing, 2));
    }

}
