# Matching Engine
Attempting to create a simple OrderBook and Order Matching Engine, following to an extent [Jane Street - How to build an exchange](https://blog.janestreet.com/how-to-build-an-exchange/)

### Architecture
![Architecture](images/architecture.png)

### Usage
1. Build
```
make build
```

2. Run the KDB TP and RDB
```
make kdb
```

3. Run the MarketListener
```
make listen
```

4. Send orders
```
make sendorders
```
