package data;

import java.io.Serializable;

public record Velocity(
        double horizontal,
        double vertical
) implements Serializable {
    public Velocity() {
        this(0, 0);
    }
    public Velocity compose(Velocity that, double thisCoefficient, double thatCoefficient) {
        return new Velocity((this.horizontal * thisCoefficient + that.horizontal * thatCoefficient)/ (thisCoefficient + thatCoefficient),
                (this.vertical * thisCoefficient + that.vertical * thatCoefficient)/ (thisCoefficient + thatCoefficient));
    }
}
