package services;

import data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulator {

    private final List<Point> points;
    private Quadrant rootQuadrant;
    private final ExecutorService taskExecutor;
    private final int threadCount;

    private int partitionSize() {
        return points.size() / threadCount;
    }

    public Simulator(int threadCount) {
        this.threadCount = threadCount;
        this.points = RandomGeneratorService.points(ConstantStorage.InitialBodyCount);
        taskExecutor = Executors.newFixedThreadPool(threadCount);
    }

    public void run() {
        for (var iteration = 0; iteration < ConstantStorage.IterationCount; iteration++) {
            if (iteration % 50 == 0 || iteration == ConstantStorage.IterationCount - 1) {
                OutputService.displayProgress(iteration);
            }
            rootQuadrant = new Quadrant(
                    new Dimension(ConstantStorage.MaxDim, ConstantStorage.MaxDim),
                    new Position()
            );

            points.forEach(point -> rootQuadrant.insert(point));

            computeForces();

            points.forEach(Point::updatePosition);
            ensureUniquePositions();
            writeOut(iteration);
        }
    }

    private void ensureUniquePositions() {
        for (var index = 0; index < points.size() - 1; ++index) {
            for (var jndex = index + 1; jndex < points.size(); ++jndex) {
                if (points.get(index).position().equals(points.get(jndex).position())) {
                    final var base = points.get(index);
                    final var collider = points.get(jndex);


                    points.set(index, base.merge(collider));
                    points.remove(jndex);

                    jndex--;
                }
            }
        }
    }

    private void writeOut(int iteration) {
        OutputService.write(iteration, points);
    }

    private void computeForces() {
        final var tasks = new ArrayList<Runnable>();
        for (var index = 0; index < points.size(); index += partitionSize()) {
            final var start = index;
            final var end = Math.min(index + partitionSize(), points.size());
            if (start == end) {
                break;
            }
            tasks.add(() -> {
                for (var jndex = start; jndex < end; jndex++) {
                    rootQuadrant.addForceActingOn(points.get(jndex));
                }
            });
        }

        try {
            taskExecutor.invokeAll(tasks.stream()
                    .map(Executors::callable)
                    .toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Force computation interrupted", e);
        }
    }


    public void shutdown() {
        taskExecutor.shutdown();
    }
}