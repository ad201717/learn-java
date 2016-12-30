package com.learn.howe.springBoot.todo.controller;

import com.learn.howe.springBoot.todo.domain.Todo;
import com.learn.howe.springBoot.todo.service.TodoPanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author Karl
 * @Date 2016/12/29 11:37
 */
@RestController
@RequestMapping("/api/todo")
public class TodoPanel {

    @Autowired
    private TodoPanelService todoPanelService;

    @RequestMapping(method = RequestMethod.POST)
    public Todo post(@RequestBody Todo todo) {
        return todoPanelService.add(todo);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Todo delete(@PathVariable(name = "id") String id) {
        return todoPanelService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Todo> list() {
        return todoPanelService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Todo get(@PathVariable(name = "id") String id) {
        return todoPanelService.get(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Todo update(@PathVariable(name = "id") String id, @RequestBody Todo todo) {
        return todoPanelService.update(todo);
    }
}
