package com.company.element;

import com.company.generator.GraphGen;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DataStructure {
    public static class CycleArray<E>{
        E[] array;
        int head;
        int tail;
        int size;
        int capacity;
        public CycleArray(int n){
            capacity = n;
            head = 0;
            tail = 0;
            size = 0;
            array = (E[]) new Object[capacity];

        }

        public boolean addFirst(E v){
            if(size >= capacity){
                expand();
            }
            array[head] = v;
            head = head - 1;
            if(head < 0){
                head = head + capacity;
            }
            size+=1;
            return true;
        }

        public boolean addLast(E v){
            if(size >= capacity){
                expand();
            }
            tail = (tail + 1)%capacity;
            array[tail] = v;
            size+=1;
            return true;
        }

        public void expand(){
            E[] temp = (E[])new Object[2*capacity];
            System.arraycopy(array,0,temp,1,array.length);
            head = 0;
            tail = array.length;
            capacity = 2*capacity;
            array = temp;
        }

        public E pop(){
            if(size <= 0){
                return null;
            }
            head = (head + 1)%capacity;
            size-=1;
            return array[head];
        }

        public boolean isEmpty(){
            return size == 0;
        }
    }

    public static class Stage{
        public int bfs;
        public int dfs;
        public Stage(int bfs,int dfs){
            this.bfs = bfs;
            this.dfs = dfs;
        }
        public boolean equal(Stage s){
            return s.bfs == bfs && s.dfs == dfs;
        }

        public boolean equal(int b, int d){
            return b == bfs && d == dfs;
        }
    }

    public static class DisV{
        public int d;
        public Vertex v;
        public DisV(Vertex v, int d){
            this.v = v;
            this.d = d;
        }
    }

}
