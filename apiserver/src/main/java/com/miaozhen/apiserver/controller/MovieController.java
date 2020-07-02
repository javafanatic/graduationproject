package com.miaozhen.apiserver.controller;

import com.miaozhen.apiserver.model.Comment;
import com.miaozhen.apiserver.model.Movie;
import com.miaozhen.apiserver.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller

public class MovieController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MovieService movieService;


    @RequestMapping("/moviedetail")
    public ModelAndView  GetMovieDetail(@RequestParam("mid") int mid){
        System.out.println(mid);
        Query query=new Query(Criteria.where("mid").is(mid));

        List<Comment> commentsList= mongoTemplate.find(query, Comment.class);
        System.out.println(commentsList.size());

        Movie movie=movieService.getMovieById(mid);
        ModelAndView mv=new ModelAndView();
        mv.addObject("movie",movie);
        mv.addObject("test","test123");
        mv.addObject("commentlist",commentsList);
        mv.setViewName("/MovieDetail");
        return mv;
    }
    @RequestMapping("/typemovie")
    public ModelAndView totypemovie(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("TypeMovie");
        return mv;
    }
    @RequestMapping("/typesearch")
    public ModelAndView searchMovieByType(@RequestParam(value = "type",defaultValue = "动画") String type ){
        ModelAndView mv=new ModelAndView();
        switch (type){
            case "犯罪":
                mv.addObject("type","fanzui");
                break;
            case  "剧情":
                mv.addObject("type","juqing");
                break;
            case   "爱情":
                mv.addObject("type","aiqing");
                break;
            case  "同性":
                mv.addObject("type","tongxing");
                break;
            case   "动作":
                mv.addObject("type","dongzuo");
                break;
            case  "喜剧":
                mv.addObject("type","xiju");
                break;
            case   "灾难":
                mv.addObject("type","zainan");
                break;
            case  "奇幻":
                mv.addObject("type","qihuan");
                break;
            case   "科幻":
                mv.addObject("type","kehuan");
                break;
            case  "历史":
                mv.addObject("type","lishi");
                break;
            case   "战争":
                mv.addObject("type","zhanzheng");
                break;
            default:mv.addObject("type","donghua");
                 break;
        }
        List<Movie> movieList= movieService.getMovieByType(type);
        mv.addObject("typelist",movieList);
        mv.setViewName("TypeMovie");
        return mv;
    }
}
