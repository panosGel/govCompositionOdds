# Government Composition Odds

A simple vert.x application that uses the mongoDB client and jsoup

## Building

You build the project using:

```
gradle clean build
```

## Testing

The application is tested using vertx-unit, and junit.

## Packaging

The application is packaged as a _fat jar_, using gradle wrapper and the gradle shadow plugin [https://github.com/johnrengelman/shadow]

To create the fat jar run
```
./gradlew shadowJar
```
## Running

launch mongo in docker
```
docker run -d -p 27017:27017 mongo
```
and then launch the fat jar
```
java -jar build/libs/govCompositionOdds-3.4.1-fat.jar
```

To see the results, open a browser in http://localhost:8080/assets/index.html
