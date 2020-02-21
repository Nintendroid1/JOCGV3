package com.company.algorithm;

import com.company.element.DataStructure;
import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Jocg_Pointer extends Algo{

    private int INF;
    private int currentBFS;
    public int dve;
    public int bve;

    public int pre_dve;
    public int pre_bve;
    public int pre_ite;
    public long preprocess;
    private long start;
    private long end;
    int shortestD;
    Graph graph;

    ArrayList<Vertex> exloredV;
    LinkedList<Vertex> path;

    public int iterate;

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
    }

    public void start(){
        dve=0;
        bve=0;
        pre_bve=0;
        pre_dve=0;
        start = System.currentTimeMillis();
        for(Graph g:graph.pieces) {
            Hop_NoHash hop = new Hop_NoHash(g);
            hop.print = false;
            hop.checkGraph = true;
            hop.start();
            this.pre_dve+=hop.dve;
            this.pre_bve+=hop.bve;
            this.pre_ite+=hop.iterate;
        }
        if(!graph.pieces.isEmpty()){
            this.pre_ite/=graph.pieces.size();
        }
        end = System.currentTimeMillis();
        preprocess = end - start;
        iterate = 0;
        //exloredVtable = new int[graph.vertices.size()/2];
        exloredV = new ArrayList<>();
        while(newbfs()){
            iterate+=1;
            /*
             * visitedE: store all visited edges in the current phase
             * any edges in vistedE won't be explored in the current phase
             */

            //visitedE = new AdjMatrix(graph.numV);
            //visitedE = new HashMap<>();
            for(Vertex v:graph.freeV(Label.A)){
                /*
                 * tempE: store visited edges in dfs(v) -> an adjacent list
                 * path: store the augmenting path returned by dfs(v)
                 */

                //tempE = new AdjMatrix(graph.numV);
                //tempE = new HashMap<>();
                path = new LinkedList<>();
                //tempE, path will be updated in newdfs
                if(newdfs(v)){
                    /*
                     * iterate the path
                     * if an path is in an affected piece, add this piece to affectedP
                     */

                    for(Graph piece:graph.pieces){
                        piece.affectedP = false;
                    }

                    Vertex parent = null;
                    for(Vertex u:path){
                        if(parent!=null && parent.piece == u.piece){
                            u.piece.affectedP = true;
                        }
                        parent = u;
                    }
                }
                if(!exloredV.isEmpty()){
                    for(Vertex ev:exloredV){
                        ev.deleteE_clean();
                        ev.explored = false;
                    }
                    exloredV = new ArrayList<>();
                }
            }
        }
    }

    private boolean newbfs(){
        shortestD = INF;
        for(Vertex v:graph.V(Label.A)){
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
            v.reset();
            if(dv.d > v.distance || v.distance > shortestD){
                continue;
            }
            this.bve+=1;
            for(Vertex u:v.edges){
                if(u == v.matching){
                    continue;
                }
                u.reset();
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
        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }

    private boolean newdfs(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }

        if(v.explored){
            return false;
        }
        else{
            v.explored = true;
            exloredV.add(v);
        }

        dve+=1;

        while(true){
            Vertex u = v.next();
            if(u == null){
                break;
            }
            if(u.matching != null && u.matching.explored){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }

                if(newdfs(u.next())){
                    u.match(v);
                    path.addFirst(u);
                    path.addFirst(v);
                    return true;
                }
            }
        }

        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        //dist.put(v,INF);
        return false;
    }

    private boolean newbfs_temp(){
        //int[][] marker = new int[graph.vertices.size()][graph.vertices.size()];
        DataStructure.CycleArray<DataStructure.DisV> cycleArray = new DataStructure.CycleArray<>(graph.vertices.size());
        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                cycleArray.addLast(new DataStructure.DisV(v,0));
                //marker[v.id] = 1;
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!cycleArray.isEmpty()){

            Vertex v;
            DataStructure.DisV dv;
            dv = cycleArray.pop();
            v = dv.v;
            if(dv.d > v.distance){
                continue;
            }
            //marker[v.id] = 0;
            if(v.distance > shortestD){
                continue;
            }
            v.reset();

            this.bve+=1;
            if(v.label == Label.A){
                //children = v.edges;

                for(Vertex u:v.edges){
                    if(u == v.matching){
                        continue;
                    }
                    //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                    if(u.distance > v.distance + graph.getWeight(u,v)){
                        u.distance = v.distance + graph.getWeight(u,v);
                        if(graph.getWeight(u,v) == 0){
                            if(u.matching == null){
                                shortestD = v.distance;
                            }
                            else if(graph.getWeight(u,u.matching) == 0
                                    && u.matching.distance > v.distance){
                                u.matching.distance = v.distance;
                                u.reset();
                                cycleArray.addFirst(new DataStructure.DisV(u.matching,u.matching.distance));
                            }
                            else {
                                cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                            }
                        }
                        else{
                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                        }

                    }

//                        if(graph.getWeight(u,v) == 0){
//                            cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
//                            //marker[u.id] = 1;
//                        }
//                        else{
//                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
//                            //marker[u.id] = 1;
//                        }
                    }
            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    continue;
                }

                Vertex u = v.matching;
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);

                    if(graph.getWeight(u,v) == 0){
                        cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                    }
                    else{
                        cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                    }
                }
            }
        }

        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }


    private boolean newbfs_backup(){
        //int[][] marker = new int[graph.vertices.size()][graph.vertices.size()];
        DataStructure.CycleArray<DataStructure.DisV> cycleArray = new DataStructure.CycleArray<>(graph.vertices.size());
        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                cycleArray.addLast(new DataStructure.DisV(v,0));
                //marker[v.id] = 1;
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!cycleArray.isEmpty()){

            Vertex v;
            DataStructure.DisV dv;
            dv = cycleArray.pop();
            v = dv.v;
            if(dv.d > v.distance){
                continue;
            }
            //marker[v.id] = 0;
            if(v.distance > shortestD){
                continue;
            }

            v.reset();

            this.bve+=1;
            if(v.label == Label.A){
                //children = v.edges;

                for(Vertex u:v.edges){
                    if(v.label == Label.A && u == v.matching){
                        continue;
                    }
                    //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                    if(u.distance > v.distance + graph.getWeight(u,v)){
                        u.distance = v.distance + graph.getWeight(u,v);

                        if(graph.getWeight(u,v) == 0){
                            cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                            //marker[u.id] = 1;
                        }
                        else{
                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                            //marker[u.id] = 1;
                        }
                    }
                }

            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    continue;
                }

                Vertex u = v.matching;
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);

                    if(graph.getWeight(u,v) == 0){
                        cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                    }
                    else{
                        cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                    }
                }
            }
        }

        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }



    private boolean newdfs_back(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }

        //add v to tempE
        //v (- A
        //parent (- B

        if(v.explored){
            return false;
        }
        else{
            v.explored = true;
            exloredV.add(v);
        }

        dve+=1;

        while(true){
            Vertex u = v.next();

            if(u == null){
                break;
            }
            if(u.matching != null && u.matching.explored){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }

                if(newdfs(u.next())){
                    u.match(v);
                    path.addFirst(u);
                    path.addFirst(v);
                    return true;
                }
            }
        }

        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        //dist.put(v,INF);
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
