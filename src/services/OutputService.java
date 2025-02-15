package services;

import data.Point;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.IntStream;

public class OutputService {

    public static void write(int iterationIndex, List<Point> points) {
        final var filename = String.format("outputs/csv/positions_iter_%d.csv", iterationIndex);
        try (final var fileWriter = new FileWriter(filename);
             final var bufferedWriter = new BufferedWriter(fileWriter);
             final var printWriter = new PrintWriter(bufferedWriter)) {
            printWriter.println("particle_id,x,y,mass");
            for (var index = 0; index < points.size(); index++) {
                final var point = points.get(index);
                printWriter.printf("%4d,%10.2f,%10.2f,%10.2f%n",
                        index,
                        point.position().horizontal(),
                        point.position().vertical(),
                        point.mass().value());
            }
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
        }
    }

    public static void displayProgress(int iteration) {
        final var barLength = 50;
        double progress = (double) iteration / ConstantStorage.IterationCount;

        int completed = (int) (progress * barLength);
        StringBuilder bar = new StringBuilder(barLength + 2);
        bar.append("[");
        IntStream.range(0, barLength).forEachOrdered(i -> bar.append((i < completed) ? "=" : " "));
        bar.append("]");
        System.out.printf("%s %3.0f%%%n", bar, progress * 100);
    }
}

