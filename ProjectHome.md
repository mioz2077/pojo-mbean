# pojo-mbean #
<table align='right'><tr><td></td></tr></table>

_pojo-mbean_ provides an easy way of MBean enabling your code, allowing it to be monitored and controlled using Java Management Extension (JMX) clients such as [JConsole](http://download.oracle.com/javase/6/docs/technotes/guides/management/jconsole.html), [VisualVM](http://visualvm.java.net/) and Java EE web clients, such as the [JBoss JMX Console](http://docs.jboss.org/jbossas/docs/Server_Configuration_Guide/4/html/Connecting_to_the_JMX_Server-Inspecting_the_Server___the_JMX_Console_Web_Application.html).

No need to implement an MBean or MXBean interface at all. Simply annotate your Java class with @MBean,  @ManagedAttribute and @ManagedOperation annotations, add a single line of Java code to register the MBean with the MBean server and you have a fully functional MBean.

Several ready-to-use sample MBean implementations are also supplied.

See the [Wiki Home](Home.md) for further information.

An associated [pojo-mbean discussion group](http://groups.google.com/group/pojo-mbean) has been created on Google Groups. Feel free to ask questions about anything relating to pojo-mbean and MBeans in general.

<font color='green'>Update:</font>**Build artifacts (jar, sources.jar and javadoc.jar) are now available through Maven Central repository, thanks to Anders.**

### Binding MBeans in Guice ###

Pojo-MBeans can be easily wired using Guice. See [Brian Oxley's blog post](http://binkley.blogspot.it/2012/10/jsr-255-jmx2-and-guice.html)