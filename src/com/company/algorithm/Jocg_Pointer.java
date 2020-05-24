package com.company.algorithm;

import com.company.element.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Jocg_Pointer extends Algo{

    private int INF;
    private int currentBFS;

    public long preprocess;
    private long start;
    private long end;
    int shortestD;
    public Graph graph;

    public DataRecorder dr;

    ArrayList<Vertex> exloredV;

    ArrayList<Vertex> path;



    DataStructure.CycleArray<DataStructure.DisV> zeros;
    DataStructure.CycleArray<DataStructure.DisV> ones;
    DataStructure.CycleArray<DataStructure.DisV> twos;
    DataStructure.CycleArray<DataStructure.DisV> temp;

    public Jocg_Pointer(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;

        zeros = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);
        ones = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);
        twos = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);

        this.dr = new DataRecorder();
    }

    public void start(){
        //record data
        dr.set(dr.numV,graph.vertices.size());
        dr.set(dr.number_of_edges,graph.edgeNum);
        dr.set(dr.pre_iterationN,0);
        start = System.currentTimeMillis();


        //pre-processing
        for(Graph g:graph.pieces) {
            Hop_NoHash hop = new Hop_NoHash(g);
            hop.print = false;
            hop.checkGraph = true;
            hop.start();

            //record data
            dr.add(dr.pre_bfsVisitedE,hop.dr.get(dr.hk_bfsVisitedE));
            dr.add(dr.pre_dfsVisitedE,hop.dr.get(dr.hk_dfsVisitedE));
            if(hop.dr.get((dr.iterationN)) > dr.get(dr.pre_iterationN)){
                dr.set(dr.pre_iterationN,hop.dr.get(dr.iterationN));
            }
        }

        end = System.currentTimeMillis();
        preprocess = end - start;
        dr.set(dr.pre_runningTime,preprocess);

        start = System.currentTimeMillis();
        exloredV = new ArrayList<>();
        while(newbfs()){
            dr.add(dr.iterationN,1);
            /*
             * visitedE: store all visited edges in the current phase
             * any edges in vistedE won't be explored in the current phase
             */
            int count = 0;
            for(Vertex v:graph.vertices){
                /*
                 * tempE: store visited edges in dfs(v) -> an adjacent list
                 * path: store the augmenting path returned by dfs(v)
                 */
                if(v.label == Label.B || !v.isFree()){
                    continue;
                }
                path = new ArrayList<>();
                //tempE, path will be updated in newdfs

                if(newdfs(v)){
                    count += 1;
                    /*
                     * iterate the path
                     * if an path is in an affected piece, add this piece to affectedP
                     */
                    Vertex parent = null;
                    int apN = 0;
                    for(Vertex uu:path){
                        for(Vertex u:new Vertex[]{uu, uu.matching}){
                            if(parent!=null && parent.piece == u.piece){
                                //u.piece.affectedP = true;
                                if(u.piece.affectedP.bfs != currentBFS
                                        && u.piece.affectedP.dfs != count){
                                    apN += 1;
                                }
                                u.piece.affectedP.bfs = currentBFS;
                                u.piece.affectedP.dfs = count;
                            }
                            parent = u;
                        }
                    }
                    dr.add(dr.affected_pieces,apN);
                }

                if(!exloredV.isEmpty()){
                    //del+=exloredV.size();
                    for(Vertex ev:exloredV){
                        ev.deleteE_clean(currentBFS,count);
                        //dr.add(dr.number_of_revisited_edges,revisitedN);
                        ev.explored = false;
                    }
                    exloredV = new ArrayList<>();
                }
            }
        }
        dr.set(dr.runningTime,System.currentTimeMillis() - start);
        dr.set(dr.matching_count,graph.matchCount());
        int w = 0;
        for(Vertex v: graph.vertices){
            if(v.label == Label.A && !v.isFree() && v.piece != v.matching.piece){
                w+=1;
            }
        }
        dr.set(dr.w,w);
    }

    private boolean newbfs(){
        shortestD = INF;
        for(Vertex v:graph.vertices){
            v.reset();
            if(v.label == Label.B){
                continue;
            }
            if(v.isFree()){
                zeros.addLast(new DataStructure.DisV(v,0));
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }

        }

        while(!(zeros.isEmpty()&&ones.isEmpty()&&twos.isEmpty())){
            if(zeros.isEmpty()){
                while (zeros.isEmpty()){
                    temp = zeros;
                    zeros = ones;
                    ones = twos;
                    twos = temp;
                }
            }

            Vertex v;
            DataStructure.DisV dv;
            dv = zeros.pop();

            v = dv.v;



            if(dv.d > shortestD){
                continue;
            }
            if(v == null){
                shortestD = dv.d;
                continue;
            }
            if(v.distance > shortestD){
                continue;
            }
            if(dv.d > v.distance){
                continue;
            }
            //v.reset();
            dr.add(dr.jocg_bfsVisitedE,1);
            for(Vertex u:v.edges){
                //this.bve2+=1;
                if(u == v.matching){
                    continue;
                }
                //u.reset();
                int distance;
                if(u.matching != null){
                    distance =  graph.getWeight(v,u) + graph.getWeight(u,u.matching);
                }
                else{
                    distance = graph.getWeight(v,u);
                }

                if(v.distance + distance > shortestD){
                    continue;
                }
                if(u.matching == null || u.matching.distance > v.distance + distance){
                    if(u.matching != null){
                        u.matching.distance = v.distance + distance;
                    }
                    if(distance == 0){
                        zeros.addLast(new DataStructure.DisV(u.matching,v.distance));
                    }
                    else if(distance == 1){
                        ones.addLast(new DataStructure.DisV(u.matching,v.distance+distance));
                    }
                    else{
                        twos.addLast(new DataStructure.DisV(u.matching,v.distance+distance));
                    }
                }
            }


        }
        //System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }

    private boolean newdfs(Vertex startV){
        ArrayList<Vertex> tempPath = new ArrayList<>();
        tempPath.add(startV);
        startV.explored = true;
        exloredV.add(startV);
        while(!tempPath.isEmpty()){
            Vertex v = tempPath.get(tempPath.size()-1);
            if(v == null){
                //augment path
                Vertex head = null;
                for(Vertex tail:tempPath){
                    if(head != null && head.label == Label.A){
                        head.match(tail);
                        path.add(head);
                        path.add(tail);
                    }
                    head = tail;
                }
                return true;
            }
            boolean nextRevisit = v.nextRevisit();
            Vertex u = v.next();
            if(u == null){
                tempPath.remove(tempPath.size()-1);
                if(!tempPath.isEmpty()){
                    tempPath.remove(tempPath.size()-1);
                }
                continue;
            }
            if(u.matching != null && u.matching.explored){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){

                if(nextRevisit){
                    dr.add(dr.number_of_revisited_edges,1);
                }

                dr.add(dr.jocg_dfsVisitedE,1);

                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }
                Vertex next = u.next();
                if(next != null){
                    next.explored = true;
                    exloredV.add(next);
                }
                tempPath.add(u);
                tempPath.add(next);
            }
        }
        return false;
    }

    private int dist(Vertex v){
        if(v == null){
            return shortestD;
        }
        else{
            return v.distance;
        }
    }
}
