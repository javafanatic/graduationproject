package com.example.moviespid;

import com.alibaba.fastjson.JSON;
import com.example.moviespid.CommentBean.RootBean;
import com.example.moviespid.MovieDetailBean.Casts;
import com.example.moviespid.MovieDetailBean.MovieDeatilBean;
import com.example.moviespid.util.SpiderUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailSpider {
    public static void main(String[] args) {
        ArrayList<String> source=new ArrayList<String>();
        File id_set=new File("D:\\mid11.txt");
        BufferedReader reader = null;
        String mid=null;
        try {
            reader=new BufferedReader(new InputStreamReader(new FileInputStream(id_set),"GB2312"));
            while (true) {
                mid= reader.readLine();

                if (mid==null||mid.equals("")) {
                    break;
                }
                else {
                    source.add(mid);
                    System.out.println(mid);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<source.size();i++) {
           try {
               spider(source.get(i), "movie_detail_last101");
           }
           catch (Exception e){
               System.out.println("bugid："+source.get(i));
               e.printStackTrace();
               continue;

           }
        }

    }
    public static void spider(String mid,String filename){
        String result=HttpRequest.sendGet("https://douban.uieee.com/v2/movie/subject/"+mid,null);
        MovieDeatilBean root= JSON.parseObject(result, MovieDeatilBean.class);
        ArrayList<ArrayList<String> > list = new ArrayList<>();
        ArrayList<String> tmpobjects = new ArrayList<String>();
        tmpobjects.add(mid);
        tmpobjects.add(root.getTitle());
        if(root.getHas_video()){
            tmpobjects.add(root.getVideos().get(0).getSample_link());

        }
        else{
            tmpobjects.add("无");
        }
        tmpobjects.add(root.getImages().getSmall());
        tmpobjects.add(root.getImages().getMedium());
        tmpobjects.add(root.getImages().getLarge());
       // tmpobjects.add(root.getPubdate());
        tmpobjects.add(root.getYear());
        String tags_tmp="";
        List<String> types=root.getTags();
        for(int w=0;w<types.size();w++){
            tags_tmp+=types.get(w)+"|";
        }
        if(tags_tmp.length()>0) {
            tags_tmp= tags_tmp.subSequence(0,tags_tmp.length() - 1).toString();
        }
        tmpobjects.add(tags_tmp);
        List<Casts> actors=root.getCasts();
        String actornames="";
        for(int k=0;k<actors.size();k++){
            actornames+=actors.get(k).getName()+"|";
        }
        if(actornames.length()>0) {
            actornames = actornames.subSequence(0, actornames.length() - 1).toString();
        }
        tmpobjects.add(actornames);

        List<String> genenres=root.getGenres();
        String generesres="";
        for(int k=0;k<genenres.size();k++){
            generesres+=genenres.get(k)+"|";
        }
        if(generesres.length()>0) {
            generesres = generesres.subSequence(0, generesres.length() - 1).toString();
        }
        if(root.getCountries()!=null&&root.getCountries().size()!=0){
            tmpobjects.add(root.getCountries().get(0));
        }
        else{
            tmpobjects.add("null");
        }
        tmpobjects.add(root.getSummary().replaceAll("\r|\n", ""));
        if(root.getLanguages()!=null&&root.getLanguages().size()!=0){
            tmpobjects.add(root.getLanguages().get(0));
        }
        else{
            tmpobjects.add("null");
        }
        if(root.getDirectors()!=null&&root.getDirectors().size()!=0){
            tmpobjects.add(root.getDirectors().get(0).getName());
        }
        else{
            tmpobjects.add("null");
        }
        if(root.getWriters()!=null&&root.getWriters().size()!=0){
            tmpobjects.add(root.getWriters().get(0).getName());
        }
        else{
            tmpobjects.add("null");
        }

        list.add(tmpobjects);
        boolean Flag=SpiderUtil.createCsvFile(list,"D:\\CSVDir",filename);
        if (Flag == true)
        {
            System.out.print("CSV文件创建成功！");
        }else {
            System.out.print("CSV文件创建失败！");
        }


    }
}
