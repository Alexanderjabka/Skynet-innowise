import java.util.*;
import java.util.concurrent.Phaser;

public class Factory implements Runnable {
    private final List<PartType> storage = new ArrayList<>();
    private final Random random = new Random();
    private final Phaser phaser;
    private final int days;

    public Factory(Phaser phaser, int days) {
        this.phaser = phaser;
        this.days = days;
    }

    private synchronized void produceParts(int day) {
        int count = random.nextInt(11);
        for (int i = 0; i < count; i++) {
            PartType type = PartType.values()[random.nextInt(PartType.values().length)];
            storage.add(type);
        }
        System.out.printf("[День %3d] Фабрика произвела %2d деталей. Всего на складе: %2d%n",
            day, count, storage.size());
    }

    public synchronized List<PartType> getParts(int maxParts) {
        List<PartType> taken = new ArrayList<>();
        int take = Math.min(maxParts, storage.size());
        for (int i = 0; i < take; i++) {
            taken.add(storage.remove(random.nextInt(storage.size())));
        }
        return taken;
    }

    public synchronized int getStorageSize() {
        return storage.size();
    }

    @Override
    public void run() {
        for (int day = 1; day <= days; day++) {
            produceParts(day);

            phaser.arriveAndAwaitAdvance();

            phaser.arriveAndAwaitAdvance();

            System.out.printf("[День %3d] После ночи на складе осталось: %2d%n%n",
                day, getStorageSize());
        }

        phaser.arriveAndDeregister();
        System.out.println("[Фабрика] Работа завершена!");
    }
}