import java.util.concurrent.Phaser;

public class Main {
    public static void main(String[] args) {
        final int DAYS = 100;

        Phaser phaser = new Phaser(3);

        Factory factory = new Factory(phaser, DAYS);
        Faction world = new Faction("World", factory, phaser, DAYS);
        Faction wednesday = new Faction("Wednesday", factory, phaser, DAYS);

        Thread factoryThread = new Thread(factory, "FactoryThread");
        Thread worldThread = new Thread(world, "WorldThread");
        Thread wedThread = new Thread(wednesday, "WednesdayThread");

        factoryThread.start();
        worldThread.start();
        wedThread.start();

        try {
            factoryThread.join();
            worldThread.join();
            wedThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n=== РЕЗУЛЬТАТЫ ===");
        System.out.printf("%s собрала %d роботов%n", world.getName(), world.getRobotsBuilt());
        System.out.printf("%s собрала %d роботов%n", wednesday.getName(), wednesday.getRobotsBuilt());

        if (world.getRobotsBuilt() > wednesday.getRobotsBuilt()) {
            System.out.println("🏆 Побеждает фракция World!");
        } else if (world.getRobotsBuilt() < wednesday.getRobotsBuilt()) {
            System.out.println("🏆 Побеждает фракция Wednesday!");
        } else {
            System.out.println("🤝 Ничья!");
        }
    }
}