package com.company.test;

import com.company.algorithm.*;
import com.company.element.EdgeMaker;
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
        int[] numV = {1000000};//,50000};
        int[] middles = {8, 16, 32, 64};

        //int[] parts = {14,16,18,20,22};//{1,2,3,4,5,6,8,10,12,14,16,18,20,22};

        int[] parts = {4};
        {//1,2,3,4,5,6,7,8,9,10,11,12,13};//14,16,18,20,22};

//        ArrayList<Integer> middles = new ArrayList<>();
//        for(int i = 2; i < 20; i++){
//            middles.add(i);
//        }
            int times = 1;
            double[] bottlenecks = {0.5};//{1,2,3,4,5,6,7,8,9};
            for (int i : numV) {
                for (double bottleneck : bottlenecks) {
                    for (int middle : parts) {

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
                        double JoPredve2 = 0;
                        double JoPrebve2 = 0;
                        double JoPreite = 0;
                        double HKite = 0;
                        double Joite = 0;
                        double HK_DVE2 = 0;
                        double HK_BVE2 = 0;
                        double Jo_DVE2 = 0;
                        double Jo_BVE2 = 0;
                        double JDL = 0;
                        double delTime = 0;

                        for (int time = 1; time <= times; time++) {
                            System.out.println("|V| = " + i + " bottleneck = " + bottleneck + " part = " + middle + " time: " + time + "/" + times);
//                System.out.println("|V| = " + 2*i);
                            GraphGen euclideanGen = new GraphGen(5);
                            Graph graph = euclideanGen.generate(i, 128);
                            EdgeMaker edgeMaker = new EdgeMaker(graph);
                            edgeMaker.reEdgesWeights(bottleneck, middle, 128, 0.01,true);

                            //Graph graph = euclideanGen.generate_approx(i,100,middle,bottleneck);

                            long starth = System.currentTimeMillis();
                            hop = new Hop_NoHash(graph);
                            hop.start();
                            long finishh = System.currentTimeMillis();
                            timeElapsedHop += finishh - starth;
                            int hopMatchCount = graph.matchCount();
                            assert graph.matchValidCheck();

                            graph.reset(true);
                            for (Graph piece : graph.pieces) {
                                piece.reset(true);
                            }
                            //changeGraph(graph);
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

                            HKdve += hop.dve;
                            Jodve += jocg.dve;

                            HKbve += hop.bve;
                            Jobve += jocg.bve;

                            HKite += hop.iterate;
                            Joite += jocg.iterate;

                            JoPrebve += jocg.pre_bve;
                            JoPredve += jocg.pre_dve;
                            JoPreite += jocg.pre_ite;

                            HK_BVE2 += hop.bve2;
                            HK_DVE2 += hop.dve2;
                            Jo_BVE2 += jocg.bve2;
                            Jo_DVE2 += jocg.dve2;

                            JoPrebve2 += jocg.pre_bve2;
                            JoPredve2 += jocg.pre_dve2;

                            JDL = jocg.del;


                            timePreprocess += jocg.preprocess;

                            delTime += jocg.delTime;
                        }

                        timeElapsedHop /= times;
                        timeElapsedJocg /= times;
                        edgeNum /= times;
                        matchNum /= times;

                        timePreprocess /= times;
                        HKdve /= times;
                        Jodve /= times;
                        HKbve /= times;
                        Jobve /= times;
                        HKite /= times;
                        Joite /= times;
                        delTime /= times;

                        JoPrebve /= times;
                        JoPredve /= times;
                        JoPreite /= times;

                        HK_BVE2 /= times;
                        HK_DVE2 /= times;
                        Jo_BVE2 /= times;
                        Jo_DVE2 /= times;
                        JoPrebve2 /= times;
                        JoPredve2 /= times;
                        JDL /= times;

                        double ratio = timeElapsedJocg / timeElapsedHop;
                        System.out.println("average HB: " + HKbve);
                        System.out.println("average HD: " + HKdve);
                        System.out.println("average HB2: " + HK_BVE2);
                        System.out.println("average HD2: " + HK_DVE2);

                        System.out.println("average JPB: " + JoPrebve);
                        System.out.println("average JPD: " + JoPredve);
                        System.out.println("average JPB2: " + JoPrebve2);
                        System.out.println("average JPD2: " + JoPredve2);
                        System.out.println("average JB: " + Jobve);
                        System.out.println("average JD: " + Jodve);
                        System.out.println("average JB2: " + Jo_BVE2);
                        System.out.println("average JD2: " + Jo_DVE2);
                        System.out.println("average JDL: " + JDL);

                        System.out.println("average HI: " + HKite);
                        System.out.println("average JI: " + Joite);
                        System.out.println("average JPI: " + JoPreite);

                        System.out.println("average HB/I: " + HKbve / HKite);
                        System.out.println("average HD/I: " + HKdve / HKite);

                        System.out.println("average JPB/I: " + JoPrebve / JoPreite);
                        System.out.println("average JPD/I: " + JoPredve / JoPreite);
                        System.out.println("average JB/I: " + Jobve / Joite);
                        System.out.println("average JD/I: " + Jodve / Joite);


                        System.out.println("average Edge number: " + edgeNum);
                        System.out.println("average Matching number: " + matchNum);
                        System.out.println("average Jocg: " + timeElapsedJocg);
                        System.out.println("average Jocg Preprocess: " + timePreprocess);
                        System.out.println("average DT: " + delTime);
                        System.out.println("average Hop: " + timeElapsedHop);
                        System.out.println("average J/H: " + ratio);
                        System.out.println("----------------------------------------------------------");


                        writer.append("|V| = " + i + " bottleneck = " + bottleneck + " middle = " + middle + " times = " + times + '\n');
                        writer.append("average HB: " + HKbve + "\n");
                        writer.append("average HD: " + HKdve + "\n");
                        writer.append("average HB2: " + HK_BVE2 + "\n");
                        writer.append("average HD2: " + HK_DVE2 + "\n");

                        writer.append("average JPB: " + JoPrebve + "\n");
                        writer.append("average JPD: " + JoPredve + "\n");
                        writer.append("average JPB2: " + JoPrebve2 + "\n");
                        writer.append("average JPD2: " + JoPredve2 + "\n");
                        writer.append("average JB: " + Jobve + "\n");
                        writer.append("average JD: " + Jodve + "\n");
                        writer.append("average JB2: " + Jo_BVE2 + "\n");
                        writer.append("average JD2: " + Jo_DVE2 + "\n");
                        writer.append("average JDL: " + JDL + "\n");


                        writer.append("average HI: " + HKite + "\n");
                        writer.append("average JI: " + Joite + "\n");
                        writer.append("average JPI: " + JoPreite + "\n");

                        writer.append("average HB/I: " + HKbve / HKite + "\n");
                        writer.append("average HD/I: " + HKdve / HKite + "\n");

                        writer.append("average HB2/I: " + HK_BVE2 / HKite + "\n");
                        writer.append("average HD2/I: " + HK_DVE2 / HKite + "\n");


                        writer.append("average JPB/I: " + JoPrebve / JoPreite + "\n");
                        writer.append("average JPD/I: " + JoPredve / JoPreite + "\n");
                        writer.append("average JB/I: " + Jobve / Joite + "\n");
                        writer.append("average JD/I: " + Jodve / Joite + "\n");

                        writer.append("average JB2/I: " + Jo_BVE2 / Joite + "\n");
                        writer.append("average JD2/I: " + Jo_DVE2 / Joite + "\n");

                        writer.append("average Edge number: " + edgeNum + "\n");
                        writer.append("average Matching number: " + matchNum + "\n");
                        writer.append("average Jocg: " + timeElapsedJocg + "\n");
                        writer.append("average DT: " + delTime + "\n");
                        writer.append("average Jocg Preprocess: " + timePreprocess + "\n");
                        writer.append("average Hop: " + timeElapsedHop + "\n");
                        writer.append("average Jocg/Hop: " + ratio + "\n");
                        //writer.append();
                        writer.append("----------------------------------------------------------\n");
                    }
                }
            }
            writer.close();
        }

    }
    private static void changeGraph(Graph g){
        for(Vertex v: g.vertices){
            v.piece = new Graph();
        }
        g.pieces = new ArrayList<>();
    }

}
