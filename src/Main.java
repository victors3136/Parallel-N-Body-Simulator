import services.ConstantStorage;
import services.Simulator;

public class Main {

    public static void main(String[] args) {
        final var threadCount = Runtime.getRuntime().availableProcessors();
        final var partitionSize = ConstantStorage.InitialBodyCount / threadCount;
        System.out.println("Initialization:");
        System.out.println("Bodies Count = " + ConstantStorage.InitialBodyCount);
        System.out.println("Iteration Count = " + ConstantStorage.IterationCount);
        System.out.println("Thread Count = " + threadCount);
        System.out.println("Particles per thread = " + partitionSize);
        final var simulation = new Simulator(threadCount);
        simulation.run();
        simulation.shutdown();
    }
}
