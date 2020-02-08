package com.company.algorithm;

import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Jocg_Hk extends Algo{

    private int INF;
    private int currentBFS;
    int shortestD;
    Graph graph;

    class AdjMatrix{
        int[][] matrix;
        LinkedList<Integer>[] indexes;
        int N;
        public AdjMatrix(int n){
            N = n;
            matrix = new int[N][];
            indexes = new LinkedList[N];
        }

        public void addEdge(int a, int b){
            b -= N;
            if(matrix[a] == null){
                matrix[a] = new int[N];
                indexes[a] = new LinkedList<>();
            }

            matrix[a][b] = 1;
            indexes[a].addLast(b);
        }

        public void deleteEdge(int a, int b){
            b -= N;
            if(matrix[a] != null){
                matrix[a][b] = 0;
                indexes[a].remove(b);
            }
        }

        public boolean containE(int a, int b){
            b -= N;
            return matrix[a]!=null && matrix[a][b] == 1;
        }
    }

    AdjMatrix visitedE;
    AdjMatrix tempE;

//    HashMap<Vertex,HashSet<Vertex>> visitedE;
//    HashMap<Vertex,HashSet<Vertex>> tempE;
    LinkedList<Vertex> path;

    public int iterate;

    public Jocg_Hk(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;
    }

    public void start(){

        for(Graph g:graph.pieces) {
            Hop_NoHash hop = new Hop_NoHash(g);
            hop.print = false;
            hop.start();
        }

        Hop_NoHash hop = new Hop_NoHash(graph);
        hop.start();

    }

    private boolean newbfs(){
        LinkedList<Vertex> queue = new LinkedList<>();

        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                queue.addLast(v);
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!queue.isEmpty()){
            Vertex v = queue.pop();
            HashSet<Vertex> children;

            if(v.distance > shortestD){
                continue;
            }
            if(v.label == Label.A){
                children = new HashSet<>(v.edges);
                children.remove(v.matching);
            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    //dist.put(null,dist.get(v));
                    continue;
                }
                children = new HashSet<>();
                children.add(v.matching);
            }

            for(Vertex u:children){
                //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);
                    //dist.put(u,dist.get(v) + graph.getWeight(u,v));
                    if(graph.getWeight(u,v) == 0){
                        queue.addFirst(u);
                    }
                    else{
                        queue.addLast(u);
                    }
                }
            }
        }

        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }

    private boolean newdfs(Vertex v,Vertex parent){
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

        if(parent != null){
            if(!tempE.containE(v.id,parent.id)){
                tempE.addEdge(v.id,parent.id);
            }
        }


//        if(!tempE.containsKey(v)){
//            tempE.put(v,new HashSet<>());
//        }
//        if(parent != null){
//            tempE.get(v).add(parent);
//        }

        v.explored = true;

        for(Vertex u:v.edges){

            //if the edge (v,u) is already explored, then skip this

            if(visitedE.containE(v.id,u.id) || tempE.containE(v.id,u.id)){
                continue;
            }
            else{

//                if(u.id < graph.numV){
//                    int a = 0;
//                }


                tempE.addEdge(v.id,u.id);
            }

//            if(containE(visitedE,v,u) || containE(tempE,v,u)){
//                continue;
//            }
//            else{
//                tempE.get(v).add(u);
//            }

            //if path from v to u.matching is valid, then continue

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){

                //if the edge (u.matching,u) is already explored, then skip this

                if(u.matching != null){
                    if(visitedE.containE(u.matching.id,u.id) || tempE.containE(u.matching.id, u.id)){
                        continue;
                    }
                }

//                if(containE(visitedE,u.matching,u) || containE(tempE,u.matching,u)){
//                    continue;
//                }

                if(newdfs(u.matching,u)){
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

    private int pathWeight(LinkedList<Vertex> path){
        Vertex parent = null;
        int result = 0;
        for(Vertex v:path){
            if(parent != null && parent.piece != v.piece){
                result += 1;
            }
            parent = v;
        }
        return result;
    }

    private boolean containE(HashMap<Vertex,HashSet<Vertex>> visitedE, Vertex a, Vertex b){
        return (visitedE.containsKey(a) && visitedE.get(a).contains(b));
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
