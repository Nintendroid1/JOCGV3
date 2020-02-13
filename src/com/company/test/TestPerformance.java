package com.company.test;

import com.company.element.Label;
import com.company.element.Vertex;

import java.util.ArrayList;
import java.util.LinkedList;

public class TestPerformance {
    public static void test1(){
        ArrayList<Vertex> vertices = new ArrayList<>();
        for(int i = 0; i < 5000000; i++){
            vertices.add(new Vertex(i, Label.A));
        }
        long start;
        long end;


        start = System.currentTimeMillis();
        ArrayList<Vertex> array = new ArrayList<>();
        for(Vertex v:vertices){
            array.add(v);
        }
        end = System.currentTimeMillis();

        System.out.println(end - start);


        start = System.currentTimeMillis();
        LinkedList<Vertex> link = new LinkedList<>();
        for(Vertex v:vertices){
            link.addLast(v);
        }
        end = System.currentTimeMillis();

        System.out.println(end - start);


        start = System.currentTimeMillis();
        while (!array.isEmpty()){
            array.remove(array.size()-1);
        }
        end = System.currentTimeMillis();

        System.out.println(end - start);


        start = System.currentTimeMillis();
        while (!link.isEmpty()){
            link.pop();
        }
        end = System.currentTimeMillis();

        System.out.println(end - start);
    }
    
}
