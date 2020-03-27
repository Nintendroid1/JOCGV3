package com.company;

import com.company.test.FindBottle;
import com.company.test.TestEdgeMaker;
import com.company.test.TestEuclidean;
import com.company.test.TestPerformance;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
	    // write your code here
        //TestEuclidean.test();
        //TestPerformance.test1();
        FindBottle findBottle = new FindBottle();
        findBottle.find();
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

    }
}
