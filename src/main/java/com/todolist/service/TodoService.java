package com.todolist.service;

import com.todolist.model.Todo;
import com.todolist.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Page<Todo> getAllTodos(Pageable pageable) {
        return todoRepository.findAll(pageable);
    }

    public Optional<Todo> getTodoById(String id) {
        return todoRepository.findById(id);
    }

    public Todo createTodo(Todo todo) {
        todo.setCompleted(false);
        return todoRepository.save(todo);
    }


    public Todo updateTodo(String id, Todo todo) {
        Optional<Todo> existingTodo = todoRepository.findById(id);
        if (existingTodo.isPresent()) {
            todo.setId(id);
            return todoRepository.save(todo);
        } else {
            throw new RuntimeException("Task not found with ID: " + id);
        }
    }

    public void deleteTodo(String id) {
        todoRepository.deleteById(id);
    }

}
