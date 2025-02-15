package data;

import java.io.Serializable;

public record Mass(double value) implements Serializable {
    public Mass() {
        this(1);
    }
    public Mass sum(Mass other) {
        return new Mass(this.value + other.value);
    }
}
