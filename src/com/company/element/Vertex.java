package com.company.element;

import com.company.generator.GraphGen;

import java.util.ArrayList;

public class Vertex {
    public int id;
    public Label label;
    public ArrayList<Vertex> edges;
    /*
     * visitedE is the index for the last vertex(edge) in visitedE + 1
     * tempE is the index for the last vertex(edge) in tempE + 1
     * onezero is the the index for the last edge(vertex) with weight 1 in edges + 1
     */
    private int visitedEP;
    private int tempEP;
    public int onezeroP;

    public Vertex matching;
    public Graph piece;

    public boolean explored;
    public int distance;

    public Point point;

    public Vertex(int id, Label label){
        this.id = id;
        this.label = label;
        this.edges = new ArrayList<>();
        this.matching = null;
        this.piece = null;
        this.explored = false;
    }

    public void match(Vertex x){
        x.matching = this;
        this.matching = x;
    }

    public void deleteE_clean(int cb, int cd){
        if(label == Label.A){
            if(tempEP <= onezeroP){
                visitedEP = tempEP;
            }
            else{
                //in this region, edges can only have weight of 0
                if(this.piece.affectedP.equal(cb,cd)){
                    //the piece is affected piece
                    //if this v can be connected to aug path by edges of weight 0,
                    //then all edges can be saved
                    //otherwise, delete all edges
                    visitedEP = Math.max(visitedEP,onezeroP);
                    tempEP = visitedEP;
                }
                else{
                    visitedEP = tempEP;
                }
            }
            if(this.matching != null){
                this.matching.deleteE_clean(cb,cd);
            }
        }
        else{
            if(tempEP > 0){
                if(this.matching != null){
                    if(this.piece == this.matching.piece && this.piece.affectedP.equal(cb,cd)){
                        tempEP = 0;
                    }
                }
                else{
                    tempEP = 0;
                }

            }
        }
    }

    public void allDelte(){
        visitedEP = tempEP;
    }

    public void reserve(int off){
        if(label == Label.A){
            explored = false;
            for(int i = visitedEP; i < tempEP - off;i++){
                if(edges.get(i) != this.matching){
                    edges.get(i).reserve(0);
                }
            }
        }
        else{
            if(this.matching != null
                    && this.piece == this.matching.piece
                    && this.matching.explored){
                this.matching.reserve(0);
            }
        }
    }


//    public int[] deleteE(int code){
//        int deleted = 0;
//        int visted = tempEP - visitedEP;
//        assert tempEP >= visitedEP;
//        if(label == Label.A){
//            if(tempEP <= onezeroP){
//                deleted=(tempEP-visitedEP);
//                visitedEP = tempEP;
//            }
//            else{
//                //in this region, edges can only have weight of 0
//                if(this.piece.affectedP == code){
//                    deleted=Math.max(0,onezeroP-visitedEP);
//
//                    visitedEP = Math.max(visitedEP,onezeroP);
//                    tempEP = visitedEP;
//                }
//                else{
//                    deleted=(tempEP-visitedEP);
//                    visitedEP = tempEP;
//                }
//            }
//            if(this.matching != null){
//                this.matching.deleteE(code);
//            }
//        }
//        else{
//            if(tempEP > 0){
//                if(this.matching != null){
//                    if(this.piece == this.matching.piece && this.piece.affectedP == code){
//                        tempEP = 0;
//                    }
//                }
//                else{
//                    tempEP = 0;
//                }
//
//            }
//        }
//        return new int[]{visted,deleted};
//    }

    public boolean hasNext(){
        if(label == Label.A){
            return tempEP < edges.size();
        }
        else {
            return tempEP == 0;
        }
    }

    public Vertex next(){
        if(label == Label.A){
            if(hasNext()){
                //what if result == matching?
                Vertex result = edges.get(tempEP);
                tempEP += 1;
                if(result == this.matching){
                    return next();
                }
                return result;
            }
            return null;
        }
        //if label == B
        if(hasNext()){
            tempEP+=1;
            return matching;
        }
        return null;
    }

    public void reset(){
        visitedEP = 0;
        tempEP = 0;
    }

    public boolean isFree(){
        return matching == null;
    }

}
