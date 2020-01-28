package com.company.test;

import com.company.algorithm.Hop;
import com.company.algorithm.Jocg;
import com.company.algorithm.Jocg_DB;
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
        int[] numV = {5000};//,50000};
        int times = 1;
        double bottleneck = 5;
        for(int i:numV){
            double edgeNum = 0;
            double matchNum = 0;
            double timeElapsedHop = 0;
            double timeElapsedJocg = 0;

            for(int time = 1; time <= times; time++){
                System.out.println("|V| = " + 2*i + " bottleneck = " + bottleneck + " " + "time: " + time + "/" + times);
//                System.out.println("|V| = " + 2*i);
                EuclideanGen euclideanGen = new EuclideanGen(5);
                Graph graph = euclideanGen.generate(i,80,20,0.01,bottleneck);

//                long starth = System.currentTimeMillis();
//                Hop hop = new Hop(graph);
//                hop.start();
//                long finishh = System.currentTimeMillis();
//                timeElapsedHop += finishh - starth;
//                int hopMatchCount = graph.matchCount();
//
//                assert graph.matchValidCheck();
//                graph.resetMatch();
//                //matchingTable_Hop.printMatching();
//                assert graph.matchCount() == 0;

                long startj = System.currentTimeMillis();
                Jocg jocg = new Jocg(graph);
                jocg.start();
                long finishj = System.currentTimeMillis();
                timeElapsedJocg += finishj - startj;
                int joMatchCount = graph.matchCount();

                System.out.println(jocg.iterate);
//                System.out.println(hop.iterate);
//                assert joMatchCount == hopMatchCount;
                assert graph.matchValidCheck();

                //assert hop.iterate >= jocg.iterate;

                edgeNum += graph.edgeNum;
                matchNum += graph.matchCount();

            }
            timeElapsedHop /= times;
            timeElapsedJocg /= times;
            edgeNum /= times;
            matchNum /= times;

            double ratio = timeElapsedJocg/timeElapsedHop;
            System.out.println("average Edge number: " + edgeNum);
            System.out.println("average Matching number: " + matchNum);
            System.out.println("average Jocg: " + timeElapsedJocg);
            System.out.println("average Hop: " + timeElapsedHop);
            System.out.println("average Jocg/Hop: " + ratio);
            System.out.println("----------------------------------------------------------");


            writer.append("|V| = " + 2*i + " bottleneck = " + bottleneck + " times = " + times +'\n');
            writer.append("average Edge number: " + edgeNum + "\n");
            writer.append("average Matching number: " + matchNum + "\n");
            writer.append("average Jocg: " + timeElapsedJocg + "\n");
            writer.append("average Hop: " + timeElapsedHop + "\n");
            writer.append("average Jocg/Hop: " + ratio + "\n");
            //writer.append();
            writer.append("----------------------------------------------------------\n");
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

//Graph graph = euclideanGen.generateNoPiece(i,5,5);
//TestGraph.ifValid(graph);

//                EuclideanGen euclideanGen = new EuclideanGen(100);
//                Graph graph = euclideanGen.generate("C:\\Users\\yjc38\\CondaCode\\JOCG\\output_" + i + ".txt",5,i);
