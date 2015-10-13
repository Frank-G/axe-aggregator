[![Build Status](https://travis-ci.org/cloudiator/axe-aggregator.svg?branch=master)](https://travis-ci.org/cloudiator/axe-aggregator)

AXE Aggregator
=====================================

This component is the aggregator service of cloudiator. It runs
on its own, providing a RMI interface to allow the orchestration
of the aggregators.

Prerequisites
=====================================
KairosDB (1.0.0-1)


Installation
=====================================

Step I: Install KairosDB and run it on port 8080 on the same
machine as AXE.

Step II: mvn install


Usage
=====================================

java -jar service/target/axe-aggregator-service-1.2.0-SNAPSHOT-jar-with-dependencies.jar


Documentation
=====================================

Orchestration is done via the Monitoring-API of colosseum.


Contact
=====================================

Frank Griesinger (University of Ulm)
frank.griesinger(at)uni-ulm.de




