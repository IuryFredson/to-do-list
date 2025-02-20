package com.todolist.service;

import com.todolist.model.Todo;
import com.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetAllTodos() {
        // Arrange
        Todo todo1 = new Todo ("Task 1");
        Todo todo2 = new Todo ("Task 2");
        List<Todo> todos = Arrays.asList(todo1, todo2);
        when(todoRepository.findAll()).thenReturn(todos);

        // Act
        List<Todo> result = todoService.getAllTodos();

        // Assert
        assertEquals(2, result.size());
        verify(todoRepository, times(1)).findAll();
    }
    @Test
    void testGetTodoById(){
        // Arrange
        String id = "1";
        Todo todo = new Todo("Task 1");
        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        // Act
        Optional<Todo> result = todoService.getTodoById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Task 1", result.get().getTitle());
        verify(todoRepository, times(1)).findById(id);

    }

    @Test
    void testCreateTodo(){
        // Arrange
        Todo todo = new Todo("New Task");
        when(todoRepository.save(todo)).thenReturn(todo);

        // Act
        Todo result = todoService.createTodo(todo);

        // Assert
        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    void testDeleteTodo() {
        // Arrange
        String id = "1";
        doNothing().when(todoRepository).deleteById(id);

        // Act
        todoService.deleteTodo(id);

        // Assert
        verify(todoRepository, times(1)).deleteById(id);
    }
}

