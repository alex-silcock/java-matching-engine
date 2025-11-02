#!/bin/bash
JAR=target/matchingengine-1.0-SNAPSHOT.jar
MAIN=matchingengine.Main
MARKETLISTENER=matchingengine.utils.MarketListener
KDBPATH=/Users/alexsilcock/q/m64/q
KDBDIR=/Users/alexsilcock/Documents/java-matching-engine/kdb

all: 
	build run

build: 
	mvn clean package && java -jar target/matchingengine-1.0-SNAPSHOT.jar

run: 
	rlwrap -r $(KDBPATH) $(KDBDIR)/tick.q sym &
	rlwrap -r $(KDBPATH) $(KDBDIR)/tick/r.q :5010 -p 5011 &
	sleep 2
	mvn exec:java -Dexec.mainClass=$(MARKETLISTENER)
	
	sleep 1

tp: 
	rlwrap -r $(KDBPATH) $(KDBDIR)/tick.q sym &


clean:
	pkill -f $(KDBDIR)/tick.q
	pkill -f $(KDBDIR)/tick/r.q
