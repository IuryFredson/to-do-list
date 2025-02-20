package com.todolist.controller;

import com.todolist.model.Todo;
import com.todolist.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @Test
    void testGetAllTodos() throws Exception {
        // Arrange
        Todo todo1 = new Todo("Task 1", "Description 1", new Date(), "High");
        Todo todo2 = new Todo("Task 2", "Description 2", new Date(), "Medium");
        List<Todo> todos = Arrays.asList(todo1, todo2);
        when(todoService.getAllTodos()).thenReturn(todos);

        // Act & Assert
        mockMvc.perform(get("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].priority").value("High"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].priority").value("Medium"));
    }

    @Test
    void testGetAllTodosPaged() throws Exception {
        // Arrange
        Todo todo1 = new Todo("Task 1", "Description 1", new Date(), "High");
        Todo todo2 = new Todo("Task 2", "Description 2", new Date(), "Medium");
        List<Todo> todos = Arrays.asList(todo1, todo2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> todoPage = new PageImpl<>(todos, pageable, todos.size());
        when(todoService.getAllTodos(pageable)).thenReturn(todoPage);

        // Act & Assert
        mockMvc.perform(get("/api/v1/todos/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dueDate,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Task 1"))
                .andExpect(jsonPath("$.content[0].description").value("Description 1"))
                .andExpect(jsonPath("$.content[0].priority").value("High"))
                .andExpect(jsonPath("$.content[1].title").value("Task 2"))
                .andExpect(jsonPath("$.content[1].description").value("Description 2"))
                .andExpect(jsonPath("$.content[1].priority").value("Medium"));
    }

    @Test
    void testGetTodoById() throws Exception {
        // Arrange
        String id = "1";
        Todo todo = new Todo("Task 1", "Description 1", new Date(), "High");
        when(todoService.getTodoById(id)).thenReturn(Optional.of(todo));

        // Act & Assert
        mockMvc.perform(get("/api/v1/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.priority").value("High"));
    }

    @Test
    void testGetTodoByIdNotFound() throws Exception {
        // Arrange
        String id = "1";
        when(todoService.getTodoById(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTodo() throws Exception {
        // Arrange
        Todo todo = new Todo("New Task", "New Description", new Date(), "Low");
        when(todoService.createTodo(any(Todo.class))).thenReturn(todo);

        // Act & Assert
        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Task\", \"description\": \"New Description\", \"dueDate\": \"2023-12-31T23:59:59Z\", \"priority\": \"Low\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.priority").value("Low"));
    }

    @Test
    void testUpdateTodo() throws Exception {
        // Arrange
        String id = "1";
        Todo todo = new Todo("Updated Task", "Updated Description", new Date(), "Medium");
        when(todoService.getTodoById(id)).thenReturn(Optional.of(todo));
        when(todoService.updateTodo(eq(id), any(Todo.class))).thenReturn(todo);

        // Act & Assert
        mockMvc.perform(put("/api/v1/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Task\", \"description\": \"Updated Description\", \"dueDate\": \"2023-12-31T23:59:59Z\", \"priority\": \"Medium\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.priority").value("Medium"));
    }

    @Test
    void testUpdateTodoNotFound() throws Exception {
        // Arrange
        String id = "1";
        when(todoService.getTodoById(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/v1/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Task\", \"description\": \"Updated Description\", \"dueDate\": \"2023-12-31T23:59:59Z\", \"priority\": \"Medium\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTodo() throws Exception {
        // Arrange
        String id = "1";
        when(todoService.getTodoById(id)).thenReturn(Optional.of(new Todo("Task 1", "Description 1", new Date(), "High")));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTodoNotFound() throws Exception {
        // Arrange
        String id = "1";
        when(todoService.getTodoById(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}