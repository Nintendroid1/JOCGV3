package com.company.element;

import java.util.HashSet;

public class Graph {
    public HashSet<Vertex> vertices;
    public HashSet<Graph> pieces;
    public int edgeNum;
    public Graph(){
        vertices = new HashSet<>();
        pieces = new HashSet<>();
        edgeNum = 0;
    }

    public int getWeight(Vertex v, Vertex u){
        if(v == null || u == null){
            return 0;
        }
        assert v.hasEdgewith(u);

        if(v.piece == u.piece){
            return 0;
        }
        return 1;
    }

    public HashSet<Vertex> freeV(Label label){
        HashSet<Vertex> result = new HashSet<>();
        for(Vertex v:vertices){
            if(v.isFree() && (label == null|| (v.label == label))){
                result.add(v);
            }
        }
        return result;
    }

    public void resetExplore(){
        for(Vertex v:vertices){
            v.explored = false;
        }
    }

    public HashSet<Vertex> V(Label label){
        HashSet<Vertex> result = new HashSet<>();
        for(Vertex v:vertices){
            if(label == null|| (v.label == label)){
                result.add(v);
            }
        }
        return result;
    }

    public void resetMatch(){
        for(Vertex v:vertices){
            v.matching = null;
        }
    }

    public void resetVisit(){
        for(Vertex v:vertices){
            v.visitedE = new HashSet<>();
            v.tempE = new HashSet<>();
        }
    }

    public int matchCount(){
        assert freeV(Label.A).size() == freeV(Label.B).size();
        assert  freeV(null).size()%2 == 0;
        return (vertices.size() - freeV(null).size())/2;
    }
}
