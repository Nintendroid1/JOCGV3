package com.company.test;

import com.company.algorithm.Algo;

public class Timer {

    public static long start(Algo algo){
        long starth = System.currentTimeMillis();
        algo.start();
        long finishh = System.currentTimeMillis();

        return finishh - starth;
    }
}
