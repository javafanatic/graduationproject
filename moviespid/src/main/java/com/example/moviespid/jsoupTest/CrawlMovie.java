package com.example.moviespid.jsoupTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class CrawlMovie {

    public static void main(String[] args) {
        ArrayList<Movie> movies3 = crawlAllMovies();
        for (Movie movie : movies3) {
            System.out.println(movie.toString());
        }

        writeMoviesToTxt(movies3);
    }


    public static void crawlMovie() {


        final String URL = "https://movie.douban.com/top250";

        Document document = null;
        try {
            document = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Movie movie = new Movie();


        Element itemElement = document.select("ol li").first();



        Element rankElement = itemElement.selectFirst("em");
        String rankString = rankElement.text();
        System.out.println("rankString:" + rankString.toString());
        movie.setRank(new Integer(rankString));


        Element urlElement = itemElement.select("div.hd a").first();
        String urlString = urlElement.attr("href");
        System.out.println("urlString:" + urlString.toString());
        movie.setUrl(urlString);


        Element titleElement = urlElement.select("span.title").first();
        String titleString = titleElement.text();
        System.out.println("titleString:" + titleString.toString());
        movie.setTitle(titleString);


        Element ratingNumElement = itemElement.select("div.star span.rating_num").first();
        String ratingNumString = ratingNumElement.text();
        System.out.println("ratingNumString:" + ratingNumString.toString());
        movie.setRatingNum(new Double(ratingNumString));


        Element ratingPeopleNumElement = itemElement.select("div.star span").last();
        String ratingPeopleNumString = ratingPeopleNumElement.text();
        System.out.println("ratingPeopleNumString:" + ratingPeopleNumString.toString());


        movie.setRatingPeopleNum(
                new Integer(ratingPeopleNumString.substring(0, ratingPeopleNumString.length() - 3)));


        Element quoteElement = itemElement.select("p.quote span.inq").first();
        String quoteString = quoteElement.text();
        System.out.println("quoteString:" + quoteString.toString());
        movie.setQuote(quoteString);

        System.out.println(movie.toString());
    }


    public static ArrayList<Movie> crawlMovies(String URL) {



        Document document = null;
        try {
            document = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements itemElement = document.select("ol li");

        ArrayList<Movie> movies = new ArrayList<Movie>(25);

        for (Element element : itemElement) {
            Movie movie = new Movie();
            Element rankElement = element.selectFirst("em");
            String rankString = rankElement.text();
            movie.setRank(new Integer(rankString));


            Element urlElement = element.select("div.hd a").first();
            String urlString = urlElement.attr("href");
            movie.setUrl(urlString);


            Element titleElement = urlElement.select("span.title").first();
            String titleString = titleElement.text();
            movie.setTitle(titleString);


            Element ratingNumElement = element.select("div.star span.rating_num").first();
            String ratingNumString = ratingNumElement.text();
            movie.setRatingNum(new Double(ratingNumString));


            Element ratingPeopleNumElement = element.select("div.star span").last();
            String ratingPeopleNumString = ratingPeopleNumElement.text();
            movie.setRatingPeopleNum(
                    new Integer(ratingPeopleNumString.substring(0, ratingPeopleNumString.length() - 3)));


            Element quoteElement = element.select("p.quote span.inq").first();

            String quoteString = null;
            if (quoteElement == null) {
                quoteString = "";
            } else {
                quoteString = quoteElement.text();
            }
            movie.setQuote(quoteString);

            movies.add(movie);
        }

        return movies;
    }

    public static ArrayList<Movie> crawlAllMovies() {

        ArrayList<Movie> movies = new ArrayList<Movie>(250);


        String prefix = "https://movie.douban.com/top250";

        ArrayList<String> urlList = new ArrayList<String>(10);
        for (int i = 0; i < 11; i++) {
            String url = prefix + "?start=" + new Integer(i * 25).toString() + "&filter=";
            urlList.add(url);
        }


        for (String url : urlList) {
            movies.addAll(crawlMovies(url));
        }

        return movies;
    }


    public static void writeMoviesToTxt(ArrayList<Movie> movies) {


        ArrayList<String> moviesString = new ArrayList<String>(250);
        for (Movie movie : movies) {
            moviesString.add(movie.toString());
        }
        try (FileOutputStream out = new FileOutputStream("豆瓣电影top250.txt");) {
            for (String string : moviesString) {
                out.write(string.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

