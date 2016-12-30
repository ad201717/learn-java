package com.learn.howe.springBoot.todo.service;

import com.learn.howe.springBoot.todo.domain.Rank;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/12/30 20:33
 */
public interface RankListService {

    List<Rank> top(int limit);

    Rank add(Rank rank);
}
