Smart Campus SP
===============

# Overview
SmartCampus is a [FI-Ware](https://www.fiware.org/) software prototype to measure the contamination levels in the [University of São Paulo (USP)](https://www5.usp.br/) Campus with a group of Android, Arduino and Galileo sensors. Below you can find a diagram of all the system components and a brief explanation of their functions. The following sections will detail how the components were connected and all the software and hardware installed.

![FI-Beer overview](/img/overview.png)

SmartCampus makes use of several FI-Ware building blocks, all of them deployed in the FI-Lab testbed:
* [Context Broker](http://catalogue.fi-ware.eu/enablers/publishsubscribe-context-broker-orion-context-broker): a data concentrator for all the measures, manages subscriptions and data access. Needs some configuring in order to integrate it with Cosmos through the Cygnus plugin.  
* [Wirecloud](http://catalogue.fi-ware.eu/enablers/application-mashup-wirecloud): Front End based on HTML5+JS widget composing, offers some libraries to integrate with the CB. 
* [Cosmos](http://catalogue.fi-ware.eu/enablers/bigdata-analysis-cosmos): Big Data platform to store and analyze measure data.

# Detailed description

## Polution sensors instrumentation and hardware connection

In the first iteration of the prototype, the system will only manage one magnitude of the polution metrics: temperature, pressure, noise and air particles.

## NGSI Clients

All the heterogeneus sensor reading is send to the Context Broker using the [NGSI Protocol](http://forge.fi-ware.eu/plugins/mediawiki/wiki/fiware/index.php/OMA_NGSI_10).

#### Developing the NGSI Client

The Context Broker, where the measures will be aggregated and distributed, listens for requests following the [NGSI protocol](http://technical.openmobilealliance.org/Technical/release_program/NGSI_v1_0.aspx). In order to communicate with this component, two NGSI clients were developed (python and java). The NGSI protocol defines several resources and operations, but the client only use a very restricted set:

* Appending a new measure for a context
* Getting measures for a particular context

The NGSI protocol is a REST protocol that accepts both XML and JSON payloads. JSON was prefered for this project.

##### Python version

Intented to be used with Arduino and Galileo sensors. All the communications were stablished using the `requests` HTTP library with JSON payloads (the default `json` python library).

##### Java version

Intended to be used with Android OS. For the Java version, the [Jersey API](https://jersey.java.net/) was used.

## The Context Broker

The [Orion Context Broker](https://forge.fi-ware.eu/plugins/mediawiki/wiki/fiware/index.php/Publish/Subscribe_Broker_-_Orion_Context_Broker_-_User_and_Programmers_Guide#Query_Context_operation) was used as the central data node of the system. All the data of the sensors is sent to Orion who, in turn, send it to all its suscribers and can be queried from the frontend systems to get up-to-date information of any of the measures.

For the prototype, we used a dedicated cloud instance in [FI-Lab](http://lab.fi-ware.eu/), that has a preinstalled Context Broker service (the image name is `orion-psb-image-R3.2`). It should start listening on port 1026. Remember to open this port in the Security Group.

## Wirecloud Management Widget
The Front End of the system was designed as a series of widgets deployed in FI-Ware's Mashup Platform: [Wirecloud](http://conwet.fi.upm.es/wirecloud/). The widgets were deployed in the FI-Lab's marketplace and composed in a new workspace.

Three widgets were deployed:
* NGSI Updater
* Map viewer: that consumes data from the Context Broker, showing a map of the campus with realtime information on the temperature, signaling what parts are too cool or too warm.
* Linear graph: Works as a historical data widget, depicting the evolution of the temperature over the last minutes.

![WireCloud overview](/img/wirecloud1.png)

![Wiring overview](/img/wirecloud2.png)

## Connection to Cosmos (HDFS and Hive)

In order to consume the historical data from the widgets, it has to be first stored somewhere. FI-Ware sensor data is stored in the HDFS system of its Big Data GE: [Cosmos](http://catalogue.fi-ware.eu/enablers/bigdata-analysis-cosmos). The data is stored in Cosmos through the use of a script in the Context Broker machine (Cygnus), that is subscribed to the measure update.

## Android client

The Android client was developed in Java. The [Jersey API](https://jersey.java.net/) was used for REST and JSON-related tasks.

![Android client](/img/android.png)
