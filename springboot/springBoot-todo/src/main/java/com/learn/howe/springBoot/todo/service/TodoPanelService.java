package com.learn.howe.springBoot.todo.service;

import com.learn.howe.springBoot.todo.domain.Todo;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/12/30 20:12
 */
public interface TodoPanelService {

    Todo add(Todo todo);

    Todo delete(String id);

    List<Todo> findAll();

    Todo get(String id);

    Todo update(Todo todo);
}
