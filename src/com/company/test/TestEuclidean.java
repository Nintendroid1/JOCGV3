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

//    public static void testFileGen(){
//        EuclideanGen euclideanGen = new EuclideanGen(100);
//        //Graph graph = euclideanGen.generate("C:\\Users\\yjc38\\CondaCode\\JOCG\\output.txt",1,5000);
//        int a = 0;
//    }

    public static void test() throws IOException {


        BufferedWriter writer = new BufferedWriter(new FileWriter("EculideanOut.txt"));
        writer.write("");


        int tenK = 10000;
        int[] numV = {5*tenK};//5*tenK,8*tenK
        double[] bottlenecks = {1,0.1,0.01};
        int times = 20;

        double sizeLargeGrid = 80;
        double sizeMiddleGrid = 20;
        double sizeSmallGrid = 0.01;




        for(int numv:numV){
            for(double bottleneck:bottlenecks){
                double edgeNum = 0;
                double matchNum = 0;
                double timeElapsedHop = 0;
                double timeElapsedJocg = 0;

                for(int time = 1; time <= times; time++){
                    System.out.println("|V| = " + 2*numv + " bottleneck = " + bottleneck + " " + "time: " + time + "/" + times);
                    EuclideanGen euclideanGen = new EuclideanGen(time);
                    Graph graph = euclideanGen.generate(numv,sizeLargeGrid,sizeMiddleGrid,sizeSmallGrid,bottleneck);

                    Hop hop = new Hop(graph);
                    timeElapsedHop += Timer.start(hop);
                    int hopMatchCount = graph.matchCount();

                    graph.resetMatch();

                    Jocg jocg = new Jocg(graph);
                    timeElapsedJocg +=Timer.start(jocg);
                    int joMatchCount = graph.matchCount();
                    assert joMatchCount == hopMatchCount;

                    System.out.println("Hop total ite num: " + hop.iterate);
                    System.out.println("Jocg total ite num: " + jocg.iterate);

                    System.out.println("Edge num: " + graph.edgeNum);
                    System.out.println("Matching num: " + graph.matchCount());

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


                writer.append("|V| = " + 2*numv + " bottleneck = " + bottleneck + " times = " + times +'\n');
                writer.append("average Edge number: " + edgeNum + "\n");
                writer.append("average Matching number: " + matchNum + "\n");
                writer.append("average Jocg: " + timeElapsedJocg + "\n");
                writer.append("average Hop: " + timeElapsedHop + "\n");
                writer.append("average Jocg/Hop: " + ratio + "\n");
                //writer.append();
                writer.append("----------------------------------------------------------\n");


            }
        }
        writer.close();
    }

//    public static void testGen(){
//        for(int i = 1000; i < 3000; i+=100){
//            EuclideanGen euclideanGen = new EuclideanGen(2131);
//            Graph graph = euclideanGen.generate(i,80,40,0.5,4,10000);
//            int smallestSub = Integer.MAX_VALUE;
//            for(Graph sub:graph.pieces){
//                if(sub.vertices.size() < smallestSub){
//                    smallestSub = sub.vertices.size();
//                }
//            }
//            System.out.println(smallestSub + " " + graph.vertices.size());
//            System.out.println(i + ": "+(double) smallestSub/graph.vertices.size()*100 + "%");
//            int a = 0;
//        }
//    }
}


//                EuclideanGen euclideanGen = new EuclideanGen(100);
//                Graph graph = euclideanGen.generate("C:\\Users\\yjc38\\CondaCode\\JOCG\\output_" + i + ".txt",5,i);
