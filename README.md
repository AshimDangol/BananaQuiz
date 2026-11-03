# Banana Quiz - CIS046-3

Java Swing quiz using the Banana API. Log in first (default `Ada` / `hello23`, Ada is admin), then you get Play banana quiz, Leaderboard and Settings.

## setup

needs Java 17.

```bat
run.bat
```

or by hand:

```bat
javac -d bin src/module-info.java src/com/bananaquiz/engine/*.java src/com/bananaquiz/peripherals/*.java
copy src\banana1.png bin\
java -cp bin com.bananaquiz.peripherals.QuizLoginGUI
```

## notes

- questions come from https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes
- if the api is down the quiz uses the bundled `banana1.png` instead and tags
  the question (offline). that tag can be turned off in Settings
- passwords sit in `~/.bananaquiz/users.txt` hashed (SHA-256 + salt), never as
  plain text
- there's a session token in `~/.bananaquiz/session.cookie` that expires after
  30 mins
- every answer writes itself into `~/.bananaquiz/scores.txt`, so there's no
  save button anymore
- the leaderboard shows everyone the same full list, one row per player
  (their best game), highest score first
- the quiz opens full screen with the leaderboard down the right edge. it
  re-reads scores.txt every 5 seconds so a score saved somewhere else turns
  up without pressing anything, and the Leaderboard window does the same.
  both stop polling when the window closes. you can turn the panel off in
  Settings
- settings are per user in `~/.bananaquiz/settings.txt`: seconds per question
  (10-60), live leaderboard on/off, offline tag on/off. a change only comes
  in from the next quiz

based on the TomatoFull example from the unit. see source comments for links used.

## acknowledgements

layout and event handling started from the TomatoFull example by Marc Conrad (unit
BREO site, https://github.com/marcconrad/comparativeintegratedsystems). password
hashing snippet from https://stackoverflow.com/questions/33036467/sha-256-password-hashing-in-java.
login form layout from https://best-programming-tricks.blogspot.com/2011/07/how-to-make-login-form-with-java-gui.html.
questions and images from the Banana API by Marc Conrad (https://marcconrad.com/uob/banana/doc.php).
some parts of this project were written with AI assistance; each borrowed
piece is marked with a comment in the source file where it is used.
