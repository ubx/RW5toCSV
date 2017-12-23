package ch.luethi.rw5tocsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RW5Parser {


    private enum Mode {Start, GS, HSDV, DT, TM}

    private static String userDefined = null;
    private static String rtkMethod = null;

    public static List<Rrec> getRrecs(String rw5FileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(rw5FileName));
        Mode mode = Mode.Start;
        List<Rrec> rrecs = new ArrayList<>();
        Rrec lastRrec = null;
        // Reading each line of file using Scanner class
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() == 0) continue; // skip empty line
            if (line.startsWith("--GS,")) {
                mode = Mode.GS;
            } else if (line.startsWith("--User Defined:")) {
                userDefined = line;
            } else if (line.startsWith("--RTK Method:")) {
                rtkMethod = line;
            }

            switch (mode) {
                case GS:
                    if (lastRrec != null) {
                        addLastRrec(rrecs, lastRrec);
                    }
                    lastRrec = new Rrec();
                    lastRrec.gs = line;
                    mode = Mode.HSDV;
                    break;
                case HSDV:
                    if (line.startsWith("--HSDV:")) {
                        lastRrec.hsdv = line;
                        mode = Mode.DT;
                        break;
                    }
                case DT:
                    if (line.startsWith("--DT")) {
                        lastRrec.dt = line;
                        mode = Mode.TM;
                        break;
                    }

                case TM:
                    if (line.startsWith("--TM")) {
                        lastRrec.tm = line;
                        mode = Mode.Start;
                        break;
                    }
            }
        }
        if (lastRrec != null) {
            addLastRrec(rrecs, lastRrec);
        }
        return rrecs;
    }

    private static void addLastRrec(List<Rrec> rrecs, Rrec lastRrec) {
        lastRrec.userDefined = userDefined;
        lastRrec.rtkMethod = rtkMethod;
        rrecs.add(lastRrec);
    }

}
