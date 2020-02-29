package com.company.element;

import com.company.generator.GraphGen;
import com.company.test.TestEuclidean;

import java.util.ArrayList;
import java.util.HashSet;

public class Graph {
    public ArrayList<Vertex> vertices;
    public ArrayList<Graph> pieces;
    public int edgeNum;
    public int numV;
    public DataStructure.Stage affectedP;
    public ArrayList<GraphGen.Point> points;
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

    public void resetMatch(){
        for(Vertex v:vertices){
            v.matching = null;
            v.explored = false;
        }
        affectedP = new DataStructure.Stage(0,0);
    }


    public int matchCount(){
        ArrayList<Vertex> freeA = freeV(Label.A);
        ArrayList<Vertex> freeB = freeV(Label.B);
        assert freeA.size() == freeB.size();
        return this.vertices.size()/2 - freeA.size();
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
        for(int i = 0; i < vertices.size(); i++){
            ArrayList<Vertex> edges = new ArrayList<>();
            for(int j = 0; j < vertices.size(); j++){
                GraphGen.Point source = points.get(i);
                GraphGen.Point target = points.get(j);
                //if labels are same, or two vertexes are same, not calculate distance
                if(source == target || vertices.get(i).label == vertices.get(j).label){
                    continue;
                }
                double x = source.x - target.x;
                double y = source.y - target.y;
                double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                if(d <= bottleneck){
                    edges.add(vertices.get(j));
                }
            }
            vertices.get(i).edges = edges;
            edgeNum+=edges.size();
        }
        edgeNum/=2;
    }
}
