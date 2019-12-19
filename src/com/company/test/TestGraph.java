package com.company.test;

import com.company.element.Graph;
import com.company.element.Vertex;

import java.util.HashSet;

public class TestGraph {
    public static void ifValid(Graph graph){
        HashSet<Vertex> vSet = new HashSet<>();
        for(Graph subgraph:graph.pieces){
            for(Vertex v:subgraph.vertices){
                assert subgraph == v.piece;
                assert !vSet.contains(v);
                vSet.add(v);
            }
        }
    }
}
