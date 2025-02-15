package data;

import services.ConstantStorage;

import java.io.Serializable;

public record Position(
        double horizontal,
        double vertical
) implements Serializable {
    private final static double PositionDelta = ConstantStorage.MaxDim / 200;
    public Position() {
        this(0, 0);
    }

    public double distance(Position that) {
        return Math.sqrt(
                Math.pow(this.horizontal - that.horizontal, 2)
                        + Math.pow(this.vertical - that.vertical, 2));
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof Position(double otherHorizontal, double otherVertical))) return false;
        return (Math.abs(horizontal - otherHorizontal) < PositionDelta) && (Math.abs(vertical - otherVertical) < PositionDelta);
    }
}