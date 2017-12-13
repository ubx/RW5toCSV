package ch.luethi.rw5tocsv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RA5toCSVTest {

    private static final String THE_FILES_DIFFER = " The files differ!";

    private static final String TESTDATA = "testdata";
    private static final String BERNECK_RW5 = TESTDATA + "/BERNECK.rw5";
    private static final String BERNECK_CSV = TESTDATA + "/BERNECK.csv";
    private static final String BERNECK_TXT = TESTDATA + "/BERNECK.txt";
    private static final String BERNECK_REF_CSV = TESTDATA + "/BERNECK-ref.csv";
    private static final String BERNECK_REF_TXT = TESTDATA + "/BERNECK-ref.txt";

    private static final String FORMARERROR_RW5 = TESTDATA + "/test-00.rw5";
    private static final String FORMARERROR_CSV = TESTDATA + "/test-00.csv";
    private static final String FORMARERROR_REF_CSV = TESTDATA + "/test-00-ref.csv";

    private static final String FloatingFormatError_RW5 = TESTDATA + "/test-01.rw5";
    private static final String FloatingFormatError_CSV = TESTDATA + "/test-01.csv";
    private static final String FloatingFormatError_REF_CSV = TESTDATA + "/test-01-ref.csv";

    private static final String DriftExceedsLimits_RW5 = TESTDATA + "/P301-12_AARE.rw5";
    private static final String DriftExceedsLimits_CSV = TESTDATA + "/P301-12_AARE.csv";
    private static final String DriftExceedsLimits_TXT = TESTDATA + "/P301-12_AARE.txt";
    private static final String DriftExceedsLimits_REF_TXT = TESTDATA + "/P301-12_AARE-ref.txt";

    private static final String Issue5_RW5 = TESTDATA + "/test-02.rw5";
    private static final String Issue5_CSV = TESTDATA + "/test-02.csv";
    private static final String Issue5_TXT = TESTDATA + "/test-02.txt";
    private static final String Issue5_REF_TXT = TESTDATA + "/test-02-ref.txt";

    private static final String Issue6_RW5 = TESTDATA + "/test-03.rw5";
    private static final String Issue6_CSV = TESTDATA + "/test-03.csv";
    private static final String Issue6_TXT = TESTDATA + "/test-03.txt";
    private static final String Issue6_REF_TXT = TESTDATA + "/test-03-ref.txt";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void basicTest() throws IOException {
        deleteQuietly(new File(BERNECK_CSV));
        deleteQuietly(new File(BERNECK_TXT));
        RA5toCSV.main(new String[]{"-r", BERNECK_RW5, "-c", BERNECK_CSV, "-t", BERNECK_TXT});
        getaVoid(BERNECK_CSV, BERNECK_REF_CSV);
        getaVoid(BERNECK_TXT, BERNECK_REF_TXT);
    }

    @Test
    void formatErrorTest() throws IOException {
        deleteQuietly(new File(FORMARERROR_CSV));
        RA5toCSV.main(new String[]{"-r", FORMARERROR_RW5, "-c", FORMARERROR_CSV});
        getaVoid(FORMARERROR_CSV, FORMARERROR_REF_CSV);
    }

    @Test
    void FloatingFormatErrorTest() throws IOException {
        deleteQuietly(new File(FloatingFormatError_CSV));
        RA5toCSV.main(new String[]{"-r", FloatingFormatError_RW5, "-c", FloatingFormatError_CSV});
        getaVoid(FloatingFormatError_CSV, FloatingFormatError_REF_CSV);
    }

    @Test
    void DriftExceedsLimitsTest() throws IOException {
        deleteQuietly(new File(DriftExceedsLimits_TXT));
        RA5toCSV.main(new String[]{"-r", DriftExceedsLimits_RW5, "-t", DriftExceedsLimits_TXT, "-c", DriftExceedsLimits_CSV});
        getaVoid(DriftExceedsLimits_TXT, DriftExceedsLimits_REF_TXT);
    }

    @Test
    void issue5_Test() throws IOException {
        deleteQuietly(new File(Issue5_TXT));
        deleteQuietly(new File(Issue5_CSV));
        RA5toCSV.main(new String[]{"-r", Issue5_RW5, "-t", Issue5_TXT, "-c", Issue5_CSV});
        getaVoid(Issue5_TXT, Issue5_REF_TXT);
    }

    @Test
    void issue6_Test() throws IOException {
        deleteQuietly(new File(Issue6_TXT));
        deleteQuietly(new File(Issue6_CSV));
        RA5toCSV.main(new String[]{"-r", Issue6_RW5, "-t", Issue6_TXT, "-c", Issue6_CSV});
        getaVoid(Issue6_TXT, Issue6_REF_TXT);
    }

    private void getaVoid(String file, String file2) throws IOException {
        assertTrue(contentEquals(new File(file), new File(file2)), file + "," + file2 + THE_FILES_DIFFER);
    }

}
