
package com.test.dijkstra;

import java.io.*;
import java.util.*;


//利用Dijkstra算法，计算地铁站经过路径，以南京地铁为例

public class Subway {

    private List<Station> outList = new ArrayList<Station>();//记录已经分析过的站点
    public static String outputPath;

    //计算从s1站到s2站的最短经过路径
    public void calculate(Station s1,Station s2) throws Exception{
        if(outList.size() == DataBuilder.totalStaion){


            LinkedHashSet<Station> how2Go = s1.getAllPassedStations(s2);
            String[] goStation = new String[how2Go.size()];
            int i =0;
            for(Station station : how2Go){
                goStation[i]=station.getName();
                i++;
            }
            Subway.output(goStation,outputPath);


            return;
        }
        if(!outList.contains(s1)){
            outList.add(s1);
        }
        //如果起点站的OrderSetMap为空，则第一次用起点站的前后站点初始化之
        if(s1.getOrderSetMap().isEmpty()){
            List<Station> Linkedstations = getAllLinkedStations(s1);
            for(Station s : Linkedstations){
                s1.getAllPassedStations(s).add(s);
            }
        }
        Station parent = getShortestPath(s1);//获取距离起点站s1最近的一个站（有多个的话，随意取一个）
        if(parent == s2){
            LinkedHashSet<Station> how2Go = s1.getAllPassedStations(s2);
            String[] goStation = new String[how2Go.size()];
            int i =0;
            for(Station station : how2Go){
                goStation[i]=station.getName();
                i++;
            }
            Subway.output(goStation,outputPath);
            return;
        }
        for(Station child : getAllLinkedStations(parent)){
            if(outList.contains(child)){
                continue;
            }
            int shortestPath = (s1.getAllPassedStations(parent).size()-1) + 1;//前面这个1表示计算路径需要去除自身站点，后面这个1表示增加了1站距离
            if(s1.getAllPassedStations(child).contains(child)){
                //如果s1已经计算过到此child的经过距离，那么比较出最小的距离
                if((s1.getAllPassedStations(child).size()-1) > shortestPath){
                    //重置S1到周围各站的最小路径
                    s1.getAllPassedStations(child).clear();
                    s1.getAllPassedStations(child).addAll(s1.getAllPassedStations(parent));
                    s1.getAllPassedStations(child).add(child);
                }
            } else {
                //如果s1还没有计算过到此child的经过距离
                s1.getAllPassedStations(child).addAll(s1.getAllPassedStations(parent));
                s1.getAllPassedStations(child).add(child);
            }
        }
        outList.add(parent);
        calculate(s1,s2);//重复计算，往外面站点扩展
    }

    //取参数station到各个站的最短距离，相隔1站，距离为1，依次类推
    private Station getShortestPath(Station station){
        int minPatn = Integer.MAX_VALUE;
        Station rets = null;
        for(Station s :station.getOrderSetMap().keySet()){
            if(outList.contains(s)){
                continue;
            }
            LinkedHashSet<Station> set  = station.getAllPassedStations(s);//参数station到s所经过的所有站点的集合
            if(set.size() < minPatn){
                minPatn = set.size();
                rets = s;
            }
        }
        return rets;
    }

    //获取参数station直接相连的所有站，包括交叉线上面的站
    private List<Station> getAllLinkedStations(Station station){
        List<Station> linkedStaions = new ArrayList<Station>();
        for(List<Station> line : DataBuilder.lineList){
            if(line.contains(station)){//如果某一条线包含了此站，注意由于重写了hashcode方法，只有name相同，即认为是同一个对象
                Station s = line.get(line.indexOf(station));
                if(s.prev != null){
                    linkedStaions.add(s.prev);
                }
                if(s.next != null){
                    linkedStaions.add(s.next);
                }
            }
        }
        return linkedStaions;
    }

    //根据内容的输出路径输出结果
    public static void output(String[] content,String ouputPath) throws Exception {
        //写入中文字符时解决中文乱码问题
        FileOutputStream fos=new FileOutputStream(new File(ouputPath));
        OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw=new BufferedWriter(osw);

        for(String arr:content){
            bw.write(arr+"\t\n");
        }

        //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
        bw.close();
        osw.close();
        fos.close();
    }


    public static void main(String[] args) throws Exception{
        Map<String,String> argMap = new HashMap<>();
        for (int i=0;i<args.length/2;i++){
            argMap.put(args[i*2],args[i*2+1]);
        }
        DataBuilder.init(argMap.get("-map"));
        Subway.outputPath = argMap.get("-o");
        String lineNum = argMap.get("-a");
        String howGo = argMap.get("-b");
        if (lineNum!=null){
            List<Station> line =  DataBuilder.lineList.get(Integer.parseInt(lineNum.substring(0,1))-1);
            if (line!=null){
                String[] stations = new String[line.size()];
                for (int i=0;i<line.size();i++){
                    stations[i]=line.get(i).getName();
                }
                Subway.output(stations,outputPath);
            }
        }
        if (howGo!=null){
            Subway sw = new Subway();
            sw.calculate(new Station(howGo.split("，")[0]), new Station(howGo.split("，")[1]));
        }



    }
}