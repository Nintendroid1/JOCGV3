package com.company.algorithm;

import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.SortedSet;

public class Jocg extends Algo{

    private int INF;
    private int currentBFS;
    Graph graph;
    HashMap<Vertex,Integer> dist;
    HashMap<Vertex,HashSet<Vertex>> visitedE;
    HashMap<Vertex,HashSet<Vertex>> tempE;
    LinkedList<Vertex> path;

    public int iterate;

    public Jocg(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;
        this.dist = new HashMap<>();
    }

    public void start(){

        for(Graph g:graph.pieces) {
            Hop hop = new Hop(g);
            hop.print = false;
            hop.start();
        }

        iterate = 0;
        while(newbfs()){
            iterate+=1;
            /*
             * visitedE: store all visited edges in the current phase
             * any edges in vistedE won't be explored in the current phase
             */
            visitedE = new HashMap<>();
            for(Vertex v:graph.freeV(Label.A)){
                /*
                 * tempE: store visited edges in dfs(v) -> an adjacent list
                 * path: store the augmenting path returned by dfs(v)
                 */
                tempE = new HashMap<>();
                path = new LinkedList<>();
                //tempE, path will be updated in newdfs
                if(newdfs(v,null)){
//                    System.out.println("current BFS returns: " + currentBFS);
//                    System.out.println("DFS finds a path with weight: " + pathWeight(path));
                    /*
                     * iterate the path
                     * if an path is in an affected piece, add this piece to affectedP
                     */
                    HashSet<Graph> affectedP = new HashSet<>();
                    Vertex parent = null;
                    for(Vertex u:path){
                        if(parent!=null && parent.piece == u.piece){
                            affectedP.add(u.piece);
                        }
                        parent = u;
                    }

                    //if an edge belongs to an affected piece, remove this edge from tempE
                    for(Vertex a:tempE.keySet()){
                        for(Vertex b:new HashSet<>(tempE.get(a))){
                            if(a.piece == b.piece && affectedP.contains(a.piece)){
                                tempE.get(a).remove(b);
                            }
                        }
                    }
                }

                //add all remaining edges in tempE to visitedE
                for(Vertex a:tempE.keySet()){
                    if(!visitedE.containsKey(a)){
                        visitedE.put(a,tempE.get(a));
                    }
                    else{
                        visitedE.get(a).addAll(tempE.get(a));
                    }
                }
            }
        }
    }

    private boolean newbfs(){
        LinkedList<Vertex> queue = new LinkedList<>();

        dist = new HashMap<>();
        dist.put(null,INF);
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                queue.addLast(v);dist.put(v,0);
            }
            else{
                dist.put(v,INF);
            }
        }
        while(!queue.isEmpty()){
            Vertex v = queue.pop();

            if(dist.get(v) <= dist.get(null)){

                if(v.label == Label.A){
                    for(Vertex u:v.edges){
                        if(dist.get(u) == INF){
                            if(graph.getWeight(u,v) == 0){
                                queue.addFirst(u);
                            }
                            else{
                                queue.addLast(u);
                            }
                            dist.put(u,dist.get(v) + graph.getWeight(u,v));
                        }
                    }
                }
                else{
                    if(v.matching != null){
                        if(graph.getWeight(v.matching,v) == 0){
                            queue.addFirst(v.matching);
                        }
                        else{
                            queue.addLast(v.matching);
                        }
                    }
                    dist.put(v.matching,dist.get(v) + graph.getWeight(v,v.matching));
                }
            }
        }
        System.out.println("Jocg bfs returns: " + dist.get(null));
        currentBFS = dist.get(null);
        return dist.get(null) != INF;
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
        if(!tempE.containsKey(v)){
            tempE.put(v,new HashSet<>());
        }
        if(parent != null){
            tempE.get(v).add(parent);
        }

        v.explored = true;

        for(Vertex u:v.edges){

            //if the edge (v,u) is already explored, then skip this
            if(containE(visitedE,v,u) || containE(tempE,v,u)){
                continue;
            }
            else{
                tempE.get(v).add(u);
            }

            //if path from v to u.matching is valid, then continue
            if(dist.get(u.matching) - dist.get(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){

                //if the edge (u.matching,u) is already explored, then skip this
                if(containE(visitedE,u.matching,u) || containE(tempE,u.matching,u)){
                    continue;
                }

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


}
