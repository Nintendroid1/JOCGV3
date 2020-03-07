package com.company.test;

import com.company.algorithm.Algo;
import com.company.algorithm.Hop;
import com.company.algorithm.Hop_NoHash;
import com.company.algorithm.Jocg_Pointer;
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
                graph = euclideanGen.generate(numV,128,part,0.01,bottleneck);
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

    /*
     * graph needs to preserve points so that edge can be generated
     */
    public static Double[] find(Algo algo, double head, double tail, double approx){
//        Hop_NoHash hop;
//        Jocg_Pointer jocg;
//
//        if(algo.getClass() == Hop_NoHash.class){
//            hop = (Hop_NoHash)algo;
//            while (tail - head > approx*tail){
//                double bottleneck = (tail + head)/2;
//                hop.graph.resetMatch();
//                hop.graph.reEdges(bottleneck);
//                hop.start();
//
//                if(hop.graph.matchCount() == hop.graph.vertices.size()/2){
//                    tail = bottleneck;
//                }
//                else{
//                    head = bottleneck;
//                }
//            }
//        }
//        else if(algo.getClass() == Jocg_Pointer.class){
//            jocg = (Jocg_Pointer)algo;
//        }
//        else{
//            return;
//        }

        while (tail - head > approx*tail){
            double bottleneck = (tail + head)/2;
            algo.graph.resetMatch();
            algo.graph.reEdges(bottleneck);
            algo.start();

            if(algo.graph.perfectMatch()){
                tail = bottleneck;
            }
            else{
                head = bottleneck;
            }
        }

        if(!algo.graph.perfectMatch()){
            return null;
        }
        else{
            return new Double[]{head,tail};
        }

    }
}
