package com.joost.category;

/**
 * Created by Joost on 28/01/2015.
 */
public class Category {

    private long id;
    private String name;
    private long parent_id;
    private long lft;
    private long rgt;
    private int color;

    public Category(long id, String name, long parent_id, long lft, long rgt, int color) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.lft = lft;
        this.rgt = rgt;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public long getLft() {
        return lft;
    }

    public void setLft(long lft) {
        this.lft = lft;
    }

    public long getRgt() {
        return rgt;
    }

    public void setRgt(long rgt) {
        this.rgt = rgt;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
