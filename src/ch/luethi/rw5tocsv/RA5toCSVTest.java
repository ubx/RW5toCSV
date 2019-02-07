package ch.luethi.rw5tocsv;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RA5toCSVTest {

    private static final String THE_FILES_DIFFER = " The files differ!";
    private static final String DIFF_CMD = "meld ";

    private static final String TESTDATA = "testdata";
    private static final String BERNECK_RW5 = TESTDATA + "/BERNECK.rw5";
    private static final String BERNECK_CSV = TESTDATA + "/BERNECK.csv";
    private static final String BERNECK_LOG = TESTDATA + "/BERNECK.log";
    private static final String BERNECK_REF_CSV = TESTDATA + "/BERNECK-ref.csv";
    private static final String BERNECK_REF_LOG = TESTDATA + "/BERNECK-ref.txt";

    private static final String FORMARERROR_RW5 = TESTDATA + "/test-00.rw5";
    private static final String FORMARERROR_CSV = TESTDATA + "/test-00.csv";
    private static final String FORMARERROR_REF_CSV = TESTDATA + "/test-00-ref.csv";

    private static final String FloatingFormatError_RW5 = TESTDATA + "/test-01.rw5";
    private static final String FloatingFormatError_CSV = TESTDATA + "/test-01.csv";
    private static final String FloatingFormatError_REF_CSV = TESTDATA + "/test-01-ref.csv";

    private static final String DriftExceedsLimits_RW5 = TESTDATA + "/P301-12_AARE.rw5";
    private static final String DriftExceedsLimits_CSV = TESTDATA + "/P301-12_AARE.csv";
    private static final String DriftExceedsLimits_LOG = TESTDATA + "/P301-12_AARE.txt";
    private static final String DriftExceedsLimits_REF_LOG = TESTDATA + "/P301-12_AARE-ref.txt";

    private static final String Issue5_RW5 = TESTDATA + "/test-02.rw5";
    private static final String Issue5_CSV = TESTDATA + "/test-02.csv";
    private static final String Issue5_LOG = TESTDATA + "/test-02.txt";
    private static final String Issue5_REF_LOG = TESTDATA + "/test-02-ref.txt";

    private static final String Issue6_RW5 = TESTDATA + "/test-03.rw5";
    private static final String Issue6_CSV = TESTDATA + "/test-03.csv";
    private static final String Issue6_LOG = TESTDATA + "/test-03.txt";
    private static final String Issue6_REF_LOG = TESTDATA + "/test-03-ref.txt";

    private static final String Issue7_RW5 = TESTDATA + "/test-04.rw5";
    private static final String Issue7_CSV = TESTDATA + "/test-04.csv";
    private static final String Issue7_LOG = TESTDATA + "/test-04.txt";
    private static final String Issue7_REF_LOG = TESTDATA + "/test-04-ref.txt";

    private static final String Issue8_RW5 = TESTDATA + "/test-05.rw5";
    private static final String Issue8_CSV = TESTDATA + "/test-05.csv";
    private static final String Issue8_LOG = TESTDATA + "/test-05.txt";
    private static final String Issue8_REF_LOG = TESTDATA + "/test-05-ref.txt";

    private static final String Issue10_RW5 = TESTDATA + "/test-06.rw5";
    private static final String Issue10_CSV = TESTDATA + "/test-06.csv";
    private static final String Issue10_LOG = TESTDATA + "/test-06.txt";
    private static final String Issue10_REF_LOG = TESTDATA + "/test-06-ref.txt";

    private static final String Issue11_RW5 = TESTDATA + "/test-07.rw5";
    private static final String Issue11_CSV = TESTDATA + "/test-07.csv";
    private static final String Issue11_LOG = TESTDATA + "/test-07.txt";
    private static final String Issue11_REF_LOG = TESTDATA + "/test-07-ref.txt";

    private static final String Issue12_RW5 = TESTDATA + "/P316-KIRCHDORF.rw5";
    private static final String Issue12_CSV = TESTDATA + "/P316-KIRCHDORF.csv";
    private static final String Issue12_LOG = TESTDATA + "/P316-KIRCHDORF.txt";
    private static final String Issue12_REF_LOG = TESTDATA + "/P316-KIRCHDORF-ref.txt";

    private static final String Issue12_2_RW5 = TESTDATA + "/P374.rw5";
    private static final String Issue12_2_CSV = TESTDATA + "/P374.csv";
    private static final String Issue12_2_LOG = TESTDATA + "/P374.log";
    private static final String Issue12_2_REF_LOG = TESTDATA + "/P374-ref.log";


    @Test
    void basicTest() throws IOException {
        deleteQuietly(new File(BERNECK_CSV));
        deleteQuietly(new File(BERNECK_LOG));
        RW5toCSV.main(new String[]{"-r", BERNECK_RW5, "-c", BERNECK_CSV, "-l", BERNECK_LOG});
        getaVoid(BERNECK_CSV, BERNECK_REF_CSV);
        getaVoid(BERNECK_LOG, BERNECK_REF_LOG);
    }

    @Test
    void formatErrorTest() throws IOException {
        deleteQuietly(new File(FORMARERROR_CSV));
        RW5toCSV.main(new String[]{"-r", FORMARERROR_RW5, "-c", FORMARERROR_CSV});
        getaVoid(FORMARERROR_CSV, FORMARERROR_REF_CSV);
    }

    @Test
    void FloatingFormatErrorTest() throws IOException {
        deleteQuietly(new File(FloatingFormatError_CSV));
        RW5toCSV.main(new String[]{"-r", FloatingFormatError_RW5, "-c", FloatingFormatError_CSV});
        getaVoid(FloatingFormatError_CSV, FloatingFormatError_REF_CSV);
    }

    @Test
    void DriftExceedsLimitsTest() throws IOException {
        deleteQuietly(new File(DriftExceedsLimits_LOG));
        RW5toCSV.main(new String[]{"-r", DriftExceedsLimits_RW5, "-l", DriftExceedsLimits_LOG, "-c", DriftExceedsLimits_CSV});
        getaVoid(DriftExceedsLimits_LOG, DriftExceedsLimits_REF_LOG);
    }

    @Test
    void issue5_Test() throws IOException {
        deleteQuietly(new File(Issue5_LOG));
        deleteQuietly(new File(Issue5_CSV));
        RW5toCSV.main(new String[]{"-r", Issue5_RW5, "-l", Issue5_LOG, "-c", Issue5_CSV});
        getaVoid(Issue5_LOG, Issue5_REF_LOG);
    }

    @Test
    void issue6_Test() throws IOException {
        deleteQuietly(new File(Issue6_LOG));
        deleteQuietly(new File(Issue6_CSV));
        RW5toCSV.main(new String[]{"-r", Issue6_RW5, "-l", Issue6_LOG, "-c", Issue6_CSV});
        getaVoid(Issue6_LOG, Issue6_REF_LOG);
    }

    @Test
    void issue7_Test() throws IOException {
        deleteQuietly(new File(Issue7_LOG));
        deleteQuietly(new File(Issue7_CSV));
        RW5toCSV.main(new String[]{"-r", Issue7_RW5, "-l", Issue7_LOG, "-c", Issue7_CSV});
        getaVoid(Issue7_LOG, Issue7_REF_LOG);
    }

    @Test
    void issue8_Test() throws IOException {
        deleteQuietly(new File(Issue8_LOG));
        deleteQuietly(new File(Issue8_CSV));
        RW5toCSV.main(new String[]{"-r", Issue8_RW5, "-l", Issue8_LOG, "-c", Issue8_CSV});
        getaVoid(Issue8_LOG, Issue8_REF_LOG);
    }

    @Test
    void issue9_Test() throws IOException {
        deleteQuietly(new File(BERNECK_CSV));
        deleteQuietly(new File(BERNECK_LOG));
        RW5toCSV.main(new String[]{"-r", BERNECK_RW5});
        getaVoid(BERNECK_CSV, BERNECK_REF_CSV);
        getaVoid(BERNECK_LOG, BERNECK_REF_LOG);
    }

    @Test
    void issue10_Test() throws IOException {
        deleteQuietly(new File(Issue10_LOG));
        deleteQuietly(new File(Issue10_CSV));
        RW5toCSV.main(new String[]{"-r", Issue10_RW5, "-l", Issue10_LOG, "-c", Issue10_CSV});
        getaVoid(Issue10_LOG, Issue10_REF_LOG);
    }

    @Test
    void issue11_Test() throws IOException {
        deleteQuietly(new File(Issue11_LOG));
        deleteQuietly(new File(Issue11_CSV));
        RW5toCSV.main(new String[]{"-r", Issue11_RW5, "-l", Issue11_LOG, "-c", Issue11_CSV});
        getaVoid(Issue11_LOG, Issue11_REF_LOG);
    }


    @Test
    void issue12_Test() throws IOException {
        deleteQuietly(new File(Issue12_LOG));
        deleteQuietly(new File(Issue12_CSV));
        RW5toCSV.main(new String[]{"-r", Issue12_RW5, "-l", Issue12_LOG, "-c", Issue12_CSV});
        getaVoid(Issue12_LOG, Issue12_REF_LOG);
    }

    @Test
    void issue12_2_Test() throws IOException {
        deleteQuietly(new File(Issue12_2_LOG));
        deleteQuietly(new File(Issue12_2_CSV));
        RW5toCSV.main(new String[]{"-r", Issue12_2_RW5, "-l", Issue12_2_LOG, "-c", Issue12_2_CSV});
        getaVoid(Issue12_2_LOG, Issue12_2_REF_LOG);
    }


    private void getaVoid(String file, String file2) throws IOException {
        assertTrue(contentEquals(new File(file), new File(file2)), DIFF_CMD + file + " " + file2 + THE_FILES_DIFFER);
    }

}
