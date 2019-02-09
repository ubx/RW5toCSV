package ch.luethi.rw5tocsv;

import java.io.*;
import java.util.List;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import static java.lang.System.exit;

class RW5toCSV {

    private static final String RW5 = "r";
    private static final String CSV = "c";
    private static final String LOG = "l";
    private static final String VER = "v";
    private static final String DEF = "d";
    private static final String NLIM = "nl";
    private static final String ELIM = "el";
    private static final String ELELIM = "elel";
    private static final String EXC = "ec";


    protected static final double DEFAULT_NORHING_LIM = 0.04;
    protected static final double DEFAULT_EASTING_LIM = 0.04;
    protected static final double DEFAULT_ELEVATION_LIM = 0.06;

    private static String rw5FileName, csvFileName, logFileName;

    public static void main(String args[]) throws IOException {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(RW5, "RW5 input file", true, "RW5 file to extract data");
        options.addOption(CSV, "CSV output file", true, "csv file to write data, optional. If not specified the output is <file-name>.csv in the same directory as the input file");
        options.addOption(LOG, "LOG output file", true, "log file to write comments, optional. If not specified the output is <file-name>.log in the same directory as the input file");
        options.addOption(VER, "version of the program", false, "prints the version number of the program");
        options.addOption(DEF, "default limits", false, "prints the default values of configurable limits");
        options.addOption(NLIM, "norhing limit", true, "norhing limit");
        options.addOption(ELIM, "easting limit", true, "easting limit");
        options.addOption(ELELIM, "elevation limit", true, "erlrvation limit");
        options.addOption(EXC, "extra comment", false, "output extra comment in log file");

        CommandLine cmd;

        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        try {
            cmd = parser.parse(options, args);
            rw5FileName = cmd.getOptionValue(RW5);
            csvFileName = cmd.getOptionValue(CSV);
            if (cmd.hasOption(VER)) {
                System.out.println("version " + Version.version);
                exit(1);
            } else if (cmd.hasOption(DEF)) {
                System.out.println("default norhing limit: " + DEFAULT_NORHING_LIM);
                System.out.println("default easting limit: " + DEFAULT_EASTING_LIM);
                System.out.println("default elevation limit: " + DEFAULT_ELEVATION_LIM);
                exit(1);
            } else if (cmd.hasOption(CSV)) {
                csvFileName = cmd.getOptionValue(CSV);
            } else {
                csvFileName = getFullPathWithBaseName() + ".csv";
            }
            if (cmd.hasOption(LOG)) {
                logFileName = cmd.getOptionValue(LOG);
            } else {
                logFileName = getFullPathWithBaseName() + ".log";
            }
            if (cmd.hasOption(NLIM)) {
                ProcessData.norhingLim = Double.valueOf(cmd.getOptionValue(NLIM));
            }
            if (cmd.hasOption(ELIM)) {
                ProcessData.eastingLim = Double.valueOf(cmd.getOptionValue(ELIM));
            }
            if (cmd.hasOption(ELELIM)) {
                ProcessData.elevationLim = Float.valueOf(cmd.getOptionValue(ELELIM));
            }
            if (cmd.hasOption(EXC)) {
                ProcessData.extraComment = true;
            }
            if (rw5FileName == null) throw new ParseException("now arguments specified");
        } catch (ParseException e) {
            formatter.printHelp("java -jar RW5toCSV.jar args", options);
            exit(1);
        } catch (NumberFormatException e) {
            formatter.printHelp(e.getMessage(), options); // todo -- be mor specific !
        }

        List<Rrec> rrecs = RW5Parser.getRrecs(rw5FileName);

        // Process ...
        List<String> csvs = ProcessData.getCSVRecs(rrecs);

        // Write csv file
        BufferedWriter csvOut = new BufferedWriter(new FileWriter(new File(csvFileName)));
        for (String l : csvs) {
            csvOut.write(l + "\n");
        }
        csvOut.close();

        // write txt file if given (ProcessData.getCSVRecs must be called before !)(
        if (logFileName != null) {
            List<String> txts = ProcessData.getCSVRecsWithComment();
            BufferedWriter txtOut = new BufferedWriter(new FileWriter(new File(logFileName)));
            for (String l : txts) {
                txtOut.write(l + "\n");
            }
            txtOut.close();
        }
    }


    private static String getFullPathWithBaseName() {
        return FilenameUtils.getFullPath(rw5FileName) + FilenameUtils.getBaseName(rw5FileName);
    }

}
