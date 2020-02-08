package com.company.element;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Graph {
    public ArrayList<Vertex> vertices;
    public ArrayList<Graph> pieces;
    public int edgeNum;
    public int numV;
    public Graph(){
        vertices = new ArrayList<>();
        pieces = new ArrayList<>();
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

    public LinkedList<Vertex> freeV(Label label){
        LinkedList<Vertex> result = new LinkedList<>();
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

    public LinkedList<Vertex> V(Label label){
        LinkedList<Vertex> result = new LinkedList<>();
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

    public boolean matchValidCheck(){
        HashSet<Vertex> aused = new HashSet<>();
        HashSet<Vertex> bused = new HashSet<>();
        for(Vertex v:freeV(Label.A)){
            if(v.isFree()){
                continue;
            }
            if(bused.contains(v.matching)){
                return false;
            }
            if(v.matching.matching != v){
                return false;
            }
            bused.add(v.matching);
        }
        for(Vertex v:freeV(Label.B)){
            if(v.isFree()){
                continue;
            }
            if(aused.contains(v.matching)){
                return false;
            }
            if(v.matching.matching != v){
                return false;
            }
            aused.add(v.matching);
        }
        return true;
    }
}
