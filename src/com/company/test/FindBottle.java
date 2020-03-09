package com.company.test;

import com.company.algorithm.*;
import com.company.element.EdgeMaker;
import com.company.element.Graph;
import com.company.generator.GraphGen;

public class FindBottle {

    static int numV;
    static double head;
    static double tail;
    static double bottleneck;
    static double lambda;
    static int partN;
    static double largeG;
    static double smallG;

    public static void init(){
        numV = 100000;
        partN = 4;
        largeG = 128;
        smallG = 0.01;
        head = 0;
        tail = 5;
        bottleneck = 0;
        lambda = 0.01;
    }

    public static void find(){
        long start,end;
        long hTime,jTime;
        init();
        GraphGen graphGen = new GraphGen(5);
        Graph graph = graphGen.generate(numV,largeG);

        start = System.currentTimeMillis();
        find_Jocg(graph);
        end = System.currentTimeMillis();
        jTime = end - start;

        init();

        start = System.currentTimeMillis();
        find_Hk(graph);
        end = System.currentTimeMillis();
        hTime = end - start;





        System.out.println("HK search bottleneck takes: " + hTime);
        System.out.println("Jocg search bottleneck takes: " + jTime);

    }


    public static void find_Hk(Graph graph){
//        int numV = 100000;
//        int part = 4;
//        double head = 0;
//        double tail = 5;
//        double bottleneck = 0;
//        double lambda = 0.01;
//        GraphGen graphGen = new GraphGen(5);
//        Graph graph = graphGen.generate(numV,largeG);
        EdgeMaker edgeMaker = new EdgeMaker(graph);
        while(tail-head > lambda*head){
            bottleneck = (tail + head)/2;
            edgeMaker.reEdges(bottleneck);
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

    public static void find_Jocg(Graph graph){
//        int numV = 10000;
//        int partN = 4;
//        double largeG = 128;
//        double smallG = 0.01;
//        double head = 0;
//        double tail = 8;
//        double bottleneck = 0;
//        double lambda = 0.01;
//        GraphGen graphGen = new GraphGen(5);
//        Graph graph = graphGen.generate(numV,largeG);
        EdgeMaker edgeMaker = new EdgeMaker(graph);
        while(tail-head > lambda*head){
            bottleneck = (tail + head)/2;
            edgeMaker.reEdgesWeights(bottleneck,partN,largeG,smallG);
            Jocg_Pointer jocg = new Jocg_Pointer(graph);
            jocg.start();
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
