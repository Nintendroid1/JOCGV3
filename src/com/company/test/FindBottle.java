package com.company.test;

import com.company.algorithm.Hop;
import com.company.algorithm.Hop_NoHash;
import com.company.element.Graph;
import com.company.generator.GraphGen;

public class FindBottle {

    public static void find(){
        int numV = 10000;
        int part = 4;
        double head = 0;
        double tail = 16;
        double bottleneck = 0;
        double lambda = 0.01;
        GraphGen euclideanGen = new GraphGen(5);
        Graph graph = null;

        while(tail-head > lambda){
            bottleneck = (tail + head)/2;
            if(graph == null){
                graph = euclideanGen.generate2(numV,128,part,0.01,bottleneck,true);
            }
            else{
                graph.resetMatch();
                graph.reEdges(bottleneck);
            }
            Hop_NoHash hop = new Hop_NoHash(graph);
            hop.start();
            if(graph.matchCount() < graph.vertices.size()/2){
                head = bottleneck;
            }
            else{
                tail = bottleneck;
            }
        }

        System.out.println(bottleneck);

    }
}
