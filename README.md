# Banana Quiz - CIS046-3

Java Swing quiz using the Banana API. Login first (default `Ada` / `hello23`, Ada is admin), then Play banana quiz or Leaderboard.

## setup

needs Java 17.

```bat
run.bat
```

or by hand:

```bat
javac -d bin src/module-info.java src/com/weatherquiz/engine/*.java src/com/weatherquiz/peripherals/*.java
copy src\banana1.png bin\
java -cp bin com.weatherquiz.peripherals.QuizLoginGUI
```

## notes

- banana questions come from https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes.
- offline fallback uses `banana1.png` if the api is down, tagged (offline).
- users stored hashed (SHA-256 + salt) in `~/.weatherquiz/users.txt`.
- session token in `~/.weatherquiz/session.cookie`, expires after 30 mins.
- scores in `~/.weatherquiz/scores.txt`.
- leaderboard: normal users see own scores, Ada (admin) sees all.

based on the TomatoFull example from the unit. see source comments for links used.
