package com.test.dijkstra;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataBuilder {

    public static List<List<Station>> lineList = new ArrayList<>();//所有线集合
    public static int totalStaion = 0;//总的站点数量
    public static List<String> strList = new ArrayList<>();
    public static String subwayFilePath;
    public static void init (String filePath){
        subwayFilePath = filePath;
        try {
            FileInputStream fis=new FileInputStream(subwayFilePath);
            InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line="";
            while ((line=br.readLine())!=null) {
                strList.add(line);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            System.out.print("找不到文件");
            System.exit(1);
        }

        for (String lineStr:strList) {
            String[] line1Arr = lineStr.split("、");
            List<Station> line = new ArrayList<Station>();
            for(String s : line1Arr){
                line.add(new Station(s));
            }
            for(int i =0;i<line.size();i++){
                if(i<line.size()-1){
                    line.get(i).next = line.get(i+1);
                    line.get(i+1).prev = line.get(i);
                }
            }
            lineList.add(line);
        }

        for (List<Station> line:lineList) {
            totalStaion+=line.size();
        }
    }

}
