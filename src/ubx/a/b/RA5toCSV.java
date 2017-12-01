package ubx.a.b;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.*;

import static java.lang.System.exit;

public class RA5toCSV {

    private static final String RW5 = "r";
    private static final String CSV = "c";
    private static final String TXT = "t";

    private static String rw5file, csvfile, txtfile;

    private enum Mode {GS, HSDV, DT, TM}


    public static void main(String args[]) throws IOException {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addRequiredOption(RW5, "RW5 input file", true, "RW5 file to extract data");
        options.addRequiredOption(CSV, "CSV output file", true, "CSV to write data");
        options.addOption(TXT, "TXT output file", true, "optional text to write comments");

        CommandLine cmd = null;

        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        try {
            cmd = parser.parse(options, args);
            rw5file = cmd.getOptionValue(RW5);
            csvfile = cmd.getOptionValue(CSV);
            if (cmd.hasOption(TXT)) {
                txtfile = cmd.getOptionValue(TXT);
            } else {
                txtfile = null;
            }

        } catch (ParseException e) {
            formatter.printHelp("ra5toCsv", options);
            exit(1);
        }

        Scanner scanner = new Scanner(new File(rw5file));
        Mode mode = Mode.GS;

        Rrec lastRrec = null;
        List<Rrec> rrecs = new ArrayList<Rrec>();

        // Reading each line of file using Scanner class
        int lineNumber = 1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() == 0) continue; // skip empty line
            switch (mode) {
                case GS:
                    if (line.startsWith("--GS,")) {
                        System.out.println("GS>>>" + line);
                        lastRrec = new Rrec();
                        lastRrec.gs = line;
                        mode = Mode.HSDV;
                        break;
                    }
                case HSDV:
                    if (line.startsWith("--HSDV:")) {
                        System.out.println("HSDV>>>" + line);
                        if (lastRrec == null) {
                            lastRrec = new Rrec();
                        }
                        lastRrec.hsdv = line;
                        mode = Mode.DT;
                        break;
                    }
                case DT:
                    if (line.startsWith("--DT")) {
                        System.out.println("DT>>>" + line);
                        if (lastRrec == null) {
                            lastRrec = new Rrec();
                        }
                        lastRrec.dt = line;
                        mode = Mode.TM;
                        break;
                    }

                case TM:
                    if (line.startsWith("--TM")) {
                        System.out.println("TM>>>" + line);
                        if (lastRrec == null) {
                            lastRrec = new Rrec();
                        }
                        lastRrec.tm = line;
                        mode = Mode.GS;
                        rrecs.add(lastRrec);
                        break;
                    }
            }
        }

        // Process ...
        List<String> csvs = ProcessData.getCSVRecs(rrecs);

        BufferedWriter csvOut = new BufferedWriter(new FileWriter(new File(csvfile)));
        for (String l : csvs) {
            csvOut.write(l + "\n");
        }
        csvOut.close();
    }
}
