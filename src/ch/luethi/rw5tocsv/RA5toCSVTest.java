package ch.luethi.rw5tocsv;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class RA5toCSVTest {

    private static final String TESTDATA = "testdata";
    private static final String BERNECK_RW5 = TESTDATA + "/BERNECK.rw5";
    private static final String BERNECK_CSV = TESTDATA + "/BERNECK.csv";
    private static final String BERNECK_TXT = TESTDATA + "/BERNECK.txt";
    private static final String BERNECK_REF_CSV = TESTDATA + "/BERNECK-ref.csv";
    private static final String BERNECK_REF_TXT = TESTDATA + "/BERNECK-ref.txt";

    private static final String TEST_CSV = TESTDATA + "/testA.csv";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void basicTest() throws IOException {
        forceDelete(new File(BERNECK_CSV));
        forceDelete(new File(BERNECK_TXT));
        RA5toCSV.main(new String[]{"-r", BERNECK_RW5, "-c", BERNECK_CSV, "-t", BERNECK_TXT});
        assertTrue(contentEquals(new File(BERNECK_CSV), new File(BERNECK_REF_CSV)), "The files differ!");
        assertTrue(contentEquals(new File(BERNECK_TXT), new File(BERNECK_REF_TXT)), "The files differ!");
    }

}