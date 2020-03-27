package com.company.test;

import com.company.element.EdgeMaker;
import com.company.element.Graph;
import com.company.element.Point;
import com.company.generator.GraphGen;

import java.util.Random;

public class TestEdgeMaker {

    public static void test(){
        long start, end;

        Graph graph = new GraphGen(5321312).generate(50000,128);
        EdgeMaker em = new EdgeMaker(graph);
        em.graph = graph;
        double bottleneck = 3;
        start = System.currentTimeMillis();
        em.reEdgesWeights(bottleneck,4,128,0.1,true);
        end = System.currentTimeMillis();

        System.out.println("weighted version takes: " + (end-start));

        start = System.currentTimeMillis();
        em.reEdges(bottleneck,true);
        end = System.currentTimeMillis();

        System.out.println("unweighted version takes: " + (end-start));

        randomeSample(graph,bottleneck,100000);
        //completeTest(graph,bottleneck);
        System.out.println("done");
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
        for(Point s:graph.points){
            for(Point t:graph.points){
                if(s.v.id == t.v.id || s.v.label == t.v.label){
                    continue;
                }
                double x = s.x - t.x;
                double y = s.y - t.y;
                double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                if(d <= bottleneck){
                    if(!(s.v.edges.contains(t.v) && t.v.edges.contains(s.v))){
                        assert  false;
                    }
                }
            }
        }
        return true;
    }
}
