# CountingApplication.java #



## Description of application ##

pojo-mbean sample application.

The application runs until manually stopped.

[@ManagedAttribute](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/annotation/ManagedAttribute.java) methods will be called from multiple threads (application as well as MBean server) and appropriate thread safety precautions must therefore be taken.

These may include:
  * Make the methods synchronized
  * Include synchronized blocks around critical sections of the method
  * Make the fields volatile
  * Use concurrent types, and use them in a thread safe manner (AtomicInteger used in this example)

For more information about concurrency and thread safety, I highly recommend the books [Effective Java](http://books.google.com/books/about/Effective_Java.html?id=ka2VUBqHiWkC), and [Concurrency in Practice](http://books.google.com/books?id=6LpQAAAAMAAJ).

## Application source ##

```
@MBean(objectName = "org.softee:type=Demo,name=CountingApplication")
@Description("This Java application shows how to expose a Read/Write int property and two methods as an MBean")
public class CountingApplication {
    /**
     * The counter that is made visible to both application and Management Agent (JConsole / JMX console)
     */
    private final AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        new CountingApplication().run();
    }

    public Object run() throws Exception {
        /* in a "real" application, the registration instance would probably be
         * saved in a field for subsequent unregistration */
        new MBeanRegistration(this).register();
        for (;;) {
            System.out.println("counter = " + getCounter());
            incrementCounter(1);
            Thread.sleep(1000); // one second
        }
    }

    @ManagedAttribute @Description("A counter variable")
    public int getCounter() {
        return counter.get();
    }

    @ManagedAttribute
    public void setCounter(int counter) {
        this.counter.set(counter);
    }

    @ManagedOperation
    @Description("Increments the counter by the requested amount and shows the resulting value")
    public int incrementCounter(
            @Parameter("amount") @Description("The amount to increment the counter with") int delta) {
        return counter.addAndGet(delta);
    }

    @ManagedOperation @Description("Resets the counter")
    public void reset() {
        counter.set(0);
    }
}
```

_Full source: [CountingApplication.java](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/samples/CountingApplication.java)_

## jconsole screenshots ##

### showing attributes ###

![http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-attributes.png](http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-attributes.png)

### showing counter attribute ###

![http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-counter.png](http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-counter.png)

### showing operations ###

![http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-operations.png](http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-operations.png)

### after calling increment operation ###

![http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-increment.png](http://pojo-mbean.googlecode.com/svn/wiki/img/counting-jconsole-increment.png)

## JMX Console screenshots ##

Soon to be arriving at a browser near you ;-)