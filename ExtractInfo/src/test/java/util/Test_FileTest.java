package util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class Test_FileTest extends TestCase {
    //TEST

    /** Create the test case
     *  @param testName name of the test case
     */
    public Test_FileTest( String testName ) { super( testName ); }

    /** @return the suite of tests being tested */
    public static Test suite() {
        return new TestSuite(
                Test_FileTest.class
        );
    }

    /**  Rigourous Test :-) */
    public void testApp() { assertTrue( true ); }



}