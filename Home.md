# Wiki Home #



## Overview ##

The _pojo-mbean_ project was created to provide an easy-to-use method of exposing plain old Java beans (POJO's) as MBean's.

_pojo-mbean_ provides a very lightweight annotation-based MBean specification and deployment on Java SE 5 platform (onwards).

pojo-mbean has **no dependencies** on any external libraries, and may be deployed on any JRE 5.0 (or later) based systems.

## Design ##

The design of pojo-mbean is quite simple.

A number of annotations allow a POJO (Plain Old Java Object) classe to be marked as an [MBean](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/annotation/MBean.java) and its [attributes](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/annotation/ManagedAttribute.java), [operations](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/annotation/ManagedOperation.java) and [parameters](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/annotation/Parameter.java) to be marked and [described](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/annotation/Description.java).

A (moderately complex) [IntrospectedDynamicMBean](http://code.google.com/p/pojo-mbean/source/browse/trunk/src/main/java/org/softee/management/helper/IntrospectedDynamicMBean.java) class then introspects the annotated POJO bean, and exposes it as a [DynamicMBean](http://download.oracle.com/javase/6/docs/api/javax/management/DynamicMBean.html), allowing it to be registered with an MBean server. Similar to how the [javax.management.StandardMBean](http://download.oracle.com/javase/6/docs/api/javax/management/StandardMBean.html) exposes a traditional MBean/MXBean implementation+interface pair as a [DynamicMBean](http://download.oracle.com/javase/6/docs/api/javax/management/DynamicMBean.html)

Additionally, a number of helper classes for ObjectName construction and MBean registration and a number of sample MBean implementations are included.

## Samples ##

  * **[CountingApplication.java](CountingApplication.md)** - serves as an example of how little is required to expose a class as a Management Bean (MBean).
  * Many more to come...

## JMX 2.0 and JSR-255 ##

The JMX 2.0 API specification may provide something similar in terms of ease-of-use and short learning curve to _pojo-mbean_ at some point in the future.

To learn about the JMX 2.0 API ([JSR 255](http://jcp.org/en/jsr/detail?id=255)) have a look at the JMX Spec Lead Eamonn McManus' [post on java.net](http://weblogs.java.net/blog/emcmanus/archive/2007/08/defining_mbeans.html).

But while the JMX 2.0 was originally scheduled for inclusion JDK 6.0, it clearly didn't make that milestone, and it isn't going to make it into JDK 7.0 either. The current target ([according to the spec lead](http://weblogs.java.net/blog/emcmanus/archive/2009/06/jsr_255_jmx_api.html)) is now JDK 8.0. However, the most recent update on the [JSR-255  updates section](http://jcp.org/en/jsr/detail?id=255#updates) is:
> _2006.01.20: This JSR is now scheduled for Dolphin (Java SE 7) and not Mustang_
- and the JSR 255 status is marked as _inactive_. So it's clear that JSR 255 is not getting a lot of attention these days.

## Build and Deployment ##

See the [Downloads](Downloads.md) section for information about build and downloading

## Project Member's Section ##

Here are the instructions for build/deployment setup at Sonatype, who are host ing build artifacts for this project: https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide