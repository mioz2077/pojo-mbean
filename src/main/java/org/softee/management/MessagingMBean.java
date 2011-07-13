package org.softee.management;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.xml.datatype.XMLGregorianCalendar;

import org.softee.management.annotation.Description;
import org.softee.management.annotation.MBean;
import org.softee.management.annotation.ManagedAttribute;
import org.softee.management.annotation.ManagedOperation;
import org.softee.management.annotation.ManagedOperation.Impact;
import org.softee.management.exception.ManagementException;

/**
 * Sample class for implementing commonly monitored metrics in a message processing system.<p>
 * This class may be extended, and new metrics (attributes and operations) may be added by applying annotations to the subclass
 *
 * @author morten.hattesen@gmail.com
 */
@MBean(objectName = "org.softee:type=org.softee.MessagingMBean,name=Default")
@Description("Generic MBean for monitoring input/output processing")
public class MessagingMBean extends AbstractMBean {

    private AtomicLong inputCount;
    private AtomicLong inputLatest;

    private AtomicLong outputCount;
    private AtomicLong outputLatest;

    private AtomicLong durationLastMillis;
    private AtomicLong durationTotalMillis;
    private AtomicLong durationMaxMillis;
    private AtomicLong durationMinMillis;

    private AtomicLong failedCount;
    private AtomicLong failedLatest;
    private Throwable failedLatestCause;

    public MessagingMBean() throws MalformedObjectNameException {
        super();
    }

    public MessagingMBean(String name) throws MalformedObjectNameException {
        super(name);
    }

    public MessagingMBean(ObjectName objectName) {
        super(objectName);
    }

    /**
     * Notify that a message has been input, and processing will begin
     */
    public synchronized void notifyInput() {
        inputCount.incrementAndGet();
        inputLatest.set(now());
    }

    /**
     * Notify that a message has been successfully processed (output), and automatically set the processing duration
     * to the duration since the most recent call to {@link #notifyInput()}.
     * The duration will be invalid this MBean is notified from multiple threads.
     */
    public synchronized void notifyOutput() {
        long latest = inputLatest.get();
        if (latest == NONE) {
            /* This can only be caused by...
             * 1. notifyStop() without preceding notifyStart()
             * 2. a call to reset() since last notifyStart()
             */
            notifyOutput(-1);
        } else {
            notifyOutput(now() - latest);
        }
    }

    /**
     * Notify that a message has been successfully processed (output)
     * @param durationMillis The duration of the processing in milliseconds
     */
    public synchronized void notifyOutput(long durationMillis) {
        outputLatest.set(now());
        outputCount.incrementAndGet();

        if (durationMillis >= 0) {
            durationLastMillis.set(durationMillis);
            durationTotalMillis.addAndGet(durationMillis);

            Long minMillis = getDurationMinMillis();
            if ((minMillis == null) || (minMillis != null && durationMillis < minMillis)) {
                durationMinMillis.set(durationMillis);
            }

            Long maxMillis = getDurationMaxMillis();
            if ((maxMillis == null) || (durationMillis > maxMillis)) {
                durationMaxMillis.set(durationMillis);
            }
        }
    }

    /**
     * Notify that the processing of a message has failed - no cause
     */
    public synchronized void notifyFailed() {
        notifyFailed(null);
    }

    /**
     * Notify that the processing of a message has failed
     * @param cause The cause of the failure, or null if no cause is available
     */
    public synchronized void notifyFailed(Throwable cause) {
        failedCount.incrementAndGet();
        failedLatest.set(now());
        failedLatestCause = cause;
    }

    /**
     * Used for debugging purposes, performs reregistering of MBean allowing remote debugging to take effect
     * @throws ManagementException
     */
    // @Operation("Unregister and then register the MXBean monitor")
    public void reregisterMonitor() throws ManagementException {
        registration.unregister();
        registration.register();
    }

    @Override
    @ManagedOperation(Impact.ACTION)
    @Description("Reset this MBean's metrics")
    public synchronized void resetMBean() {
        super.resetMBean();
        inputLatest = none();
        outputLatest = none();
        failedLatest = none();
        failedLatestCause = null;
        inputCount = zero();
        outputCount = zero();
        failedCount = zero();
        durationLastMillis = none();
        durationMinMillis = none();
        durationMaxMillis = none();
        durationTotalMillis = zero();
    }


    @ManagedAttribute @Description("Number of messages received")
    public long getInputCount() {
        return inputCount.get();
    }

    @ManagedAttribute @Description("Time of last received message")
    public XMLGregorianCalendar getInputLatest() {
        return date(noneAsNull(inputLatest));
    }

    @ManagedAttribute @Description("Time since latest received message (seconds)")
    public Long getInputLatestAgeSeconds() {
        return age(noneAsNull(inputLatest), SECONDS);
    }

    @ManagedAttribute @Description("Number of processed messages")
    public long getOutputCount() {
        return outputCount.get();
    }

    @ManagedAttribute @Description("Time of the latest processed message")
    public XMLGregorianCalendar getOutputLatest() {
        return date(noneAsNull(outputLatest));
   }

    @ManagedAttribute @Description("Time since latest processed message (seconds)")
    public Long getOutputLatestAgeSeconds() {
        return age(noneAsNull(outputLatest), SECONDS);
    }

    @ManagedAttribute @Description("Processing time of the latest message (ms)")
    public Long getDurationLatestMillis() {
        return noneAsNull(durationLastMillis);
    }

    @ManagedAttribute @Description("Total processing time of all messages (ms)")
    public long getDurationTotalMillis() {
        return durationTotalMillis.get();
    }

    @ManagedAttribute @Description("Average processing time (ms)")
    public Long getDurationAverageMillis() {
        long processedDurationTotalMillis = getDurationTotalMillis();
        long processedCount = getOutputCount();
        return (processedCount != 0) ? processedDurationTotalMillis/processedCount : null;
    }

    @ManagedAttribute @Description("Min processing time (ms)")
    public Long getDurationMinMillis() {
        return noneAsNull(durationMinMillis);
    }

    @ManagedAttribute @Description("Max processing time (ms)")
    public Long getDurationMaxMillis() {
        return noneAsNull(durationMaxMillis);
    }

    @ManagedAttribute @Description("Number of processes that failed")
    public long getFailedCount() {
        return failedCount.get();
    }

    @ManagedAttribute @Description("Time of the latest failed message processing")
    public XMLGregorianCalendar getFailedLatest() {
        return date(noneAsNull(failedLatest));
    }

    @ManagedAttribute @Description("Time since latest failed message processing (seconds)")
    public Long getFailedLatestAgeSeconds() {
        return age(noneAsNull(failedLatest), SECONDS);
    }

    @ManagedAttribute @Description("The failure reason of the latest failed message processing")
    public String getFailedLatestReason() {
        if (failedLatestCause == null) {
            return null;
        }
        return failedLatestCause.toString();
    }

    /**
     * TODO Presentation of multi-line content isn't elegant. But in the JConsole, by double-clocking on a String array,
     * the elements will be presented one-per-line.<p>
     * If required, look into presenting in a composite or tabular form (see {@link javax.management.MXBean}
     */
    @ManagedAttribute @Description("The failure stacktrace of the latest failed message processing (one line per element)")
    public String[] getFailedLatestStacktrace() {
        if (failedLatestCause == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        failedLatestCause.printStackTrace(new PrintWriter(sw));
        ArrayList<String> lines = new ArrayList<String>(1000);
        BufferedReader reader = new BufferedReader(new StringReader(sw.toString()));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ignore) {
            // Impossible
        }
        return lines.toArray(new String[lines.size()]);
    }
}
