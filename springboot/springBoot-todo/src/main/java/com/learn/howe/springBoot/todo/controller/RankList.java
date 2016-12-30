package com.learn.howe.springBoot.todo.controller;

import com.learn.howe.springBoot.todo.domain.Rank;
import com.learn.howe.springBoot.todo.service.RankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.learn.howe.springBoot.todo.cache.RedisConfig.RANK_KEY;

/**
 * @Author Karl
 * @Date 2016/12/30 20:10
 */
@RestController
@RequestMapping("/api/rankList")
public class RankList {

    @Autowired
    private RankListService rankListService;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public void init(){
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("鬼吹灯之精绝古城",8.5);
        map.put("孤单又灿烂的神：鬼怪",8.8);
        map.put("9号秘事 第三季",9.2);
        map.put("控方证人",7.7);
        map.put("西部世界 第一季",9.0);
        map.put("蓝色大海的传说",7.3);
        map.put("我们这一天",9.5);
        map.put("逃避虽可耻但有用",8.4);
        map.put("地球脉动 第二季",9.9);
        map.put("请回答1988",9.5);
        map.put("放弃我，抓紧我",4.7);
        map.put("心里的声音",9.0);
        map.put("举重妖精金福珠",8.1);
        map.put("羞耻 第三季",9.5);
        map.put("黑镜 第三季",8.7);
        map.put("王冠",9.2);
        map.put("校对女孩河野悦子",7.6);
        map.put("我在故宫修文物",9.4);
        map.put("一善之差",8.4);
        map.put("北上广依然相信爱情",6.2);
        map.put("瓣嘴",6.8);
        map.put("年轻的教宗 第一季",9.1);
        map.put("如果蜗牛有爱情",7.5);
        map.put("无耻之徒(美版) 第七季",9.6);
        map.put("法医秦明",7.1);
        map.put("青云志2",4.5);
        map.put("只有吉祥寺是想住的街道吗？",8.8);
        map.put("非正式会谈 第三季",9.2);
        map.put("百年酒馆",9.4);
        map.put("大明王朝1566",9.5);
        map.put("超感猎杀 第一季",8.7);
        map.put("行尸走肉 第七季",8.8);
        map.put("一年生",8.8);
        map.put("火花",9.3);
        map.put("致命复活",7.0);
        map.put("信号",9.1);
        map.put("心理罪2",6.7);
        map.put("伦敦生活",8.7);
        map.put("代价",8.0);
        map.put("生活大爆炸 第十季",9.1);
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            rankListService.add(new Rank(entry.getKey(), entry.getValue()));
        }
    }

    @RequestMapping(value = "/top/{limit}", method = RequestMethod.GET)
    public List<Rank> top(@PathVariable(name = "limit") int limit) {
        return rankListService.top(limit);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Rank add(@RequestBody Rank rank) {
        return rankListService.add(rank);
    }
}
