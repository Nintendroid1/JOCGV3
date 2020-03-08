package com.company.element;

import java.util.ArrayList;
import java.util.List;

public class EdgeMaker {

    public ArrayList<Point> points;
    public Graph graph;
    public int edgeCount;
    public EdgeMaker(ArrayList<Point> ps){
        points = ps;
        graph = null;
        edgeCount = 0;
    }

    public void reEdges(double bottleneck){
//        double bo = bottleneck;
        Box box = new Box(partition(points,bottleneck),bottleneck);
        while(true){
            List<Point> source = box.getNext();
            if(source == null){
                break;
            }
            if(source.contains(graph.vertices.get(3402).point)){
                int a = 0;
            }
            if(source.contains(graph.vertices.get(8916).point)){
                int a = 0;
            }

            List<List<Point>> targets = box.getNeighbor();
            targets.add(source);
            for(List<Point> target: targets){
                makeEdge(source,target,bottleneck);
            }

        }
    }

    private void makeEdge(List<Point> source,List<Point> target,double bottleneck){
        for(Point s:source){
            for(Point t:target){
                if(s.v.label == t.v.label){
                    continue;
                }
                double x = s.x - t.x;
                double y = s.y - t.y;
                double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                if(d <= bottleneck){
                    s.v.edges.add(t.v);
                    t.v.edges.add(s.v);
                }
            }
        }
    }

    private List<List<List<Point>>> partition(List<Point> list, double bottleneck){
        List<List<Point>> xp = partition_(points,bottleneck,'x');
        List<List<List<Point>>> yp = new ArrayList<>();
        assert xp != null;
        for(List<Point> box:xp){
            yp.add(partition_(box,bottleneck,'y'));
        }
        return yp;
    }

    private List<List<Point>> partition_(List<Point> list, double bottleneck, char c){
        if(list.isEmpty()){
            return null;
        }
        if(c == 'x'){
            list.sort(new Point.Xcomparator());
        }
        else if(c == 'y'){
            list.sort(new Point.Ycomparator());
        }
        else{
            return null;
        }

        List<List<Point>> result = new ArrayList<>();
        int level = getLevel(list.get(0),bottleneck,c); int head = 0;
        for(int i = 1; i < list.size(); i++){
            Point point = list.get(i);
            int next_level = getLevel(point,bottleneck,c);
            if(next_level > level){
                result.add(list.subList(head,i));
                head = i;
                level = next_level;
            }
        }
        if(head < list.size()){
            result.add(list.subList(head,list.size()));
        }
        return result;
    }


    private int getLevel(Point p,double b, Character c){
        double result;
        if(c == 'x'){
            result = p.x/b;
            return  (int)result;
        }
        else if(c == 'y'){
            result = p.y/b;
            return  (int)result;
        }
        else{
            assert false;
        }
        return -1;
    }

    class Box {
        List<List<List<Point>>> partition;
        int currX, currY, rightY;
        double bottleneck;

        Box(List<List<List<Point>>> pa, double b){
            partition = pa;
            currX = 0; currY = -1; rightY = 0;
            bottleneck = b;
        }

        public void init(){
            currX = 0; currY = -1;
        }


        public List<Point> getNext(){
            if(currX < partition.size() && currY + 1 < partition.get(currX).size()){
                currY+=1;
                return partition.get(currX).get(currY);
            }
            else if(currX + 1 < partition.size()){
                currX+=1;currY=0; rightY = 0;
                return partition.get(currX).get(currY);
            }
            else{
                return null;
            }
        }

        public List<List<Point>> getNeighbor(){
            /*
             * get neighbor from bottom
             */
            List<List<Point>> result = new ArrayList<>();
            List<Point> currBox = partition.get(currX).get(currY);

//            if(currBox.contains(graph.vertices.get(3402).point)){
//                int a = 0;
//            }

            if(currY + 1 < partition.get(currX).size()){
                List<Point> botBox = partition.get(currX).get(currY+1);
                if(getLevel(botBox.get(0),bottleneck,'y') == getLevel(currBox.get(0),bottleneck,'y') + 1){
                    result.add(partition.get(currX).get(currY+1));
                }
            }

            /*
             * get neighbor from right hand side
             */
            if(currX + 1 < partition.size()){
                int tempRightY = rightY;
                while(rightY < partition.get(currX+1).size()){
                    List<Point> rightBox = partition.get(currX+1).get(rightY);
                    if(getLevel(rightBox.get(0),bottleneck,'y') > getLevel(currBox.get(0),bottleneck,'y') + 1){
                        break;
                    }
                    if(getLevel(rightBox.get(0),bottleneck,'y') >= getLevel(currBox.get(0),bottleneck,'y') - 1){
                        result.add(rightBox);
                    }
                    if(getLevel(rightBox.get(0),bottleneck,'y') < getLevel(currBox.get(0),bottleneck,'y')){
                        tempRightY+=1;
                    }
                    rightY+=1;

                }
                rightY = tempRightY;
            }

            return result;
        }

        public boolean closeTo(List<Point> b1, List<Point> b2){

            if(b1.isEmpty() || b2.isEmpty()){
                return false;
            }
            int x1 = getLevel(b1.get(0),bottleneck,'x');
            int y1 = getLevel(b1.get(0),bottleneck,'y');
            int x2 = getLevel(b2.get(0),bottleneck,'x');
            int y2 = getLevel(b2.get(0),bottleneck,'y');

            return (Math.abs(x1 - x2) + Math.abs(y1 - y2) <= 2);
        }
    }
}
