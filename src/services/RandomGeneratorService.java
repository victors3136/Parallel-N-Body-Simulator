package services;

import data.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomGeneratorService {
    static private final Random random;

    static {
        random = new Random();
    }

    public static Position nextPosition() {

        final var innerHorizontalBound = ConstantStorage.MaxDim;
        final var innerVerticalBound = ConstantStorage.MaxDim;
        return new Position(
                random.nextDouble() * innerHorizontalBound,
                random.nextDouble() * innerVerticalBound);
    }

    public static Force nextForce() {
        return new Force(
                random.nextDouble() * (ConstantStorage.MaxInitialForce) + ConstantStorage.MinInitialForce,
                random.nextDouble() * (ConstantStorage.MaxInitialForce) + ConstantStorage.MinInitialForce);
    }

    public static Mass nextMass() {
        final var massValue = random.nextDouble() * ConstantStorage.MaxMass;
        return massValue == 0 ? nextMass() : new Mass(massValue);
    }

    public static Velocity nextVelocity() {
        return new Velocity();
    }

    public static Point nextPoint() {
        return new Point(nextPosition(), nextMass(), nextVelocity(), nextForce());
    }

    public static List<Point> points(int size) {
        assert size > 0;
        return IntStream.range(0, size).mapToObj(_ -> nextPoint()).collect(Collectors.toList());
    }
}
