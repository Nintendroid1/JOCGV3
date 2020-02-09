package com.company.element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Vertex {
    public int id;
    public Label label;
    public LinkedList<Vertex> edges;
    /*
     * visitedE is the index for the last vertex(edge) in visitedE
     * tempE is the index for the last vertex(edge) in tempE
     * onezero is the the index for the last edge(vertex) with weight 1 in edges
     */
    private int visitedE;
    private int tempE;
    private int onezero;
    private int current;

    public Vertex matching;
    public Graph piece;

    public boolean explored;
    public int distance;

    public Vertex(int id, Label label){
        this.id = id;
        this.label = label;
        this.edges = new LinkedList<>();
        this.matching = null;
        this.piece = null;
        this.explored = false;
    }

    public void match(Vertex x){
        x.matching = this;
        this.matching = x;
    }

    public boolean hasEdgewith(Vertex x){
        return x.edges.contains(this) && this.edges.contains(x);
    }

    public boolean isFree(){
        return matching == null;
    }

}
