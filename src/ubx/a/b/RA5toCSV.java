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

    private enum Mode {GS, HSDV}


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
            formatter.printHelp("cupToOgnTask", options);
            exit(1);
        }

        Scanner scanner = new Scanner(new File(rw5file));
        Mode mode = Mode.GS;

        String lastGS = null;
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
                        lastGS = line;
                        mode = Mode.HSDV;
                    }
                case HSDV:
                    if (line.startsWith("--HSDV:")) {
                        System.out.println("HSDV>>>" + line);
                        rrecs.add(new Rrec(lastGS,line));
                        lastGS = null;
                        mode = Mode.GS;
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
