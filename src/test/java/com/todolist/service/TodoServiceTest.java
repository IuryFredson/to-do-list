package com.todolist.service;

import com.todolist.model.Todo;
import com.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Date;
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
        Todo todo1 = new Todo("Task 1", "Description 1", new Date(), "High");
        Todo todo2 = new Todo("Task 2", "Description 2", new Date(), "Medium");
        List<Todo> todos = Arrays.asList(todo1, todo2);
        when(todoRepository.findAll()).thenReturn(todos);

        // Act
        List<Todo> result = todoService.getAllTodos();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Description 1", result.get(0).getDescription());
        assertEquals("High", result.get(0).getPriority());
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTodosPaged() {
        // Arrange
        Todo todo1 = new Todo("Task 1", "Description 1", new Date(), "High");
        Todo todo2 = new Todo("Task 2", "Description 2", new Date(), "Medium");
        List<Todo> todos = Arrays.asList(todo1, todo2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> todoPage = new PageImpl<>(todos, pageable, todos.size());
        when(todoRepository.findAll(pageable)).thenReturn(todoPage);

        // Act
        Page<Todo> result = todoService.getAllTodos(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Task 1", result.getContent().get(0).getTitle());
        assertEquals("Description 1", result.getContent().get(0).getDescription());
        assertEquals("High", result.getContent().get(0).getPriority());
        verify(todoRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetTodoById() {
        // Arrange
        String id = "1";
        Todo todo = new Todo("Task 1", "Description 1", new Date(), "High");
        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        // Act
        Optional<Todo> result = todoService.getTodoById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Task 1", result.get().getTitle());
        assertEquals("Description 1", result.get().getDescription());
        assertEquals("High", result.get().getPriority());
        verify(todoRepository, times(1)).findById(id);
    }

    @Test
    void testCreateTodo() {
        // Arrange
        Todo todo = new Todo("New Task", "New Description", new Date(), "Low");
        when(todoRepository.save(todo)).thenReturn(todo);

        // Act
        Todo result = todoService.createTodo(todo);

        // Assert
        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals("Low", result.getPriority());
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
    @Test
    void testUpdateTodo() {
        // Arrange
        String id = "1";
        Todo existingTodo = new Todo("Task 1", "Description 1", new Date(), "High");
        existingTodo.setId(id);

        Todo updatedTodo = new Todo("Updated Task", "Updated Description", new Date(), "Medium");
        updatedTodo.setId(id);

        when(todoRepository.findById(id)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(updatedTodo)).thenReturn(updatedTodo);

        // Act
        Todo result = todoService.updateTodo(id, updatedTodo);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("Medium", result.getPriority());
        verify(todoRepository, times(1)).findById(id);
        verify(todoRepository, times(1)).save(updatedTodo);
    }
}
