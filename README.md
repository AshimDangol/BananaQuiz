# Banana Quiz - CIS046-3

Java Swing quiz using the Banana API. Login first (default `Ada` / `hello23`, Ada is admin), then Play banana quiz, Leaderboard or Settings.

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

- banana questions come from https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes.
- offline fallback uses `banana1.png` if the api is down, tagged (offline).
- users stored hashed (SHA-256 + salt) in `~/.bananaquiz/users.txt`.
- session token in `~/.bananaquiz/session.cookie`, expires after 30 mins.
- scores in `~/.bananaquiz/scores.txt`.
- leaderboard: normal users see own scores, Ada (admin) sees all.
- the quiz opens full screen with a live leaderboard down the right edge,
  which polls `scores.txt` every 5s so a score saved elsewhere appears on
  its own. turn it off in Settings.
- settings are per user in `~/.bananaquiz/settings.txt`: seconds per question
  (10-60), live leaderboard on/off, offline tag on/off. saved changes apply
  from the next quiz.

based on the TomatoFull example from the unit. see source comments for links used.

## acknowledgements

layout and event handling started from the TomatoFull example by Marc Conrad (unit
BREO site, https://github.com/marcconrad/comparativeintegratedsystems). password
hashing snippet from https://stackoverflow.com/questions/33036467/sha-256-password-hashing-in-java.
login form layout from https://best-programming-tricks.blogspot.com/2011/07/how-to-make-login-form-with-java-gui.html.
questions and images from the Banana API by Marc Conrad (https://marcconrad.com/uob/banana/doc.php).
some parts of this project were written with AI assistance; each borrowed or
AI-assisted piece is marked with a comment in the source file where it is used.
