package com.learn.howe.springBoot.todo.controller;

import com.learn.howe.springBoot.todo.domain.Todo;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author Karl
 * @Date 2016/12/29 11:37
 */
@RestController
@RequestMapping("/api")
public class TodoPannel {
    private List<Todo> todoList = new ArrayList<>();

    @RequestMapping(value = "/todo", method = RequestMethod.POST)
    public Todo post(@RequestBody Todo todo) {
        todo.setId(UUID.randomUUID().toString());
        todoList.add(todo);
        return todo;
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.DELETE)
    public Todo delete(@PathVariable(name = "id") String id) {
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

    @RequestMapping(value = "/todo", method = RequestMethod.GET)
    public List<Todo> list() {
        return todoList;
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.GET)
    public Todo get(@PathVariable(name = "id") String id) {
        return todoList.stream().filter((todo) -> todo.getId().equals(id)).findFirst().get();
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.PUT)
    public Todo update(@PathVariable(name = "id") String id, @RequestBody Todo todo) {
        Optional<Todo> todoOptional = todoList.stream().
                filter((to -> to.getId().equals(id))).
                peek((to) -> {
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
