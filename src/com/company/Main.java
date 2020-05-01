package com.company;

import com.company.element.ExperimentList;
import com.company.element.Graph;
import com.company.generator.GraphGen;
import com.company.generator.TestCaseGen;
import com.company.test.*;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Main {



    public static void main(String[] args) throws Exception {
	    // write your code here

        //multiTask();
//        piectMultiTask();
        //singleTask();
//        TestCaseGen.generate();
        TestEdgeMaker.test2();
        //mainExpe(args);
    }

    public static void mainExpe(String[] args) throws Exception {
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);
        int[] numv = new int[]{100,1000,5000,10000,50000,100000,500000,1000000};
        for(int nv:numv){
            for(int i = start; i < end; i++){
                String folderPath = String.format("./Test_Data/%d_%d.txt",nv,i);
                System.out.println(folderPath);
                Graph graph = GraphGen.generate(new File(folderPath));
                FindBottle findBottle = new FindBottle(nv);
                ExperimentList.Experiment ex = null;
                String name = "";
                if(args[0].equals("H")){
                    ex = findBottle.find_HK(graph);
                    name = "ResultHK";
                }
                else if(args[0].equals("J")){
                    ex = findBottle.find_Jocg(graph);
                    name = "ResultJocg";
                }
                else{
                    throw new Exception();
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("%s_%d_%d.txt",name,nv,i)));
                writer.write("");
                ex.writeResult(writer);
                writer.close();
            }
        }
    }

    public static void printExperiment(ExperimentList.Experiment[] experiments) throws IOException {
        BufferedWriter writer
                = new BufferedWriter(new FileWriter("EXP_"+System.currentTimeMillis()
                        +Thread.currentThread().getName()
                        +".txt"));
        writer.write("");
        for(int i = 0; i < 2; i++){
            if(experiments[i] == null){
                break;
            }
            ExperimentList.Experiment ex = experiments[i];
            if(i == 0){
                writer.append("HK_Start\n");
            }
            else{
                writer.append("JOCG_Start\n");
            }
            ex.writeResult(writer);
        }
        writer.close();
    }

    public static void piectMultiTask() throws Exception{
        ParallelTasks tasks = new ParallelTasks();
        class OneShotTask implements Runnable {
            int numv;
            OneShotTask(int n) { numv = n; }
            public void run() {
                TestPiece testPiece = new TestPiece();
                try {
                    testPiece.test(numv);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for(int n = 0; n < 10; n++){
            tasks.add(new OneShotTask(1000000));
            Thread.sleep(50);
        }

        final long start = System.currentTimeMillis();
        tasks.go();
        System.err.println(System.currentTimeMillis() - start);
        //

    }
    public static void multiTask() throws Exception{
        ParallelTasks tasks = new ParallelTasks();
        int nvs[] = {50000};//{1000,5000,10000};//,50000,100000,500000,1000000};
        class OneShotTask implements Runnable {
            int numv;
            OneShotTask(int n) { numv = n; }
            public void run() {
                FindBottle findBottle = new FindBottle(numv);
                ExperimentList.Experiment[] experiments = findBottle.find();
                try {
                    printExperiment(experiments);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for(int n :nvs){
            for(int i = 0; i < 10; i++){
                tasks.add(new OneShotTask(n));
                Thread.sleep(50);
            }
        }

        final long start = System.currentTimeMillis();
        tasks.go();
        System.err.println(System.currentTimeMillis() - start);
    }

    public static void singleTask(){
        long start = System.currentTimeMillis();
        int numV[] = new int[]{10000,50000,100000,500000};//,100000,500000};
        for(int n:numV){
            for(int i = 0; i < 5; i++){
                FindBottle findBottle = new FindBottle(n);
                ExperimentList.Experiment[] experiments = findBottle.find();
                try {
                    printExperiment(experiments);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                experiments = findBottle.find_reverse();
                try {
                    printExperiment(experiments);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.err.println(System.currentTimeMillis() - start);
    }

    public static void listFile(){
        String folderPath = "./Test_Data";
        File folder = new File(folderPath);
        String[] files = folder.list();

        for (String fileStr: files)
        {
            GraphGen graphGen = new GraphGen(1);
            String filePath = folderPath+"/"+fileStr;
            File file = new File(filePath);
            System.out.println(filePath);
//            Graph graph = graphGen.generate(file);
//            int a = 0;
        }
    }
}
