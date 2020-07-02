package com.example.moviespid;

import com.alibaba.fastjson.JSON;
import com.example.moviespid.TypeMovieBean.Subjects;
import com.example.moviespid.TypeMovieBean.TypeMovieBean;
import com.example.moviespid.util.SpiderUtil;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MovieTypeSpider {
    public static void main(String[] args) {
        String tag="";
        File id_set=new File("D:\\type.txt");
        System.out.println(id_set.exists());
        BufferedReader reader = null;
        try {
            reader=new BufferedReader(new InputStreamReader(new FileInputStream(id_set),"GB2312"));
            while (true) {
                tag= reader.readLine();

                if (tag==null) {
                 break;
                }
                else {

                    System.out.println(tag);

                    spider(tag, "type_test");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // spider(tag,"test");
    }
    public static void spider(String tag,String filename){
        String result= HttpRequest.sendGet("https://movie.douban.com/j/search_subjects", "type=movie"+"&tag="+tag+"&page_limit=40");
        TypeMovieBean obj= JSON.parseObject(result, TypeMovieBean.class);
        List<Subjects> movies=obj.getSubjects();
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i=0;i<movies.size();i++){
            ArrayList<String> tmpobjects = new ArrayList<String>();
            tmpobjects.add(movies.get(i).getId());
            tmpobjects.add(movies.get(i).getTitle());
            list.add(tmpobjects);
        }
        boolean Flag= SpiderUtil.createCsvFile(list,"D:\\CSVDir",filename);
        if (Flag == true)
        {
            System.out.print("CSV文件创建成功！");
        }else {
            System.out.print("CSV文件创建失败！");
        }
    }
}
