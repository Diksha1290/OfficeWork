package com.officework.models;

/**
 * Created by Girish on 8/25/2016.
 */
public class RectangleCanvasModel {
    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public int getDimensionWidth() {
        return dimensionWidth;
    }

    public int getDimensionHeight() {
        return dimensionHeight;
    }

    private int coordX;
    private int coordY;
    private int dimensionWidth;
    private int dimensionHeight;

    public RectangleCanvasModel(int x, int y, int width, int height) {
        coordX = x;
        coordY = y;
        dimensionWidth = width;
        dimensionHeight = height;
    }

    public boolean isInsideBounds(int x, int y) {
        return ((x >= coordX && x <= coordX + dimensionWidth) && (y >= coordY && y <= coordY + dimensionHeight));
    }
}
