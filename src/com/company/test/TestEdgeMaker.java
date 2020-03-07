package com.company.test;

import com.company.element.EdgeMaker;
import com.company.element.Graph;
import com.company.element.Point;
import com.company.generator.GraphGen;

public class TestEdgeMaker {

    public static void test(){
        Graph graph = new GraphGen(5).generate(1000000,128);
        EdgeMaker em = new EdgeMaker(graph.points);
        double bottleneck = 0.1;
        em.reEdges(bottleneck);
        completeTest(graph,bottleneck);
        System.out.println("done");
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
