package com.company.element;

import java.util.Comparator;

public class Point {

    public static class Xcomparator implements Comparator<Point> {
        @Override
        public int compare(Point p1, Point p2) {
            return Double.compare(p1.x, p2.x);
        }
    }

    public static class Ycomparator implements Comparator<Point> {
        @Override
        public int compare(Point p1, Point p2) {
            return Double.compare(p1.y, p2.y);
        }
    }

    public double x; public double y;public Vertex v;
    public Point(double x,double y){
        this.x = x;this.y = y;
    }
}
