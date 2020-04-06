package com.company;

import com.company.element.ExperimentList;
import com.company.test.FindBottle;
import com.company.test.TestEdgeMaker;
import com.company.test.TestEuclidean;
import com.company.test.TestPerformance;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
	    // write your code here
//        TestEuclidean.test();
        //TestPerformance.test1();
//        FindBottle findBottle = new FindBottle();
//        findBottle.find();
        //TestEdgeMaker.test();

//        File file = new File("TestManager");
//        BufferedWriter writer = new BufferedWriter(new FileWriter("TestManagerOut"));
//        writer.write("");
//        //System.out.println(file.getAbsolutePath());
//        Scanner scanner = new Scanner(file);
//        String head = scanner.nextLine();
//        if(head == null){
//            writer.write("Wrong Head!\n");
//            writer.close();
//            return;
//        }
//        HashMap<String, String> param = new HashMap<>();
//
//        String line = scanner.nextLine();
//        while (line != null){
//            String[] lineArr = line.split("");
//            param.put(lineArr[0],lineArr[1]);
//            line = scanner.nextLine();
//        }
//
//        if(head.equals("findBottle")){
//            FindBottle findBottle = new FindBottle();
//            findBottle.resetParam(param);
//            findBottle.find();
//        }else if(head.equals("graphMatching")){
//
//        }
        multiTask();
        //singleTask();

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

    public static void multiTask() throws Exception{
        ParallelTasks tasks = new ParallelTasks();
        int nvs[] = {1000000};//{1000,5000,10000};//,50000,100000,500000,1000000};
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

//        final Runnable waitOneSecond = new Runnable() {
//            public void run()
//            {
//                FindBottle findBottle = new FindBottle(10000);
//                ExperimentList.Experiment[] experiments = findBottle.find();
//                try {
//                    printExperiment(experiments);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };

        for(int n :nvs){
            for(int i = 0; i < 12; i++){
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
        for(int i = 0; i < 12; i++){
            FindBottle findBottle = new FindBottle();
            findBottle.find();
        }
        System.err.println(System.currentTimeMillis() - start);
    }
}
