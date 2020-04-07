package com.company.test;

import com.company.algorithm.*;
import com.company.element.*;
import com.company.generator.GraphGen;

import java.util.ArrayList;
import java.util.Random;

public class FindBottle {

    int numV;
    double lambda;
    int partN;
    double largeG;
    double smallG;

    int numV_C = 10000;
    double lambda_C = 0.01;
    int partN_C = 4;
    double largeG_C = 128;
    double smallG_C = 0.01;

    private double initGuess(Graph graph){
        double guess = largeG/Math.sqrt(graph.vertices.size());
        System.out.println("Initial Guess = " + guess);
        return guess;
//        double closestX = Double.MAX_VALUE;
//        double closestY = Double.MAX_VALUE;
//        double result = 0;
//        graph.points.sort(new Point.Xcomparator());
//        for(int i = 0; i < graph.points.size()-1;i++){
//            double delta = graph.points.get(i+1).x - graph.points.get(i).x;
//            if(delta < closestX && delta!=0){
//                closestX = delta;
//            }
//        }
//
//        graph.points.sort(new Point.Ycomparator());
//        for(int i = 0; i < graph.points.size()-1;i++){
//            double delta = graph.points.get(i+1).y - graph.points.get(i).y;
//            if(delta < closestY && delta != 0){
//                closestY = delta;
//            }
//        }
//
//        result = Math.sqrt(Math.pow(closestX,2)+Math.pow(closestY,2));
//        return 5102*result;

    }

    public FindBottle(int n, int p, double lg, double sg, double lb){
        numV_C = n;
        partN = p;
        largeG_C = lg;
        smallG_C = sg;
        lambda_C = lb;
    }

    public FindBottle(int n){
        numV_C = n;
    }

    public FindBottle(){

    }

    public  void init(){
        numV = numV_C;
        partN = (int)Math.sqrt(Math.pow(numV,1.0/3.0));
        largeG = largeG_C;
        smallG = smallG_C;
        lambda = lambda_C;
    }


    public double find(Graph graph){
        partN_C = (int) Math.pow(graph.vertices.size(),1.0/6.0);
        init();
        ExperimentList.Experiment hEx = find_Jocg_Doubling(graph);
        return hEx.getFinalBottleNeck();
    }

    public ExperimentList.Experiment[] find(){
        long start,end;
        double hTime = 0;
        double jTime = 0;
        int time = 1;
        ExperimentList.Experiment hEx = null;
        ExperimentList.Experiment jEx = null;
        for(int i = 0; i < time; i++){
            init();
            GraphGen graphGen = new GraphGen(32+(int)System.currentTimeMillis()+(int)Thread.currentThread().getId());
            Graph graph = graphGen.generate(numV,largeG);

            System.out.println("Generate Graph Finished");

            start = System.currentTimeMillis();
            hEx = find_HK_Doubling(graph);
            end = System.currentTimeMillis();
            hTime += end - start;

            hEx.totalRunningTime = hTime;

            init();
            graph.reset(true);

            start = System.currentTimeMillis();
            jEx = find_Jocg_Doubling(graph);
            end = System.currentTimeMillis();
            jTime += end - start;
            jEx.totalRunningTime = jTime;
        }

        jTime/=time;
        hTime/=time;


        System.out.println("HK search bottleneck takes: " + hTime);
        System.out.println("Jocg search bottleneck takes: " + jTime);
        System.out.println("J/H: " + jTime/hTime);

        ExperimentList.Experiment[] experiments = new ExperimentList.Experiment[2];
        experiments[0] = hEx;
        experiments[1] = jEx;
        return experiments;
    }

    public ExperimentList.Experiment find_HK_Doubling(Graph graph){
        ExperimentList.Experiment expe = new ExperimentList.Experiment();
        EdgeMaker edgeMaker = new EdgeMaker(graph);
        double bottleneck = initGuess(graph);
        do{
            bottleneck*=2;
            edgeMaker.reEdges(bottleneck,false);
            //System.out.println("reEdge finished");
            Hop_NoHash hop = new Hop_NoHash(graph);
            expe.add(bottleneck,hop.dr);
            hop.start();
        }while (graph.matchCount() < graph.vertices.size()/2);

        expe.add(find_Hk_Binary(graph, bottleneck/2, bottleneck));
        return expe;

    }

    public ExperimentList.Experiment find_Jocg_Doubling(Graph graph){
        ExperimentList.Experiment expe = new ExperimentList.Experiment();
        EdgeMaker edgeMaker = new EdgeMaker(graph);
        double bottleneck = initGuess(graph);
        do{
            bottleneck*=2;
            edgeMaker.reEdgesFixShift(bottleneck,partN,largeG,smallG,false);
            Jocg_Pointer jocg = new Jocg_Pointer(graph);
            expe.add(bottleneck,jocg.dr);
            jocg.start();
        }while (graph.matchCount() < graph.vertices.size()/2);

        expe.add(find_Jocg_Binary(graph, bottleneck/2, bottleneck));
        return expe;
    }


    public ExperimentList.Experiment find_Hk_Binary(Graph graph, double head, double tail){
        ExperimentList.Experiment expe = new ExperimentList.Experiment();
        EdgeMaker edgeMaker = new EdgeMaker(graph);
        double bottleneck = 0;
        while(tail-head > lambda*head){
            boolean resetMatching = (bottleneck > (tail+head)/2);
            bottleneck = (tail + head)/2;
            edgeMaker.reEdges(bottleneck,resetMatching);
            Hop_NoHash hop = new Hop_NoHash(graph);
            hop.start();
            expe.add(bottleneck,hop.dr);
            if(graph.matchCount() < graph.vertices.size()/2){
                head = bottleneck;
            }
            else{
                tail = bottleneck;
            }
        }

        ArrayList<Double> candidate = edgeMaker.getCandidate(head,tail);
        int head2 = 0; int tail2 = candidate.size();
        bottleneck = Double.MAX_VALUE;
        while (tail2 - head2 > 3){
            int nextIndex = (tail2 + head2)/2;
            boolean resetMatching = (bottleneck > candidate.get(nextIndex));
            bottleneck = candidate.get(nextIndex);
            edgeMaker.reEdges(bottleneck,resetMatching);
            Hop_NoHash hop = new Hop_NoHash(graph);
            hop.start();
            expe.add(bottleneck,hop.dr);
            if(graph.matchCount() < graph.vertices.size()/2){
                head2 = nextIndex;
            }
            else{
                tail2 = nextIndex;
            }
        }
        for(int i = head2; i <= tail2; i++){
            if(i >= candidate.size()){
                continue;
            }
            boolean resetMatching = (bottleneck > candidate.get(i));
            bottleneck = candidate.get(i);
            edgeMaker.reEdges(bottleneck,resetMatching);
            Hop_NoHash hop = new Hop_NoHash(graph);
            hop.start();
            expe.add(bottleneck,hop.dr);
            if(graph.matchCount() == graph.vertices.size()/2){
                break;
            }
        }

        System.out.println(bottleneck);
        return expe;
    }

    public ExperimentList.Experiment find_Jocg_Binary(Graph graph, double head, double tail){
        ExperimentList.Experiment expe = new ExperimentList.Experiment();
        EdgeMaker edgeMaker = new EdgeMaker(graph);
        double bottleneck = 0;
        while(tail-head > lambda*head){
            boolean resetMatching = (bottleneck > (tail+head)/2);
            bottleneck = (tail + head)/2;
            edgeMaker.reEdgesFixShift(bottleneck,partN,largeG,smallG,resetMatching);
            Jocg_Pointer jocg = new Jocg_Pointer(graph);
            jocg.start();
            expe.add(bottleneck,jocg.dr);
            if(graph.matchCount() < graph.vertices.size()/2){
                head = bottleneck;
            }
            else{
                tail = bottleneck;
            }
        }
        ArrayList<Double> candidate = edgeMaker.getCandidate(head,tail);
        int head2 = 0; int tail2 = candidate.size();
        bottleneck = Double.MAX_VALUE;
        while (tail2 - head2 > 3){
            int nextIndex = (tail2 + head2)/2;
            boolean resetMatching = (bottleneck > candidate.get(nextIndex));
            bottleneck = candidate.get(nextIndex);
            edgeMaker.reEdgesFixShift(bottleneck,partN,largeG,smallG,resetMatching);
            Jocg_Pointer jocg = new Jocg_Pointer(graph);
            jocg.start();
            expe.add(bottleneck,jocg.dr);
            if(graph.matchCount() < graph.vertices.size()/2){
                head2 = nextIndex;
            }
            else{
                tail2 = nextIndex;
            }
        }
        for(int i = head2; i <= tail2; i++){
            if(i >= candidate.size()){
                continue;
            }
            boolean resetMatching = (bottleneck > candidate.get(i));
            bottleneck = candidate.get(i);
            edgeMaker.reEdgesFixShift(bottleneck,partN,largeG,smallG,resetMatching);
            Jocg_Pointer jocg = new Jocg_Pointer(graph);
            jocg.start();
            expe.add(bottleneck,jocg.dr);
            if(graph.matchCount() == graph.vertices.size()/2){
                break;
            }
        }

        System.out.println(bottleneck);
        return expe;
    }
}
