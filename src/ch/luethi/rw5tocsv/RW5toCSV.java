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

    private static String rw5FileName, csvFileName, logFileName;

    public static void main(String args[]) throws IOException {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(RW5, "RW5 input file", true, "RW5 file to extract data");
        options.addOption(CSV, "CSV output file", true, "csv file to write data, optional. If not specified the output is <file-name>.csv in the same directory as the input file");
        options.addOption(LOG, "LOG output file", true, "log file to write comments, optional. If not specified the output is <file-name>.log in the same directory as the input file");
        options.addOption(VER, "version of the program", false, "prints the version nummer of the program");

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
            } else if (cmd.hasOption(CSV)) {
                csvFileName = cmd.getOptionValue(CSV);
            } else {
                csvFileName = null;
                csvFileName = getFullPathWithBaseName() + ".csv";
            }
            if (cmd.hasOption(LOG)) {
                logFileName = cmd.getOptionValue(LOG);
            } else {
                logFileName = null;
                logFileName = getFullPathWithBaseName() + ".log";
            }
            if (rw5FileName == null) throw new ParseException("now arguments specified");
        } catch (ParseException e) {
            formatter.printHelp("java -jar RW5toCSV.jar args", options);
            exit(1);
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
