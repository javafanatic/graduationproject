package com.miaozhen.apiserver.service;


import com.miaozhen.apiserver.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class MovieService {
    @Autowired
    private MongoTemplate mongoTemplate;
   public Movie getMovieById(int mid){
       Query query = new Query(Criteria.where("mid").is(mid));
       Movie movie =mongoTemplate.findOne(query, Movie.class);
       return movie;
   }
   public List<Movie> getMovieByType(String type){
       Pattern pattern= Pattern.compile("^.*"+type+".*$", Pattern.CASE_INSENSITIVE);
       Query query = new Query(Criteria.where("types").regex(pattern));
       List<Movie> movies=mongoTemplate.find(query,Movie.class);
       System.out.println(movies.size());
       return movies;
   }
}
