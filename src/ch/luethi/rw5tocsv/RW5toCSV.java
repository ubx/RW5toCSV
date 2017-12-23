package ch.luethi.rw5tocsv;

import java.io.*;
import java.util.List;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import static java.lang.System.exit;

public class RW5toCSV {

    private static final String RW5 = "r";
    private static final String CSV = "c";
    private static final String TXT = "t";

    private static String rw5FileName, csvFileName, txtFileName;

    public static void main(String args[]) throws IOException {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addRequiredOption(RW5, "RW5 input file", true, "RW5 file to extract data");
        options.addOption(CSV, "CSV output file", true, "csv file to write data, optional. If not specified the output is <file-name>.csv in the same directory as the input file");
        options.addOption(TXT, "TXT output file", true, "text file to write comments, optional. If not specified the output is <file-name>.txt in the same directory as the input file");

        CommandLine cmd;

        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(200);
        try {
            cmd = parser.parse(options, args);
            rw5FileName = cmd.getOptionValue(RW5);
            csvFileName = cmd.getOptionValue(CSV);
            if (cmd.hasOption(CSV)) {
                csvFileName = cmd.getOptionValue(CSV);
            } else {
                csvFileName = null;
                csvFileName = getFullPathWithBaseName() + ".csv";
            }
            if (cmd.hasOption(TXT)) {
                txtFileName = cmd.getOptionValue(TXT);
            } else {
                txtFileName = null;
                txtFileName = getFullPathWithBaseName() + ".txt";
            }
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
        if (txtFileName != null) {
            List<String> txts = ProcessData.getCSVRecsWithComment();
            BufferedWriter txtOut = new BufferedWriter(new FileWriter(new File(txtFileName)));
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
