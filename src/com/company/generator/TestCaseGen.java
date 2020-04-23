package com.company.generator;

import com.company.algorithm.Hop_NoHash;
import com.company.algorithm.Jocg_Pointer;
import com.company.element.Graph;
import com.company.element.Point;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestCaseGen {
    public static void generate() throws IOException {
        int[] numv = new int[]{100,1000,5000,10000,50000,100000,500000,1000000};
        for(int nv:numv){
            for(int i = 0; i < 10; i++){
                GraphGen graphGen = new GraphGen((int)System.currentTimeMillis() + i);
                Graph graph = graphGen.generate(nv,128);
                BufferedWriter writer = new BufferedWriter(new FileWriter(nv+"_"+i+".txt"));
                writer.write(nv + " " + i + "\n");
                for(Point p:graph.points){
                    writer.append(p.x + " " + p.y + "\n");
                }
                writer.close();

            }
        }
    }
}
