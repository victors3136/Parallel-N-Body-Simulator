package data;

import services.ConstantStorage;

import java.io.Serializable;

public class Point implements Serializable {
    private Position position;
    private Mass mass;
    private Velocity velocity;
    private Force force;

    public Point() {
        this(new Position(), new Mass(), new Velocity(), new Force());
    }

    public Point(Position position, Mass mass, Velocity velocity, Force force) {
        this.position = position;
        this.mass = mass;
        this.velocity = velocity;
        this.force = force;
    }

    public Position position() {
        return position;
    }

    public Mass mass() {
        return mass;
    }

    public Velocity velocity() {
        return velocity;
    }

    public Force force() {
        return force;
    }

    public void setForce(Force force) {
        this.force = force;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setMass(Mass mass) {
        this.mass = mass;
    }

    public Force directForce(Point other) {
        final var dx = other.position.horizontal() - this.position.horizontal();
        final var dy = other.position.vertical() - this.position.vertical();
        final var distance = this.position.distance(other.position);

        final var magnitude = (
                ConstantStorage.GravitationalConstant * mass.value() * other.mass.value())
                / (distance * distance);
        final var factor = magnitude / distance;
        final var fx = factor * dx;
        final var fy = factor * dy;
        return new Force(fx, fy);
    }

    public synchronized void updatePosition() {
        final var fx = 0;
        final var fy = 0;

        var vx = velocity.horizontal() + force.horizontal() / mass.value() * ConstantStorage.TimeIncrement;
        vx = Math.min(vx, ConstantStorage.MaxVelocity);
        vx = Math.max(vx, -1 * ConstantStorage.MaxVelocity);
        var vy = velocity().vertical() + force.vertical() / mass.value() * ConstantStorage.TimeIncrement;
        vy = Math.min(vy, ConstantStorage.MaxVelocity);
        vy = Math.max(vy, -1 * ConstantStorage.MaxVelocity);

        var px = position.horizontal() + velocity.horizontal() * ConstantStorage.TimeIncrement;
        var py = position.vertical() + velocity.vertical() * ConstantStorage.TimeIncrement;
        if ((px <= 0) || (px >= ConstantStorage.MaxDim)) {
            vx *= -1;
        }
        if ((py <= 0) || (py >= ConstantStorage.MaxDim)) {
            vy *= -1;
        }

        force = new Force(fx, fy);
        velocity = new Velocity(vx, vy);
        position = new Position(px, py);
    }

    public Point merge(Point that){
        final var newPosition = this.position();
        final var newMass = this.mass().sum(that.mass());
        final var newVelocity = this.velocity().compose(that.velocity(),
                this.mass().value(),
                that.mass().value());
        final var newForce = this.force().compose(that.force(), this.mass.value(), that.mass.value());
        return new Point(newPosition, newMass, newVelocity, new Force());
    }
}

