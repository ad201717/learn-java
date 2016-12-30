package com.learn.howe.springBoot.todo.domain;

import java.io.Serializable;

/**
 * @Author Karl
 * @Date 2016/12/30 20:30
 */
public class Rank implements Serializable {

    private static final long serialVersionUID = 7854013915134640158L;

    private String name;

    private double score;

    private Long rank;

    public Rank(){}

    public Rank(String name, double score){
        this.name = name;
         this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }
}
