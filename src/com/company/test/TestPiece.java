package com.company.test;

import com.company.algorithm.Hop_NoHash;
import com.company.algorithm.Jocg_Pointer;
import com.company.element.EdgeMaker;
import com.company.element.ExperimentList;
import com.company.element.Graph;
import com.company.generator.GraphGen;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
//        int standard_partN = (int)Math.pow(numV,1.0/6.0);
        for(int i = 1; i <= 50; i+=2){
            int partN =i;

            if(largeG/partN <= bottle){
                break;
            }
//            System.out.println(i + "ssssssssssssssssssssssssssss");
            edgeMaker.changeShift(bottle,partN,largeG,smallG,true);
            Jocg_Pointer jocg = new Jocg_Pointer(graph);
            expe.add(bottle,jocg.dr);
            jocg.dr.set(jocg.dr.number_of_piece,Math.pow((partN+1),2));
            jocg.start();
        }
        expe.printResult("both");

    }

    public void test2() throws IOException {
        int[] numv = new int[]{100,1000,5000};//,10000,50000,100000,500000,1000000,1500000};
        double largeG = 128;
        double smallG = 0.01;

        File file = new File("Bottlenecks.txt");
        HashMap<Integer,double[]> table = new HashMap<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()){
            String str = scanner.nextLine();
            String[] strings = str.split(" ");
            if(strings.length != 3){
                continue;
            }
            int nv = Integer.parseInt(strings[0]);
            int idx = Integer.parseInt(strings[1]);
            double bottle = Double.parseDouble(strings[2]);
            if(!table.containsKey(nv)){
                table.put(nv,new double[10]);
            }
            table.get(nv)[idx] = bottle;
            //System.out.println(str);
        }
        scanner.close();




        for(int nv:numv){
            for(int i = 0; i < 10; i++){
                String folderPath = String.format("./Test_Data/%d_%d.txt",nv,i);
                System.out.println(folderPath);
                Graph graph = GraphGen.generate(new File(folderPath));
                EdgeMaker edgeMaker = new EdgeMaker(graph);
                double bottle = table.get(nv)[i];
                ExperimentList.Experiment expe = new ExperimentList.Experiment();


                for(int j = 1; j <= 50; j+=1){
                    int partN =j;

                    if(largeG/partN <= bottle){
                        break;
                    }
                    edgeMaker.reEdgesFixShift(bottle,partN,largeG,smallG,true);
                    Jocg_Pointer jocg = new Jocg_Pointer(graph);
                    expe.add(bottle,jocg.dr);
                    jocg.dr.set(jocg.dr.number_of_piece,Math.pow((partN+1),2));
                    jocg.start();
                    int a = 0;
                }
                expe.printResult(String.format("PieceTest_%d_%d",nv,i));
            }
        }
    }

}
