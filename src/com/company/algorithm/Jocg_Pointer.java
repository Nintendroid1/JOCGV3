package com.company.algorithm;

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
    public int ve;
    int shortestD;
    Graph graph;

//    HashMap<Vertex,HashSet<Vertex>> visitedE;
//    HashMap<Vertex,HashSet<Vertex>> tempE;
    ArrayList<Vertex> exloredV;
    int[] exloredVtable;
    LinkedList<Vertex> path;

    public int iterate;

    public Jocg_Pointer(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;
    }

    public void start(){
        ve=0;
        for(Graph g:graph.pieces) {
            Hop_NoHash hop = new Hop_NoHash(g);
            hop.print = false;
            hop.checkGraph = true;
            hop.start();
            ve+=hop.ve;
        }

        iterate = 0;
        exloredVtable = new int[graph.vertices.size()/2];
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
                        exloredVtable[ev.id] = 0;
                    }
                    exloredV = new ArrayList<>();
                }


            }
        }
    }

    private boolean newbfs(){
        int[] marker = new int[graph.vertices.size()];
        LinkedList<Vertex> queue = new LinkedList<>();

        

        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                queue.addLast(v);
                marker[v.id] = 1;
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!queue.isEmpty()){
            Vertex v = queue.pop();
            marker[v.id] = 0;
            v.reset();

            ArrayList<Vertex> children;
            if(v.distance > shortestD){
                continue;
            }

            if(v.label == Label.A){
                children = v.edges;
            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    //dist.put(null,dist.get(v));
                    continue;
                }
                children = new ArrayList<>();
                children.add(v.matching);
            }

            for(Vertex u:children){
                if(v.label == Label.A && u == v.matching){
                    continue;
                }
                //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);
                    //dist.put(u,dist.get(v) + graph.getWeight(u,v));
                    if(graph.getWeight(u,v) == 0){
                        if(marker[u.id] == 0){
                            queue.addFirst(u);
                            marker[u.id] = 1;
                        }

                    }
                    else{
                        if(marker[u.id] == 0){
                            queue.addLast(u);
                            marker[u.id] = 1;
                        }

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

        //add v to tempE
        //v (- A
        //parent (- B

//        if(parent != null){
//            if(!tempE.containE(v.id,parent.id)){
//                tempE.addEdge(v.id,parent.id);
//            }
//        }


//        if(!tempE.containsKey(v)){
//            tempE.put(v,new HashSet<>());
//        }
//        if(parent != null){
//            tempE.get(v).add(parent);
//        }

//        v.explored = true;
//        if(parent != null){
//            parent.explored = true;
//        }
        if(exloredVtable[v.id] == 0){
            exloredVtable[v.id] = 1;
            exloredV.add(v);
        }
        else{
            return false;
        }

        while(true){
            Vertex u = v.next();
            if(u == null){
                break;
            }
            if(u.matching != null && exloredVtable[u.matching.id] == 1){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                ve+=1;
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

//        for(Vertex u:v.edges){
//
//            //if the edge (v,u) is already explored, then skip this
//
//            if(visitedE.containE(v.id,u.id) || tempE.containE(v.id,u.id)){
//                continue;
//            }
//            else{
//                tempE.addEdge(v.id,u.id);
//            }
//
//            //if path from v to u.matching is valid, then continue
//
//            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
//
//                //if the edge (u.matching,u) is already explored, then skip this
//
//                if(u.matching != null){
//                    if(visitedE.containE(u.matching.id,u.id) || tempE.containE(u.matching.id, u.id)){
//                        continue;
//                    }
//                }
//
////                if(containE(visitedE,u.matching,u) || containE(tempE,u.matching,u)){
////                    continue;
////                }
//
//                if(newdfs(u.matching,u)){
//                    u.match(v);
//                    path.addFirst(u);
//                    path.addFirst(v);
//                    return true;
//                }
//            }
//
//        }
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
