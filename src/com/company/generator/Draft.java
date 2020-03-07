package com.company.generator;

public class Draft {

    public static void sum(){
        /*
         * suppose there is an array with N element
         * there is also an ArrayList<int[]>
         * suppose for each int[2] "region" in ArrayList
         * region = {a,b}
         * then it means from array[a] to array[b] (include head and end), all elements need to plus 1
         * this is an algorithm doing this task in O(N)
         *
         * heads = region.all([0])
         * tails = region.all([1])
         * heads.sort()
         * tails.sort()
         * count = 0;
         * for i in len(array)
         *      while heads.top() == i:
         *          heads.pop()
         *          count+=1
         *      while tails.top() == i
         *          tails.pop()
         *          count-=1
         *      array[i] += count
         */
    }
}
