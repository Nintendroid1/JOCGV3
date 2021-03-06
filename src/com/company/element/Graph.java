package com.company.element;

import com.company.generator.GraphGen;
import com.company.test.TestEuclidean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Graph {
    public ArrayList<Vertex> vertices;
    public ArrayList<Graph> pieces;
    public int edgeNum;
    public int numV;
    public DataStructure.Stage affectedP;
    public ArrayList<Point> points;
    public Graph(){
        vertices = new ArrayList<>();
        pieces = new ArrayList<>();
        edgeNum = 0;
        affectedP = new DataStructure.Stage(0,0);
    }

    public int getWeight(Vertex v, Vertex u){
        if(v == null || u == null){
            return 0;
        }
        //assert v.hasEdgewith(u);

        if(v.piece == u.piece){
            return 0;
        }
        return 1;
    }

    public ArrayList<Vertex> freeV(Label label){
        ArrayList<Vertex> result = new ArrayList<>();
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

    public ArrayList<Vertex> V(Label label){
        ArrayList<Vertex> result = new ArrayList<>();
        for(Vertex v:vertices){
            if(label == null|| (v.label == label)){
                result.add(v);
            }
        }
        return result;
    }

    public void reset(boolean resetMatching){
        for(Vertex v:vertices){
            if(resetMatching){
                v.matching = null;
            }
            v.explored = false;
        }
        affectedP = new DataStructure.Stage(0,0);
    }


    public int matchCount(){
        int a = 0;
        int b = 0;
        for(Vertex v:vertices){
            if(!v.isFree()){
                if(v.label == Label.A){
                    a+=1;
                }
                else{
                    b+=1;
                }
            }
        }
        assert a == b;
        return a;
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


    public void reEdges(double bottleneck){
        edgeNum = 0;

    }

    public boolean perfectMatch(){
        return matchCount() == vertices.size()/2;
    }
}
