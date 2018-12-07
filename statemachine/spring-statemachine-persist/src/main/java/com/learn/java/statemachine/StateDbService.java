package com.learn.java.statemachine;

import org.springframework.stereotype.Service;

@Service
public class StateDbService {
    /**
     * 访问数据库加载状态
     * @param s
     * @return
     */
    public RegStatusEnum load(String s) {
        return RegStatusEnum.CONNECTED;
    }

    public void save(String s, RegStatusEnum state) {
        System.out.println("保存(" + s + ")状态:" + state.name());
    }
}
