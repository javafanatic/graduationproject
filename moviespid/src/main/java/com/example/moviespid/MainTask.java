package com.example.moviespid;

import com.alibaba.fastjson.JSON;
import com.example.moviespid.bean.Casts;
import com.example.moviespid.bean.JsonRootBean;
import com.example.moviespid.bean.Subjects;

import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainTask {
    public static void main(String[] args) {
        spider(0,99,"1-100");
        spider(100,99,"101-200");
        spider(201,49,"200-205");
    }
    public static void spider(int start,int count,String filename){
        String result= HttpRequest.sendGet("https://douban.uieee.com/v2/movie/top250", "start="+start+"&count="+count);
        JsonRootBean obj=JSON.parseObject(result, JsonRootBean.class);
        List<Subjects> movies=obj.getSubjects();

        ArrayList<ArrayList<String> > list = new ArrayList<>();
        for(int i=0;i<movies.size();i++){
            ArrayList<String> tmpobjects = new ArrayList<String>();
            tmpobjects.add(movies.get(i).getId());
            tmpobjects.add(movies.get(i).getTitle());
            List<String> generous=movies.get(i).getGenres();
                String type="";
                for(int j=0;j<generous.size();j++){
                    type=type+generous.get(j)+"|";
                }
                type=type.subSequence(0,type.length()-1).toString();
                tmpobjects.add(type);
                tmpobjects.add(movies.get(i).getYear());
                tmpobjects.add(movies.get(i).getDurations().get(0).toString());
                tmpobjects.add(movies.get(i).getDirectors().get(0).getName());
                List<Casts> actors=movies.get(i).getCasts();
                String actornames="";
                for(int k=0;k<actors.size();k++){
                    actornames+=actors.get(k).getName()+"|";
            }
            if(actornames.length()>0) {
                actornames = actornames.subSequence(0, actornames.length() - 1).toString();
            }
            tmpobjects.add(actornames);
            list.add(tmpobjects);
            System.out.println(movies.get(i).getId()+movies.get(i).getTitle());
        }

        boolean Flag=createCsvFile(list,"D:\\CSVDir",filename);
        if (Flag == true)
        {
            System.out.print("CSV文件创建成功！");
        }else {
            System.out.print("CSV文件创建失败！");
        }
    }
    public static boolean createCsvFile(List<ArrayList<String> > rows, String filePath, String fileName)
    {
        //标记文件生成是否成功；
        boolean flag = true;

        //文件输出流
        BufferedWriter fileOutputStream = null;
        try{
            //含文件名的全路径
            String fullPath = filePath+ File.separator+fileName+".csv";
            File file = new File(fullPath);
            if (!file.getParentFile().exists())     //如果父目录不存在，创建父目录
            {
                file.getParentFile().mkdirs();
            }
            if (file.exists())     //如果该文件已经存在，删除旧文件
            {
                file.delete();
            }
            file = new File(fullPath);
            file.createNewFile();

            //格式化浮点数据
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMaximumFractionDigits(10);     //设置最大小数位为10；

            //格式化日期数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            //实例化文件输出流
            fileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"GBK"),1024);

            //遍历输出每行
            Iterator<ArrayList<String> > ite = rows.iterator();

            while (ite.hasNext())
            {
                ArrayList<String>  rowData = (ArrayList<String> )ite.next();
                for(int i=0;i<rowData.size();i++)
                {
                    Object obj = rowData.get(i);   //当前字段
                    //格式化数据
                    String field = "";
                    if (null != obj)
                    {
                        if (obj.getClass() == String.class)     //如果是字符串
                        {
                            field = (String)obj;
                        }else if (obj.getClass() == Double.class || obj.getClass() == Float.class)   //如果是浮点型
                        {
                            field = formatter.format(obj);   //格式化浮点数，使浮点数不以科学计数法输出
                        }else if (obj.getClass() == Integer.class || obj.getClass() == Long.class | obj.getClass() == Short.class || obj.getClass() == Byte.class)
                        {
                            //如果是整型
                            field += obj;
                        }else if (obj.getClass() == Date.class)   //如果是日期类型
                        {
                            field = sdf.format(obj);
                        }else {
                            field = " ";   //null时给一个空格占位
                        }
                        //拼接所有字段为一行数据
                        if (i<rowData.size()-1)     //不是最后一个元素
                        {
//                            System.out.print("\""+field+"\""+",");
                            fileOutputStream.write("\""+field+"\""+",");
                        }else {
                            //最后一个元素
                            fileOutputStream.write("\""+field+"\"");
                        }
                    }
                    //创建一个新行
                    if (ite.hasNext())
                    {
                        //fileOutputStream.newLine();
                    }
                }
                fileOutputStream.newLine();     //换行，创建一个新行；
            }
            fileOutputStream.flush();
        }catch (Exception e)
        {
            flag = false;
            e.printStackTrace();
        }finally {
            try{
                fileOutputStream.close();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }


}
