package com.company.test;

import com.company.algorithm.Hop_NoHash;
import com.company.algorithm.Jocg_Pointer;
import com.company.element.EdgeMaker;
import com.company.element.ExperimentList;
import com.company.element.Graph;
import com.company.generator.GraphGen;

import java.io.IOException;
import java.util.ArrayList;

public class TestPiece {

    public void test(int n) throws IOException {
        int numV = n;
        double largeG = 128;
        double smallG = 0.01;
        double bottle;
        GraphGen graphGen = new GraphGen(32+(int)System.currentTimeMillis()+(int)Thread.currentThread().getId());
        Graph graph = graphGen.generate(numV,largeG);

        ExperimentList.Experiment expe = new ExperimentList.Experiment();

        FindBottle findBottle = new FindBottle(numV);
        bottle = findBottle.find(graph);
        System.out.println(bottle);

        EdgeMaker edgeMaker = new EdgeMaker(graph);
        graph.reset(true);
        Hop_NoHash hop = new Hop_NoHash(graph);
        expe.add(bottle,hop.dr);
        hop.start();
        int standard_partN = (int)Math.pow(numV,1.0/6.0);
        for(int i = 1; i <= 2*standard_partN; i+=2){
            int partN =i;
            edgeMaker.changeShift(bottle,partN,largeG,smallG,true);
            Jocg_Pointer jocg = new Jocg_Pointer(graph);
            expe.add(bottle,jocg.dr);
            jocg.dr.set(jocg.dr.number_of_piece,Math.pow((partN+1),2));
            jocg.start();
        }
        expe.printResult("both");

    }

}
