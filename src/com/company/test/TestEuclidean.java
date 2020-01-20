package com.company.test;

import com.company.algorithm.Hop;
import com.company.algorithm.Jocg;
import com.company.algorithm.Jocg_ND;
import com.company.algorithm.Jocg_OPT;
import com.company.element.Graph;
import com.company.generator.EuclideanGen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestEuclidean {
    public TestEuclidean(){

    }

    public static void testFileGen(){
        EuclideanGen euclideanGen = new EuclideanGen(100);
        Graph graph = euclideanGen.generate("C:\\Users\\yjc38\\CondaCode\\JOCG\\output.txt",1,5000);
        int a = 0;
    }

    public static void test() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("EculideanOut.txt"));
        writer.write("");
        for(int i = 30000; i <= 30000; i+=1000){
            double timeElapsedHop = 0;
            double timeElapsedJocg = 0;
            int times = 20;
            for(int time = 0; time < times; time++){
                System.out.println("|V| = " + 2*i);
                EuclideanGen euclideanGen = new EuclideanGen(100);
                Graph graph = euclideanGen.generate(i,80,20,0.01,1);
                //TestGraph.ifValid(graph);

//                EuclideanGen euclideanGen = new EuclideanGen(100);
//                Graph graph = euclideanGen.generate("C:\\Users\\yjc38\\CondaCode\\JOCG\\output_" + i + ".txt",5,i);

                long starth = System.currentTimeMillis();
                Hop hop = new Hop(graph);
                hop.start();
                long finishh = System.currentTimeMillis();
                timeElapsedHop += finishh - starth;
                int hopMatchCount = graph.matchCount();

                graph.resetMatch();
                //matchingTable_Hop.printMatching();
                assert graph.matchCount() == 0;

                graph.resetMatch();
                long startj = System.currentTimeMillis();
                Jocg jocg = new Jocg(graph);
                jocg.start();
                long finishj = System.currentTimeMillis();
                timeElapsedJocg += finishj - startj;
                int joMatchCount = graph.matchCount();
                System.out.println(hop.iterate);
                System.out.println(jocg.iterate);
                assert joMatchCount == hopMatchCount;
                //assert hop.iterate >= jocg.iterate;



            }
            timeElapsedHop /= times;
            timeElapsedJocg /= times;

            double ratio = timeElapsedJocg/timeElapsedHop;
            System.out.println("Jocg: " + timeElapsedJocg);
            System.out.println("Hop: " + timeElapsedHop);
            System.out.println("Jocg/Hop: " + ratio);
            writer.append(i + " " + ratio + '\n');

            System.out.println("----------------------------------------------------------");
        }
        writer.close();
    }

    public static void testGen(){
        for(int i = 1000; i < 3000; i+=100){
            EuclideanGen euclideanGen = new EuclideanGen(2131);
            Graph graph = euclideanGen.generate(i,80,40,0.5,4);
            int smallestSub = Integer.MAX_VALUE;
            for(Graph sub:graph.pieces){
                if(sub.vertices.size() < smallestSub){
                    smallestSub = sub.vertices.size();
                }
            }
            System.out.println(smallestSub + " " + graph.vertices.size());
            System.out.println(i + ": "+(double) smallestSub/graph.vertices.size()*100 + "%");
            int a = 0;
        }
    }
}
