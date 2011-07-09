package org.softee.management;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.softee.management.annotation.ManagedOperation;
import org.softee.management.annotation.ManagedAttribute;
import org.softee.management.exception.ManagementException;
import org.softee.util.Preconditions;

/**
 * An abstract base-class for generating Pojo MBeans that are capable of registering and unregistering themselves.
 *
 * Subsclasses should include an @MBean annotation, specifying the domain and type attribute
 *
 * @author morten.hattesen@gmail.com
 *
 */
public abstract class AbstractPojoMBean {
    protected static final long NONE = Long.MIN_VALUE;
    private DatatypeFactory dtf;
    private AtomicLong started;
    protected final MBeanRegistration registration;


    /**
     * Construct an MBean using the objectName attribute of the @MBean annotation
     * @param mbeanName the name property of the ObjectName with which to override the name from the @MBean annotation
     * @throws MalformedObjectNameException if either domain, type or name is not specified
     */
    public AbstractPojoMBean(String mbeanName) throws MalformedObjectNameException {
        ObjectName baseName = ObjectNameFactory.createObjectName(this);
        ObjectName objectName = ObjectNameFactory.replaceProperty(baseName, "name", Preconditions.notNull(mbeanName));
        registration = new MBeanRegistration(this, objectName);
        initialize();
    }

    /**
     * Construct an MBean using the objectName attribute of the @MBean annotation
     * @throws MalformedObjectNameException if either domain, type or name is not specified
     */
    public AbstractPojoMBean() throws MalformedObjectNameException {
        registration = new MBeanRegistration(this, ObjectNameFactory.createObjectName(this));
        initialize();
    }

    /**
     * @param type the type (class) of the MBean. The parent package is used as the domain of the MBean
     * @param name the name of the MBean
     * @throws MalformedObjectNameException
     */
    public AbstractPojoMBean(ObjectName objectName) {
        registration = new MBeanRegistration(this, objectName);
        initialize();
    }

    protected void initialize() {
        try {
            dtf = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        // Reset all stats
        reset();
    }

    @ManagedOperation(value = "Reset the MBean", impact = ManagedOperation.Impact.ACTION)
    public void reset() {
        // Nothing to reset - shouldn't reset the start time
    }

    /**
     * Start monitoring and register the MBean with the MBean server
     * @throws ManagementException
     */
    public void start() throws ManagementException {
        started = new AtomicLong(now());
        registration.register();
    }

    /**
     * Stop monitoring and unregister the MBean with the MBean server
     * @throws ManagementException
     */
    public void stop() throws ManagementException {
        registration.unregister();
    }


    @ManagedAttribute("The time when the monitor was started")
    public String getStarted() {
        return dateString(noneAsNull(started));
    }

    protected Long noneAsNull(AtomicLong a) {
        long n = a.get();
        return n == NONE ? null : n;
    }

    protected Long age(Long millis, TimeUnit unit) {
        return millis == null ? null : unit.convert(now() - millis, MILLISECONDS);
    }

    /**
     * @param millis
     * @return millis formatted as an ISO 8601 (XML datetime) string
     */
    protected String dateString(Long millis) {
        if (millis == null) {
            return null;
        }
        return date(millis).toXMLFormat();
    }

    /**
     * XMLGregorianCalendar used because it provides a standard formatting (ISO 8601) and timezone handling
     * @param millis
     * @return an XMLGregorianCalendar in the current timezone.<p>
     * TODO consider forcing TimeZone to UTC
     */
    protected XMLGregorianCalendar date(Long millis) {
        if (millis == null) {
            return null;
        }
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTimeInMillis(millis);
        return dtf.newXMLGregorianCalendar(gCal);
    }

    protected long now() {
        return System.currentTimeMillis();
    }

    protected AtomicLong zero() {
        return new AtomicLong();
    }

    protected AtomicLong none() {
        return new AtomicLong(NONE);
    }


}
