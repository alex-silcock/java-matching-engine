# Matching Engine
Attempting to create a simple OrderBook and Order Matching Engine, following to an extent [Jane Street - How to build an exchange](https://blog.janestreet.com/how-to-build-an-exchange/)


### Usage
To run the MarketListener
```
mvn exec:java -Dexec.mainClass="matchingengine.utils.MarketListener"
```

To send orders
```
mvn exec:java -Dexec.mainClass="matchingengine.Main"
```