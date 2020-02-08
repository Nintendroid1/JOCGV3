package com.company.element;

import java.util.ArrayList;
import java.util.HashSet;

public class Vertex {
    public int id;
    public Label label;
    public ArrayList<Vertex> edges;
    public Vertex matching;
    public Graph piece;
    public boolean explored;
    public HashSet<Vertex> visitedE;
    public HashSet<Vertex> tempE;
    public int distance;

    public Vertex(int id, Label label){
        this.id = id;
        this.label = label;
        this.edges = new ArrayList<>();
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
