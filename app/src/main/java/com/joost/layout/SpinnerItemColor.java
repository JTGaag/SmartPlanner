package com.joost.layout;

/**
 * Created by Joost on 02/02/2015.
 */
public class SpinnerItemColor {
    private String colorName;
    private int color;
    private Boolean resourceColor;

    public SpinnerItemColor(String colorName, int color, Boolean resourceColor) {
        this.colorName = colorName;
        this.color = color;
        this.resourceColor = resourceColor;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Boolean getResourceColor() {
        return resourceColor;
    }

    public void setResourceColor(Boolean resourceColor) {
        this.resourceColor = resourceColor;
    }
}
