package com.miaozhen.apiserver.controller;


import com.miaozhen.apiserver.model.*;
import com.miaozhen.apiserver.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;
    @Autowired
    private MovieService movieService;
    @RequestMapping(value = "/usr/get")
    public String login(@RequestParam("loginname") String loginname, @RequestParam("password") String password, Map m, HttpSession session) {
        System.out.println(loginname);
      /*  if(userService.exists(u))
        {
            session.setAttribute("loginUser",loginname);
            return "redirect:/view/main";
        }
        else
        {
            return"redirect:/view/login";
        }
        */
        return "index";
    }
    @RequestMapping(value = "/viewLogin")
    public ModelAndView viewLogin(){
        System.out.println("识别了");
        ModelAndView mv=new ModelAndView();
        mv.setViewName("login");
        return mv;
    }
    @RequestMapping(value="/submitrate")
    public ModelAndView submitRate(@RequestParam("comment") String comment,@RequestParam("score") int score,@RequestParam("mid") int mid, HttpSession session){
        ModelAndView mv=new ModelAndView();
        System.out.println(comment);
        System.out.println(score);
        redisTemplate.opsForValue().set("my123","myValue");
        int uid=(int)session.getAttribute("uid");
        Long startTs = System.currentTimeMillis()/1000;

        System.out.println("uid  ;"+uid+"  mid:"+mid+" score: "+score+ "time: "+startTs);

        redisTemplate.opsForList().leftPush("uid:"+uid,mid+":"+score);

      //  redisTemplate.opsForSet().add("uid2:"+uid,mid+":"+score);
       // redisTemplate.
        String data=""+uid+"|"+mid+"|"+score+"|"+startTs;
        kafkaTemplate.send("recommender",data);
        mv.setViewName("login");
        return mv;
    }
    @RequestMapping(value="/login")
    public ModelAndView login(@RequestParam("username") String username,@RequestParam("passwd") String password,HttpSession session){
        System.out.println("login");
        int uid=Integer.parseInt(username);
        session.setAttribute("uid",uid);
        /*List<Movie> donghua=movieService.getMovieByType("动画");
        session.setAttribute("donghua",donghua);
        System.out.println("动画电影的数量"+donghua.size());*/
        ModelAndView mv=new ModelAndView();
        if(uid!=0&&password!=null&&!password.equals("")&&password.equals("123456")){
            Query query=new Query(Criteria.where("uid").is(uid));
            UserRec userRec=  mongoTemplate.findOne(query, UserRec.class);
            System.out.println(userRec.getUid());
            List<Integer> mids=new ArrayList<Integer>();
            List<Movie> movieList=new ArrayList<Movie>();
            List<UserRecommend> idrat=userRec.getRecs();
            List<Double> offvalues=new ArrayList<>();
            for(int i=0;i<idrat.size();i++){
                offvalues.add(idrat.get(i).getR());
                Query query2 = new Query(Criteria.where("mid").is(idrat.get(i).getRid()));
                Movie movie =mongoTemplate.findOne(query2, Movie.class);
                movieList.add(movie);
            }
             StreamRecs streamRecs=mongoTemplate.findOne(query,StreamRecs.class);

            int recentsize=0;
            List<RecentRate> results=new ArrayList<RecentRate>();

            if(streamRecs!=null&&streamRecs.getRecs()!=null&&!"".equals(streamRecs)){
                String[] items=streamRecs.getRecs().split("\\|");
                List<RecentRate> rates=new ArrayList<RecentRate>();
                for(int i=0;i<items.length;i++){
                    String [] tmp=items[i].split(":");
                    if(tmp.length>0){
                        rates.add(new RecentRate(Integer.parseInt(tmp[0]),Double.parseDouble(tmp[1])));
                    }

                }
                Collections.sort(rates);
                System.out.println(streamRecs.getRecs());
                recentsize=rates.size();
                if(recentsize>=4){
                    recentsize=4;
                }
                List<Double> recvalues=new ArrayList<>();
                for(int i=0;i<recentsize;i++){
                    results.add(rates.get(i));
                    recvalues.add(rates.get(i).getScore());
                }
                List<Movie> recentmovies=new ArrayList<Movie>();

                for(int i=0;i<recentsize;i++){
                    Query query2 = new Query(Criteria.where("mid").is(results.get(i).getMid()));
                    Movie movie =mongoTemplate.findOne(query2, Movie.class);
                    recentmovies.add(movie);
                }
                mv.addObject("recentlist",recentmovies);
                mv.addObject("scorelist",recvalues);
                System.out.println(rates.get(0).getMid()+" "+rates.get(0).getScore());
            }


            System.out.println(recentsize+"sdsa");
             mv.addObject("recentsize",recentsize);
            // mv.addObject("recentlist",results);

            System.out.println(movieList.size());
            mv.addObject("movielist",movieList);
            mv.addObject("offlist",offvalues);

            mv.addObject("ml","123");
            mv.setViewName("MyIndex.html");
        }
        else{
            mv.addObject("errinfo","用户名或密码错误，请返回重新输入");
            mv.setViewName("error");
        }
        return mv;
    }

}
