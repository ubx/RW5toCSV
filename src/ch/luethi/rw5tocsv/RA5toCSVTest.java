package ch.luethi.rw5tocsv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RA5toCSVTest {

    private static final String THE_FILES_DIFFER = "The files differ!";

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

    private static final String Test002_RW5 = TESTDATA + "/test-02.rw5";
    private static final String Test002_CSV = TESTDATA + "/test-02.csv";
    private static final String Test002_TXT = TESTDATA + "/test-02.txt";
    private static final String Test002_REF_TXT = TESTDATA + "/test-02-ref.txt";

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
        assertTrue(contentEquals(new File(BERNECK_CSV), new File(BERNECK_REF_CSV)), THE_FILES_DIFFER);
        assertTrue(contentEquals(new File(BERNECK_TXT), new File(BERNECK_REF_TXT)), THE_FILES_DIFFER);
    }

    @Test
    void formatErrorTest() throws IOException {
        deleteQuietly(new File(FORMARERROR_CSV));
        RA5toCSV.main(new String[]{"-r", FORMARERROR_RW5, "-c", FORMARERROR_CSV});
        assertTrue(contentEquals(new File(FORMARERROR_CSV), new File(FORMARERROR_REF_CSV)), THE_FILES_DIFFER);
    }

    @Test
    void FloatingFormatErrorTest() throws IOException {
        deleteQuietly(new File(FloatingFormatError_CSV));
        RA5toCSV.main(new String[]{"-r", FloatingFormatError_RW5, "-c", FloatingFormatError_CSV});
        assertTrue(contentEquals(new File(FloatingFormatError_CSV), new File(FloatingFormatError_REF_CSV)), THE_FILES_DIFFER);
    }

    @Test
    void DriftExceedsLimitsTest() throws IOException {
        deleteQuietly(new File(DriftExceedsLimits_TXT));
        RA5toCSV.main(new String[]{"-r", DriftExceedsLimits_RW5, "-t", DriftExceedsLimits_TXT, "-c", DriftExceedsLimits_CSV});
        assertTrue(contentEquals(new File(DriftExceedsLimits_TXT), new File(DriftExceedsLimits_REF_TXT)), THE_FILES_DIFFER);
    }

    @Test
    void issue5_Test() throws IOException {
        deleteQuietly(new File(Test002_TXT));
        deleteQuietly(new File(Test002_CSV));
        RA5toCSV.main(new String[]{"-r", Test002_RW5, "-t", Test002_TXT, "-c",Test002_CSV });
        assertTrue(contentEquals(new File(Test002_TXT), new File(Test002_REF_TXT)), THE_FILES_DIFFER);
    }

}
