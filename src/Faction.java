import java.util.*;
import java.util.concurrent.Phaser;

public class Faction implements Runnable {
    private final String name;
    private final Factory factory;
    private final Phaser phaser;
    private final int days;
    private final Map<PartType, Integer> parts = new EnumMap<>(PartType.class);
    private int robotsBuilt = 0;

    public Faction(String name, Factory factory, Phaser phaser, int days) {
        this.name = name;
        this.factory = factory;
        this.phaser = phaser;
        this.days = days;

        for (PartType type : PartType.values()) {
            parts.put(type, 0);
        }
    }

    private void addParts(List<PartType> taken) {
        for (PartType type : taken) {
            parts.put(type, parts.get(type) + 1);
        }
    }

    private void buildRobots() {
        while (parts.get(PartType.HEAD) >= 1 &&
            parts.get(PartType.TORSO) >= 1 &&
            parts.get(PartType.HAND) >= 2 &&
            parts.get(PartType.FEET) >= 2) {

            parts.put(PartType.HEAD,  parts.get(PartType.HEAD)  - 1);
            parts.put(PartType.TORSO, parts.get(PartType.TORSO) - 1);
            parts.put(PartType.HAND,  parts.get(PartType.HAND)  - 2);
            parts.put(PartType.FEET,  parts.get(PartType.FEET)  - 2);

            robotsBuilt++;
        }
    }

    @Override
    public void run() {
        for (int day = 1; day <= days; day++) {
            phaser.arriveAndAwaitAdvance();

            int takenTotal = 0;

            while (takenTotal < 5) {
                List<PartType> taken = factory.getParts(1);
                if (taken.isEmpty()) {
                    break;
                }
                addParts(taken);
                takenTotal += taken.size();
            }

            buildRobots();

            System.out.printf("[Ночь %3d] [%s] взяла %d деталей. Всего роботов: %d%n",
                day, name, takenTotal, robotsBuilt);

            phaser.arriveAndAwaitAdvance();
        }

        phaser.arriveAndDeregister();
        System.out.printf("[%s] закончила игру. Итог: %d роботов.%n", name, robotsBuilt);
    }

    public int getRobotsBuilt() {
        return robotsBuilt;
    }

    public String getName() {
        return name;
    }
}