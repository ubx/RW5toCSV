package ubx.a.b;

import ubx.a.b.Rrec;

import java.util.ArrayList;
import java.util.List;

public class ProcessData {


    public static List<String> getCSVRecs(List<Rrec> rRecs) {
        List<String> csvRecs = new ArrayList<String>();
        List<Vrec> vRecs = new ArrayList<Vrec>();
        for (Rrec rrec : rRecs) {
            Vrec vrec = new Vrec();
            String strs[] = rrec.gs.split(",");
            // --GS,PN1,N 1200261.6916,E 2608973.7195,EL583.6008,--
            vrec.easting = Double.valueOf(strs[2].split(" ")[1]);
            vrec.northing = Double.valueOf(strs[3].split(" ")[1]);
            vrec.elevation = Float.valueOf(strs[4].substring(2));


         // --HSDV:0.011, VSDV:0.014, STATUS:FIXED, SATS:13, AGE:0.6, PDOP:1.853, HDOP:1.100, VDOP:1.491, TDOP:1.116, GDOP:1.479, NSDV
            strs = rrec.hsdv.split(",");
            vrec.hsdv = Float.valueOf(strs[0].split(":")[1]);
            vrec.vsdv = Float.valueOf(strs[1].split(":")[1]);
            vrec.pdop = Float.valueOf(strs[7].split(":")[1]);
            // todo -- add date and time


            vRecs.add(vrec);

            // todo:
            //   loop over vRecs
            //   calculate distance between points
            //   calculate "points average" position
            //   create an new vRecs list with reduced/additional points
            //   add uniques names
            //   create a new csvRecs aand return it



            csvRecs.add(rrec.gs + "," + rrec.hsdv);
        }


        return csvRecs;
    }

}
