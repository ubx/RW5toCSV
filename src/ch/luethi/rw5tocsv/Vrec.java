package ch.luethi.rw5tocsv;

import java.util.ArrayList;
import java.util.List;

public class Vrec extends VrecSrc {

    final List<VrecSrc> vrecSrcs = new ArrayList<>();
    int satsMin;
    int satsMax;

    protected int getNumberOfMeasurements() {
        return vrecSrcs.size();
    }

}
