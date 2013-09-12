camel-akka-example
==================

Enkelt exempel på Akka Camel. HTTP server startas på port 9090 och gör parallella HEAD requests mot ett antal dagstidningars webservrar för att ta reda på vilken typ av webserver de kör.

### Hur man kör

Installera [SBT](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)

```
> sbt run
```

Gå in med en webbläsare på [http://localhost:9090](http://localhost:9090)
