package data;

import services.ConstantStorage;

import java.io.Serializable;

public class Quadrant implements Serializable {
    private int innerQuadrantCount;
    private Mass mass;
    private data.Position bottomLeftCorner;
    private data.Position centerOfMass;
    private Dimension dimensions;
    private final Quadrant[] innerQuadrants;
    private Point innerPoint;

    private enum Position {
        BottomLeft, TopLeft, TopRight, BottomRight
    }

    public Quadrant() {
        this(0, new Mass(), new data.Position(), new data.Position(), new Dimension(), new Quadrant[4]);
    }

    public Quadrant(Dimension dimensions, data.Position bottomLeftCorner) {
        this(
                0,
                new Mass(),
                bottomLeftCorner,
                new data.Position(),
                dimensions,
                new Quadrant[4]);
    }

    public Quadrant(int innerQuadrantCount,
                    Mass mass,
                    data.Position bottomLeftCorner,
                    data.Position centerOfMass,
                    Dimension dimensions,
                    Quadrant[] innerQuadrants) {
        this.innerQuadrantCount = innerQuadrantCount;
        this.mass = mass;
        this.bottomLeftCorner = bottomLeftCorner;
        this.centerOfMass = centerOfMass;
        this.dimensions = dimensions;
        this.innerQuadrants = innerQuadrants;
        this.innerPoint = null;
    }

    public void setBottomLeftCorner(data.Position bottomLeftCorner) {
        this.bottomLeftCorner = bottomLeftCorner;
    }

    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    public synchronized void insert(Point point) {
        var current = this;

        while (true) {
            if (current.innerQuadrantCount == 0 && current.innerPoint == null) {
                current.innerPoint = point;
                current.updateMass(point);
                return;
            } else if (current.innerQuadrantCount == 0) {
                current.subdivide();
                final var toMove = current.innerPoint;
                current.innerPoint = null;
                final var targetQuadrantForExisting = current.subquadrantContaining(toMove);
                targetQuadrantForExisting.innerPoint = toMove;
                targetQuadrantForExisting.updateMass(toMove);
            }

            Quadrant targetQuadrantForNew = current.subquadrantContaining(point);
            if (targetQuadrantForNew.innerPoint == null && targetQuadrantForNew.innerQuadrantCount == 0) {
                targetQuadrantForNew.innerPoint = point;
                targetQuadrantForNew.updateMass(point);
                return;
            }

            current = targetQuadrantForNew;
        }
    }


    private synchronized void updateMass(Point point) {
        final var newMass = mass.value() + point.mass().value();
        final var cX = (centerOfMass.horizontal() * mass.value() + point.position().horizontal() * point.mass().value()) / newMass;
        final var cY = (centerOfMass.vertical() * mass.value() + point.position().vertical() * point.mass().value()) / newMass;
        final var newCenterOfMass = new data.Position(cX, cY);
        mass = new Mass(newMass);
        centerOfMass = newCenterOfMass;
    }

    private Quadrant subquadrantContaining(Point point) {
        final var anchorX = bottomLeftCorner.horizontal();
        final var anchorY = bottomLeftCorner.vertical();
        final var halfHeight = dimensions.vertical() / 2;
        final var halfWidth = dimensions.horizontal() / 2;
        final var position = point.position();
        final var originX = position.horizontal();
        final var originY = position.vertical();
        final var middleX = anchorX + halfWidth;
        final var middleY = anchorY + halfHeight;
        if ((originX <= middleX) && (originY <= middleY)) {
            return innerQuadrants[Position.BottomLeft.ordinal()];
        } else if ((originX <= middleX) && (originY >= middleY)) {
            return innerQuadrants[Position.TopLeft.ordinal()];
        } else if (originY <= middleY) {
            return innerQuadrants[Position.BottomRight.ordinal()];
        } else {
            return innerQuadrants[Position.TopRight.ordinal()];
        }
    }

    private void subdivide() {
        final var anchorX = bottomLeftCorner.horizontal();
        final var anchorY = bottomLeftCorner.vertical();
        final var halfHeight = dimensions.vertical() / 2;
        final var halfWidth = dimensions.horizontal() / 2;
        {
            Quadrant bottomLeft = new Quadrant();
            bottomLeft.setBottomLeftCorner(new data.Position(anchorX, anchorY));
            bottomLeft.setDimensions(new Dimension(halfWidth, halfHeight));
            innerQuadrants[Position.BottomLeft.ordinal()] = bottomLeft;
        }
        {
            Quadrant topLeft = new Quadrant();
            topLeft.setBottomLeftCorner(new data.Position(anchorX, anchorY + halfHeight));
            topLeft.setDimensions(new Dimension(halfWidth, halfHeight));
            innerQuadrants[Position.TopLeft.ordinal()] = topLeft;
        }
        {
            Quadrant topRight = new Quadrant();
            topRight.setBottomLeftCorner(new data.Position(anchorX + halfWidth, anchorY + halfHeight));
            topRight.setDimensions(new Dimension(halfWidth, halfHeight));
            innerQuadrants[Position.TopRight.ordinal()] = topRight;
        }
        {
            Quadrant bottomRight = new Quadrant();
            bottomRight.setBottomLeftCorner(new data.Position(anchorX + halfWidth, anchorY));
            bottomRight.setDimensions(new Dimension(halfWidth, halfHeight));
            innerQuadrants[Position.BottomRight.ordinal()] = bottomRight;
        }
        innerQuadrantCount = 4;
    }

    public void addForceActingOn(Point point) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (point) {
            var fx = point.force().horizontal();
            var fy = point.force().vertical();

            if (innerQuadrantCount == 0) {
                if (innerPoint != null && !innerPoint.equals(point)) {
                    final var f = point.directForce(innerPoint);
                    fx += f.horizontal();
                    fy += f.vertical();
                    point.setForce(new Force(fx, fy));
                }
                return;
            }
            final var size = dimensions.horizontal();
            final var distance = point.position().distance(centerOfMass);
            if (size / distance < ConstantStorage.ApproximationTheta) {

                final var f = point.directForce(this.asPoint());
                fx += f.horizontal();
                fy += f.vertical();
                point.setForce(new Force(fx, fy));
                return;
            }
            for (final var innerQ : innerQuadrants) {
                if (innerQ.innerQuadrantCount != 0 || innerQ.innerPoint != null) {
                    innerQ.addForceActingOn(point);
                }
            }
        }
    }

    public Point asPoint() {
        Point virtualPoint = new Point();
        virtualPoint.setPosition(centerOfMass);
        virtualPoint.setMass(mass);
        return virtualPoint;
    }
}

