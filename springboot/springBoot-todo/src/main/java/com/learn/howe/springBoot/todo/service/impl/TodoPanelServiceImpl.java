package com.learn.howe.springBoot.todo.service.impl;

import com.learn.howe.springBoot.todo.domain.Todo;
import com.learn.howe.springBoot.todo.service.TodoPanelService;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author Karl
 * @Date 2016/12/30 20:23
 */
@Service
public class TodoPanelServiceImpl implements TodoPanelService{

    private List<Todo> todoList;


    @Override
    public Todo add(Todo todo) {
        todo.setId(UUID.randomUUID().toString());
        todoList.add(todo);
        return todo;
    }

    @Override
    public Todo delete(String id) {
        Iterator<Todo> iter = todoList.iterator();
        Todo delete = null;
        while (iter.hasNext()) {
            delete = iter.next();
            if (delete.getId().equals(id)) {
                iter.remove();
                return delete;
            }
        }
        return null;
    }

    @Override
    public List<Todo> findAll() {
        return todoList;
    }

    @Override
    public Todo get(String id) {
        return todoList.stream().filter((todo) -> todo.getId().equals(id)).findFirst().get();
    }

    @Override
    public Todo update(Todo todo) {
        Optional<Todo> todoOptional = todoList.stream().
                filter((to -> to.getId().equals(todo.getId())))
                .peek((to) -> {
                    if (null != todo.getDescription())
                        to.setDescription(todo.getDescription());
                    if (null != todo.getTitle())
                        to.setTitle(todo.getTitle());
                })
                .limit(1)
                .findFirst();
        if (todoOptional.isPresent()) {
            return todoOptional.get();
        } else {
            return null;
        }
    }
}
