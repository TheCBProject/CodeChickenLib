/*
 * This file is part of CodeChickenLib.
 * Copyright (c) 2018, covers1624, All rights reserved.
 *
 * CodeChickenLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * CodeChickenLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CodeChickenLib. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package codechicken.lib.math;

import org.joml.Vector2d;

/**
 * @author covers1624
 */
public class InterpHelper {

    private final Vector2d[] posCache = { new Vector2d(), new Vector2d(), new Vector2d(), new Vector2d() };
    private final double[] valCache = new double[4];

    private double x0;
    private double x1;
    private double y0;
    private double y1;

    private double rX;
    private double rY;

    private int p00;
    private int p10;
    private int p11;
    private int p01;

    /**
     * Resets the interp helper with the given quad. Does not care what order the vertices are in.
     */
    public void reset(double dx0, double dy0, double dx1, double dy1, double dx2, double dy2, double dx3, double dy3) {
        posCache[0].set(dx0, dy0);
        posCache[1].set(dx1, dy1);
        posCache[2].set(dx2, dy2);
        posCache[3].set(dx3, dy3);

        p00 = 0;// Bottom Left is always first.
        x0 = posCache[p00].x;
        y0 = posCache[p00].y;
        for (int i = 1; i < 4; i++) {
            double x = posCache[i].x;
            double y = posCache[i].y;
            if (y0 == y) {
                p10 = i;// Bottom right.
                x1 = x;
            } else if (x0 == x) {
                p01 = i;// Top left.
                y1 = y;
            } else {
                // Top right.
                p11 = i;
            }
        }
    }

    /**
     * Computes the coefficients for the interpolation.
     *
     * @param x X interp location.
     * @param y Y interp location.
     */
    public void locate(double x, double y) {
        rX = (x - x0) / (x1 - x0);
        rY = (y - y0) / (y1 - y0);
    }

    /**
     * Interpolates using the already computed coefficients.
     *
     * @param q0 Value at dx0 dy0
     * @param q1 Value at dx1 dy1
     * @param q2 Value at dx2 dy2
     * @param q3 Value at dx3 dy3
     * @return The result.
     */
    public double interpolate(double q0, double q1, double q2, double q3) {
        valCache[0] = q0;
        valCache[1] = q1;
        valCache[2] = q2;
        valCache[3] = q3;
        double f0 = (valCache[p00] * (1 - rX)) + (valCache[p10] * rX);
        double f1 = (valCache[p01] * (1 - rX)) + (valCache[p11] * rX);

        return (f0 * (1 - rY)) + (f1 * rY);
    }

    /**
     * Interpolates using the already computed coefficients.
     *
     * @param q0 Value at dx0 dy0
     * @param q1 Value at dx1 dy1
     * @param q2 Value at dx2 dy2
     * @param q3 Value at dx3 dy3
     * @return The result.
     */
    public float interpolate(float q0, float q1, float q2, float q3) {
        valCache[0] = q0;
        valCache[1] = q1;
        valCache[2] = q2;
        valCache[3] = q3;
        double f0 = (valCache[p00] * (1 - rX)) + (valCache[p10] * rX);
        double f1 = (valCache[p01] * (1 - rX)) + (valCache[p11] * rX);

        return (float) ((f0 * (1 - rY)) + (f1 * rY));
    }
}
