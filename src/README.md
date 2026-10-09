# Weather Quiz - CIS046-3

Java Swing quiz using Open-Meteo live weather.
Login first (default `Ada` / `hello23`), then answer hotter / over-under questions.

Tomato game from the unit is still in `com.perisic.tomato` for reference. My project is `com.weatherquiz`.

## setup

needs Java 17.

```bat
javac -d bin src/module-info.java src/com/perisic/tomato/engine/*.java src/com/perisic/tomato/peripherals/*.java src/com/weatherquiz/engine/*.java src/com/weatherquiz/peripherals/*.java
copy src\tomato1.png bin\
copy src\tomato2.png bin\
java -cp bin com.weatherquiz.peripherals.QuizLoginGUI
```

tomato version (old): `java -cp bin com.perisic.tomato.peripherals.LoginGUI`

## notes

- weather comes from https://api.open-meteo.com, parsed by hand, no json lib.
- offline fallback uses canned temps if the api is down.
- users are stored hashed (SHA-256 + salt) in `users.txt`.
- session token in `session.cookie`, expires after 30 mins.

based on the TomatoFull example from the unit. see source comments for links used.
