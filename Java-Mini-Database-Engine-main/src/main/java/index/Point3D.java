package main.java.index;

import java.io.Serializable;

public record Point3D(double x, double y, double z) implements Serializable {

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
