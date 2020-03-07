package com.company.element;

import java.util.ArrayList;
import java.util.List;

public class EdgeMaker {

    public ArrayList<Point> points;
    public EdgeMaker(ArrayList<Point> ps){
        points = ps;
    }

    public void reEdges(double bottleneck){
        double approx_bottleneck = bottleneck;
        Box box = new Box(partition(points,bottleneck),bottleneck);
        while(true){
            List<Point> source = box.getNext();
            if(source == null){
                break;
            }

            List<List<Point>> targets = box.getNeighbor();
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
            int next_level = getLevel(list.get(i),bottleneck,c);
            if(next_level > level){
                result.add(list.subList(head,i));
                head = i;
                level = next_level;
            }
        }
        return result;
    }


    private int getLevel(Point p,double b, Character c){
        if(c == 'x'){
            return (int)(p.x/b);
        }
        else{
            return (int)(p.y);
        }
    }

    class Box {
        List<List<List<Point>>> partition;
        int currX, currY, rightY;
        double bottleneck;

        Box(List<List<List<Point>>> pa, double b){
            partition = pa;
            currX = 0; currY = 0; rightY = 0;
            bottleneck = b;
        }

        public void init(){
            currX = 0; currY = 0;
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
            if(currY + 1 < partition.get(currX).size()
                    && closeTo(partition.get(currX).get(currY),partition.get(currX).get(currY+1))){
                result.add(partition.get(currX).get(currY+1));
            }

            /*
             * get neighbor from right hand side
             */
            int offset = -2;
            if(rightY + offset < 0){
                offset = 0;
            }
            Integer temp = null;
            while(true){
                if(currX+1 >= partition.size()){
                    break;
                }
                if(rightY + offset >= partition.get(currX + 1).size()){
                    break;
                }

                List<Point> currBox = partition.get(currX).get(currX);
                List<Point> rightBox = partition.get(currX + 1).get(rightY + offset);
                if(getLevel(rightBox.get(0),bottleneck,'y') > getLevel(currBox.get(0),bottleneck,'y') + 1){
                    break;
                }
                else if(getLevel(rightBox.get(0),bottleneck,'y') <= getLevel(currBox.get(0),bottleneck,'y') + 1
                && getLevel(rightBox.get(0),bottleneck,'y') >= getLevel(currBox.get(0),bottleneck,'y') - 1){
                    result.add(rightBox);
                }
                offset+=1;
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
