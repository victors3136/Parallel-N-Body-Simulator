package data;

import java.io.Serializable;

public record Force(
        double horizontal,
        double vertical
) implements Serializable {
    public Force() {
        this(0, 0);
    }

    public Force compose(Force that, double thisCoefficient, double thatCoefficient) {
        return new Force((horizontal * thisCoefficient + that.horizontal * thatCoefficient) / (thisCoefficient + thatCoefficient),
                (vertical * thisCoefficient + that.vertical * thatCoefficient) / (thisCoefficient + thatCoefficient));
    }
}

