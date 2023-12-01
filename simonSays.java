import swiftbot.*;

import java.util.concurrent.ConcurrentHashMap;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.Map;
import java.io.File;

public class SimonSays {
    private static SwiftBotAPI bot;
    private static int[][] colours = {{255, 0, 0}, {0, 0, 255}, {0, 255, 0}, {255, 0, 255}};
    private static HashMap<Button, int[]> buttonColorBindings = new HashMap<>();
    private static ArrayList<int[]> colorSequence = new ArrayList<>();
    private static Random random = new Random();


    public static void main(String[] args) throws InterruptedException {
        bot = new SwiftBotAPI();
        ButtonHandler buttons = new ButtonHandler(bot);

        // bind colours to the buttons
        for (int[] colour : colours) {
            strobe(colour, 50, 7);
            buttonColorBindings.put(buttons.getFirstPressed(), colour);
        }

        // little light show to say its complete
        for (int[] colour : colours) {
            strobe(colour, 15, 4);
        }

        // pause before the game starts
        Thread.sleep(1500);
        colorSequence.add(colours[random.nextInt(colours.length)]);

        boolean playing = true;
        while (playing) {
            strobe(colorSequence.get(colorSequence.size() - 1), 400, 1);
            for (int[] colour : colorSequence) {
                Button button = buttons.getFirstPressed();
                if (buttonColorBindings.get(button) != colour) {
                    playing = false;
                    break;
                } else {
                    // short successful green strobe
                    strobe(new int[]{0, 0, 70}, 50, 2);
                }
            }
            // add a new colour to the sequence
            colorSequence.add(colours[random.nextInt(colours.length)]);
            Thread.sleep(400);
        }

        // red "game over" strobe
        strobe(new int[]{255, 0, 0}, 100, 8);
        System.exit(0);
    }

    public static void strobe(int[] colour, int duration, int timesToRepeat) throws InterruptedException {
        for (int i=0; i<timesToRepeat; i++) {
            bot.fillUnderlights(colour);
            Thread.sleep(duration);
            bot.disableUnderlights();
            // save time by skipping sleep on last strobe
            if (i != timesToRepeat-1) {
                Thread.sleep(duration);
            }
        }
    }
}

public class ButtonHandler {
    private SwiftBotAPI bot;
    private ConcurrentHashMap<Button, Boolean> buttonState;
    private static Button[] buttons = {Button.A, Button.B, Button.X, Button.Y};

    public ButtonHandler(SwiftBotAPI Bot) {
        bot = Bot;
        buttonState = new ConcurrentHashMap<>();
    }

    private void resetButtons() {

        bot.disableAllButtons();

        
        for (Button button : buttons) {
            long startTime = System.currentTimeMillis();
            bot.enableButton(button, () -> {buttonState.put(button, true);});
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Total time taken: " + totalTime + " milliseconds");
            buttonState.put(button, false);
        }
    }

    public Button getFirstPressed() {

        resetButtons();

        
        int i = 0;
        while (true) {
            if (buttonState.get(buttons[i])) { return buttons[i]; }
            i++;
            if (i > 3) { i = 0; }
        }
    }
}
