package com.learn.howe.springBoot.todo.service.impl;

import com.learn.howe.springBoot.todo.domain.Rank;
import com.learn.howe.springBoot.todo.service.RankListService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.learn.howe.springBoot.todo.cache.RedisConfig.RANK_KEY;

/**
 * @Author Karl
 * @Date 2016/12/30 20:34
 */
@Service
public class RankListServiceImpl implements RankListService {

    @Resource
    private RedisTemplate<String, Rank> rankRedisTemplate;

    @Override
    public List<Rank> top(int limit) {
        assert limit > 0;
        final AtomicLong index = new AtomicLong(1);
        return rankRedisTemplate
                .opsForZSet()
                .reverseRangeWithScores(RANK_KEY, 0, limit - 1)
                .stream()
                .filter((tuple) -> null != tuple.getValue())
                .map((tuple) -> {
                    Rank rank = tuple.getValue();
                    rank.setRank(index.getAndIncrement());
                    return rank;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Rank add(Rank rank) {
        rankRedisTemplate.opsForZSet().add(RANK_KEY, rank, rank.getScore());
        rank.setRank(rankRedisTemplate.opsForZSet().reverseRank(RANK_KEY, rank));
        return rank;
    }
}
