#!/bin/bash
JAR=target/matchingengine-1.0-SNAPSHOT.jar
MAIN=matchingengine.Main
MARKETLISTENER=matchingengine.utils.MarketListener
KDBPATH=$(QHOME)/m64/q
TP=/Users/alexsilcock/Documents/java-matching-engine/kdb/tick.q
RDB=/Users/alexsilcock/Documents/java-matching-engine/kdb/tick/r.q

all: 
	build run

build: 
	mvn clean package

listen:
	mvn exec:java -Dexec.mainClass=$(MARKETLISTENER)

sendorders:
	mvn exec:java -Dexec.mainClass=$(MAIN)

run: 
	rlwrap -r $(KDBPATH) $(TP) sym &
	rlwrap -r $(KDBPATH) $(RDB) :5010 -p 5011 &
	sleep 2
	mvn exec:java -Dexec.mainClass=$(MARKETLISTENER)
	
	sleep 1

tp: 
	rlwrap -r $(KDBPATH) $(TP) sym &

kdb:
	rlwrap -r $(KDBPATH) $(TP) sym &
	rlwrap -r $(KDBPATH) $(RDB) :5010 -p 5011 &

l2book:
	mvn exec:java -Dexec.mainClass=matchingengine.utils.L2BookWrapper

clean:
	pkill -f $(TP)
	pkill -f $(RDB)
