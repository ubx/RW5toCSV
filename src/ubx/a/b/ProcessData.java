package ubx.a.b;

import ubx.a.b.Rrec;

import java.util.ArrayList;
import java.util.List;

public class ProcessData {


    public static List<String> getCSVRecs(List<Rrec> rRecs) {
        List<String> csvRecs = new ArrayList<String>();
        for (Rrec rrec : rRecs) {
            csvRecs.add(rrec.gs + "," + rrec.hsdv);
        }


        return csvRecs;
    }

}
