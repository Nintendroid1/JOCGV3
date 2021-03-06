package com.company.test;

import com.company.element.EdgeMaker;
import com.company.element.ExperimentList;
import com.company.element.Graph;
import com.company.element.Point;
import com.company.generator.GraphGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Random;

public class TestEdgeMaker {

    public static void test(){
        long start, end;
        Graph graph = new GraphGen(5321312).generate(10000,128);
        EdgeMaker em = new EdgeMaker(graph);
        em.graph = graph;
        double bottleneck = 1;
        start = System.currentTimeMillis();
        em.reEdgesFixShift(bottleneck,4,128,0.1,true);
        completeTest(graph,bottleneck);
        end = System.currentTimeMillis();

        System.out.println("weighted version takes: " + (end-start));

        start = System.currentTimeMillis();
        completeTest(graph,bottleneck);
        em.reEdges(bottleneck,true);
        end = System.currentTimeMillis();

        System.out.println("unweighted version takes: " + (end-start));

        randomeSample(graph,bottleneck,100000);
        //completeTest(graph,bottleneck);
        System.out.println("done");
    }

    public static void test2() throws FileNotFoundException {
        long start, end;
        int[] numv = new int[]{100,1000,5000,10000};//,50000,100000,500000,1000000};
        for(int nv:numv){
            for(int i = 0; i < 10; i++){

                String folderPath = String.format("./Test_Data/%d_%d.txt",nv,i);
                System.out.println(folderPath);
                Graph graph = GraphGen.generate(new File(folderPath));
                FindBottle findBottle = new FindBottle(nv);
                ExperimentList.Experiment ex = null;
                String name = "";
                ex = findBottle.find_Jocg(graph);
                double bottleneck = ex.getFinalBottleNeck();
                EdgeMaker em = new EdgeMaker(graph);
                em.cleanEdges(true);

                System.out.print("nv:/" + nv+"/");

                start = System.currentTimeMillis();
                em.reEdges(bottleneck,false);
                end = System.currentTimeMillis();
                System.out.print((end - start) + "/");

                em.cleanEdges(true);

                start = System.currentTimeMillis();
                completeTest(graph,bottleneck);
                end = System.currentTimeMillis();
                System.out.println((end - start));

            }
        }
    }


    public static boolean randomeSample(Graph graph, double bottleneck, int sampleN){
        Random r = new Random(System.currentTimeMillis());
        for(int i = 0 ; i < sampleN; i++){
            int source = r.nextInt(graph.vertices.size()/2);
            int target = r.nextInt(graph.vertices.size()/2)+graph.vertices.size()/2;
            Point s = graph.vertices.get(source).point;
            Point t = graph.vertices.get(target).point;

            double x = s.x - t.x;
            double y = s.y - t.y;
            double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
            if(d <= bottleneck){
                if(!(s.v.edges.contains(t.v) && t.v.edges.contains(s.v))){
                    assert  false;
                }
            }
        }
        return true;
    }


    public static boolean completeTest(Graph graph, double bottleneck){
        for(int i = 0; i <graph.points.size(); i++){
            for(int j = i + 1; j <graph.points.size(); j++){
                Point s = graph.points.get(i);
                Point t = graph.points.get(j);
                if(s.v.id == t.v.id || s.v.label == t.v.label){
                    continue;
                }
                double x = s.x - t.x;
                double y = s.y - t.y;
                double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                if(d <= bottleneck){
                    s.v.edges.add(t.v);
                    t.v.edges.add(s.v);
//                    if(!(s.v.edges.contains(t.v) && t.v.edges.contains(s.v))){
//                        assert  false;
//                    }

                }
            }
        }
        return true;
    }
}
