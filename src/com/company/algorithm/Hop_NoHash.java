package com.company.algorithm;

import com.company.element.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class Hop_NoHash extends Algo{
    public Graph graph;
    private Integer INF;
    public boolean print;
    public Integer mode;
    public boolean checkGraph;
    int shortestD;

    public DataRecorder dr;

    public Hop_NoHash(Graph graph){
        this.graph = graph;
        this.INF = Integer.MAX_VALUE;
        this.mode = 0;
        this.print = true;
        this.checkGraph = false;
        this.dr = new DataRecorder();
    }

    public void start(){
//        dr.set(dr.numV,graph.vertices.size());
        long start = System.currentTimeMillis();
        while(bfs()){
//            dr.add(dr.iterationN,1);
            for(Vertex v:graph.vertices){
                if(v.label == Label.A && v.isFree()){
                    dfs(v);
                }
                //assert v.matching == null;
            }
        }
        dr.set(dr.runningTime,System.currentTimeMillis() - start);
//        dr.set(dr.matching_count,graph.matchCount());
    }

    private boolean bfs(){
        //generate an empty queue
        //LinkedList<Vertex> queue = new LinkedList<>();
        DataStructure.CycleArray<Vertex> queue = new DataStructure.CycleArray<>(graph.vertices.size());
        //DataStructure.CycleArray queue = new DataStructure.CycleArray(graph.vertices.size()/2);
        shortestD = INF;

        //set all free As' distance to 0, and push them to the queue
        //set all un-free As' distance to INF
        //Bs' distances are not stored since B->A is always unique (matching)
        for(Vertex v:graph.vertices){
            if(v.label == Label.B){
                continue;
            }
            if(v.isFree()){
                queue.addLast(v);
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
            //v.reset();
        }


        while (!queue.isEmpty()){
            /*
             * v (- A
             * u (- B
             */
            Vertex v = queue.pop();
            if(v.distance < shortestD){
//                dr.add(dr.hk_bfsVisitedE,1);
                for(Vertex u:v.edges){
                    /*
                     * Because we only apply HK on the current Graph
                     * So we check if u is in the Graph
                     * If it is, it's easy to see its matching must be in the current graph.
                     */
                    //bve2+=1;
                    if(!checkGraph || u.piece == v.piece){
                        /*
                         * <dist(u.matching) == INF> means
                         * <u.matching> is free or <u.matching> has not been explored yet (in the current BFS)
                         * In either case, we update the dist of <u.matching>
                         */
                        //dist.get(u.matching) == INF

                        if(u.matching == null){
                            shortestD = v.distance + 1;
                        }
                        else if(u.matching.distance == INF){
                            u.matching.distance = v.distance + 1;
                            queue.addLast(u.matching);
                        }
                    }
                }
            }
        }
//        if(print){
//            System.out.println("Hop bfs returns: " + shortestD);
//        }
        return shortestD != INF;
    }

    private boolean dfs_(Vertex startV){
        //v.visitedE = 0
        ArrayList<Vertex> stack = new ArrayList<>();
        stack.add(startV);
        while(!stack.isEmpty()){
            Vertex v = stack.get(stack.size()-1);
            Vertex u = v.next();
            if(u == null){
                stack.remove(stack.size()-1);
                v.distance = INF;
                continue;
            }
            if(!checkGraph || u.piece == v.piece){
                int nextDist;
                if(u.matching == null){
                    nextDist = shortestD;
                }
                else{
                    nextDist = u.matching.distance;
                }
                if(nextDist == v.distance + 1){
                    if(u.matching != null){
                        stack.add(u.matching);
                    }
                    else{
                        for(int i = stack.size() - 1; i >= 0; i-=1){
                            Vertex lastU = stack.get(i).matching;
                            u.match(stack.get(i));
                            u = lastU;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean dfs(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */
        if(v == null){
            return true;
        }
//        dr.add(dr.hk_dfsVisitedE,1);
        for(Vertex u:v.edges){
            //dve2+=1;
            if(!checkGraph || u.piece == v.piece){
                int nextDist;
                if(u.matching == null){
                    nextDist = shortestD;
                }
                else{
                    nextDist = u.matching.distance;
                }
                if(nextDist == v.distance + 1){
                    if(dfs(u.matching)){
                        u.match(v);
                        return true;
                    }
                }
            }
        }
        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        v.distance = INF;
        return false;
    }
}
