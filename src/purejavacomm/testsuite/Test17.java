package purejavacomm.testsuite;

import jtermios.JTermios;
import purejavacomm.*;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by chuck on 1/2/2016.
 */
public class Test17 extends TestBase {
    private static TestSerialPortEventListener eventListener = new TestSerialPortEventListener();

    private static class TestSerialPortEventListener implements SerialPortEventListener {
        public boolean eventReceived = false;

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventType() == SerialPortEvent.PORT_CLOSED) {
                eventReceived = true;
            }
        }
    }

    static void run() throws Exception {
        try {

            begin("Test17 - Port closed handling");

            //JTermios.JTermiosLogging.setLogMask(4);

            try {
                CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(m_TestPortName);
                m_Port = (SerialPort) portid.open(APPLICATION_NAME, 1000);
            } catch (NoSuchPortException e) {
                fail("could no open port '%s'\n", m_TestPortName);
            }

            m_Port.notifyOnPortClosed(true);

            m_Port.addEventListener(eventListener);

            // Tickle the input stream to ensure its up and running
            m_Port.getInputStream().available();

            System.out.println();
            System.out.println("Disconnect/Terminate connection with device now.\nHit Enter To Continue.");
            System.in.read();

            // We want this to throw an IOException, which will indicate that the port is on its way out
            try {
                m_Port.getInputStream().available();
                fail("Input Stream is still responding. Did you disconnect the correct serial line?");
            }
            catch (IOException e) {}

            if (!eventListener.eventReceived) {
                fail("Port lost event never fired!");
            }

            System.out.println("Reconnect the device now. Wait for it to come online.\nHit Enter To Continue.");
            System.in.read();

            try {
                CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(m_TestPortName);
                m_Port = (SerialPort) portid.open(APPLICATION_NAME, 1000);
                // Tickle the input stream to ensure its up and running
                m_Port.getInputStream().available();
            } catch (NoSuchPortException e) {
                fail("could no open port '%s'\n", m_TestPortName);
            }

            finishedOK();
        } finally {
            if (m_Port != null) {
                m_Port.close();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        // Native.setProtected(false);
        TestBase.init(new String[] {"COM3"});
        //jtermios.JTermios.JTermiosLogging.setLogMask(255);
        // System.setProperty("purejavacomm.usepoll", "true");
        // System.setProperty("purejavacomm.rawreadmode", "true");
        try {
            System.out.println("PureJavaComm Test Suite");
            System.out.println("Using port: " + TestBase.getPortName());
            Test17.run();
        } catch (TestBase.TestFailedException e) {
            System.out.println("Test failure");
            System.exit(1);
        }
    }
}
