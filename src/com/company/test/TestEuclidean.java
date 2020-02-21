package com.company.test;

import com.company.algorithm.*;
import com.company.element.Graph;
import com.company.element.Vertex;
import com.company.generator.GraphGen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestEuclidean {
    public TestEuclidean(){

    }

    public static void test() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("EculideanOut.txt"));
        writer.write("");
        Hop_NoHash hop;
        Jocg_Pointer jocg;
        int[] numV = {10000};//,50000};
        int[] middles = {64};//{8,16,32,64};
        int times = 5;
        double bottleneck = 5;
        for(int i:numV){
            for(int middle:middles){

                double edgeNum = 0;
                double matchNum = 0;
                double timeElapsedHop = 0;
                double timeElapsedJocg = 0;
                double timePreprocess = 0;
                double HKdve = 0;
                double HKbve = 0;
                double Jodve = 0;
                double Jobve = 0;
                double JoPredve = 0;
                double JoPrebve = 0;
                double JoPreite = 0;
                double HKite = 0;
                double Joite = 0;
                
                for(int time = 1; time <= times; time++){
                    System.out.println("|V| = " + i + " bottleneck = " + bottleneck + " middle = " + middle + " time: " + time + "/" + times);
//                System.out.println("|V| = " + 2*i);
                    GraphGen euclideanGen = new GraphGen(5);
                    Graph graph = euclideanGen.generate(i,128,middle,0.01,bottleneck);

                    long starth = System.currentTimeMillis();
                    hop = new Hop_NoHash(graph);
                    hop.start();
                    long finishh = System.currentTimeMillis();
                    timeElapsedHop += finishh - starth;
                    int hopMatchCount = graph.matchCount();
                    assert graph.matchValidCheck();

                    graph.resetMatch();
                    for(Graph piece:graph.pieces){
                        piece.resetMatch();
                    }
                    changeGraph(graph);
                    assert graph.matchCount() == 0;

                    long startj = System.currentTimeMillis();
                    //Jocg_DB jocg = new Jocg_DB(graph);
                    jocg = new Jocg_Pointer(graph);
                    //Jocg_Hk jocg = new Jocg_Hk(graph);
                    jocg.start();
                    long finishj = System.currentTimeMillis();
                    timeElapsedJocg += finishj - startj;
                    int joMatchCount = graph.matchCount();
                    assert joMatchCount == hopMatchCount;
                    assert graph.matchValidCheck();

                    System.out.println(hop.iterate);
                    System.out.println(jocg.iterate);


                    edgeNum += graph.edgeNum;
                    matchNum += graph.matchCount();
                    
                    HKdve+=hop.dve;
                    Jodve+=jocg.dve;

                    HKbve+=hop.bve;
                    Jobve+=jocg.bve;
                    
                    HKite += hop.iterate;
                    Joite += jocg.iterate;

                    JoPrebve+=jocg.pre_bve;
                    JoPredve+=jocg.pre_dve;
                    JoPreite+=jocg.pre_ite;


                    timePreprocess+=jocg.preprocess;
                }

                timeElapsedHop /= times;
                timeElapsedJocg /= times;
                edgeNum /= times;
                matchNum /= times;

                timePreprocess/=times;
                HKdve/=times;
                Jodve/=times;
                HKbve/=times;
                Jobve/=times;
                HKite/=times;
                Joite/=times;

                JoPrebve/=times;
                JoPredve/=times;
                JoPreite/=times;

                double ratio = timeElapsedJocg/timeElapsedHop;
                System.out.println("average HKbve: " + HKbve);
                System.out.println("average HKdve: " + HKdve);

                System.out.println("average JoPrebve: " + JoPrebve);
                System.out.println("average JoPredve: " + JoPredve);
                System.out.println("average Jobve: " + Jobve);
                System.out.println("average Jodve: " + Jodve);

                System.out.println("average HKite: " + HKite);
                System.out.println("average Joite: " + Joite);
                System.out.println("average JoPreite: " + JoPreite);

                System.out.println("average HK.BFS/ite: " + HKbve/HKite);
                System.out.println("average HK.DFS/ite: " + HKdve/HKite);

                System.out.println("average Jo.Pre.BFS/ite: " + JoPrebve/JoPreite);
                System.out.println("average Jo.Pre.DFS/ite: " + JoPredve/JoPreite);
                System.out.println("average Jo.BFS/ite: " + Jobve/Joite);
                System.out.println("average Jo.DFS/ite: " + Jodve/Joite);


                System.out.println("average Edge number: " + edgeNum);
                System.out.println("average Matching number: " + matchNum);
                System.out.println("average Jocg: " + timeElapsedJocg);
                System.out.println("average Jocg Preprocess: " + timePreprocess);
                System.out.println("average Hop: " + timeElapsedHop);
                System.out.println("average Jocg/Hop: " + ratio);
                System.out.println("----------------------------------------------------------");


                writer.append("|V| = " + i + " bottleneck = " + bottleneck + " middle = " + middle + " times = " + times +'\n');
                writer.append("* average HK.bve: " + HKbve + "\n");
                writer.append("* average HK.dve: " + HKdve + "\n");
                writer.append("average JoPrebve: " + JoPrebve + "\n");
                writer.append("average JoPredve: " + JoPredve + "\n");
                writer.append("* average Jocg.bve: " + Jobve + "\n");
                writer.append("* average Jocg.dve: " + Jodve + "\n");


                writer.append("average HKite: " + HKite + "\n");
                writer.append("average Joite: " + Joite + "\n");
                writer.append("average JoPreite: " + JoPreite + "\n");

                writer.append("average HK.BFS/ite: " + HKbve/HKite + "\n");
                writer.append("average HK.DFS/ite: " + HKdve/HKite + "\n");


                writer.append("average Jo.Pre.BFS/ite: " + JoPrebve/JoPreite + "\n");
                writer.append("average Jo.Pre.DFS/ite: " + JoPredve/JoPreite + "\n");
                writer.append("average Jo.BFS/ite: " + Jobve/Joite + "\n");
                writer.append("average Jo.DFS/ite: " + Jodve/Joite + "\n");
                
                writer.append("average Edge number: " + edgeNum + "\n");
                writer.append("average Matching number: " + matchNum + "\n");
                writer.append("average Jocg: " + timeElapsedJocg + "\n");
                writer.append("average Jocg Preprocess: " + timePreprocess + "\n");
                writer.append("average Hop: " + timeElapsedHop + "\n");
                writer.append("average Jocg/Hop: " + ratio + "\n");
                //writer.append();
                writer.append("----------------------------------------------------------\n");
            }
        }
        writer.close();
    }


    private static void changeGraph(Graph g){
        for(Vertex v: g.vertices){
            v.piece = new Graph();
        }
        g.pieces = new ArrayList<>();
    }
}
