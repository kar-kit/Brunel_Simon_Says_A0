# Simon Says — SwiftBot

Simon Says played on a [SwiftBot](https://www.swiftbot.co.uk/) robot: the underlights flash a growing colour sequence, you repeat it on the physical buttons, and the game ends the first time you get one wrong.

**Stack** — Java · SwiftBot API · `java.util.concurrent`

Coursework, Brunel University, December 2023.

---

## What it demonstrates

**Turning a callback API into a blocking one.**
The SwiftBot API is event-driven — `enableButton` registers a handler and returns immediately — but Simon Says needs to *wait* for the next press. `ButtonHandler.getFirstPressed()` bridges the two models: it resets all four button states, registers handlers that flip a flag, then polls until one is set and returns which. Shared state between the callback threads and the game loop is held in a `ConcurrentHashMap`, because a plain `HashMap` read while a hardware callback writes to it is a race.

**Runtime binding instead of hardcoding.**
Colours aren't assigned to buttons at compile time. On startup the robot strobes each colour and waits for a press, building the `Button → colour` mapping from whatever the player chooses. The game logic then works off the map and never needs to know which physical button is red.

**Hardware timing you actually have to think about.**
`strobe()` skips the trailing sleep on the final repetition — a small thing, but it's the difference between a light pattern that feels responsive and one that lags a beat behind every input.

## Running it

Requires SwiftBot hardware and the `swiftbot` API on the classpath.

`simonSays.java` holds both public classes as submitted, so javac needs them split into `SimonSays.java` and `ButtonHandler.java` first — Java allows one public class per file, named to match:

```bash
javac -cp swiftbot.jar SimonSays.java ButtonHandler.java
java  -cp .:swiftbot.jar SimonSays
```

---

**Scope, stated plainly:** 114 lines, one sitting, an introductory assignment. It's here as evidence of comfort with Java, concurrency primitives and embedded/hardware APIs — not as a portfolio piece. The substantial work is in [SticksNBoulders](https://github.com/kar-kit/SticksNBoulders) and [MemoAI](https://github.com/kar-kit/MemoAI).
