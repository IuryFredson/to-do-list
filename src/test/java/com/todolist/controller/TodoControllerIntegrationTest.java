package com.todolist.controller;

import com.todolist.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/todos";
    }

    @Test
    public void testCreateTodo() {
        Todo todo = new Todo("Test Todo", "This is a test todo", new Date(), "High");
        ResponseEntity<Todo> response = restTemplate.postForEntity(baseUrl, todo, Todo.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Todo", response.getBody().getTitle());
    }

    @Test
    public void testGetTodoById() {
        // Primeiro, cria um Todo para testar
        Todo todo = new Todo("Test Todo", "This is a test todo", new Date(), "High");
        ResponseEntity<Todo> createResponse = restTemplate.postForEntity(baseUrl, todo, Todo.class);
        String todoId = createResponse.getBody().getId();

        // Agora, busca o Todo pelo ID
        ResponseEntity<Todo> response = restTemplate.getForEntity(baseUrl + "/" + todoId, Todo.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(todoId, response.getBody().getId());
    }

    @Test
    public void testUpdateTodo() {
        // Primeiro, cria um Todo para testar
        Todo todo = new Todo("Test Todo", "This is a test todo", new Date(), "High");
        ResponseEntity<Todo> createResponse = restTemplate.postForEntity(baseUrl, todo, Todo.class);
        String todoId = createResponse.getBody().getId();

        // Atualiza o Todo
        Todo updatedTodo = new Todo("Updated Todo", "This is an updated todo", new Date(), "Low");
        updatedTodo.setId(todoId);
        restTemplate.put(baseUrl + "/" + todoId, updatedTodo);

        // Busca o Todo atualizado
        ResponseEntity<Todo> response = restTemplate.getForEntity(baseUrl + "/" + todoId, Todo.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Todo", response.getBody().getTitle());
    }

    @Test
    public void testDeleteTodo() {
        // Primeiro, cria um Todo para testar
        Todo todo = new Todo("Test Todo", "This is a test todo", new Date(), "High");
        ResponseEntity<Todo> createResponse = restTemplate.postForEntity(baseUrl, todo, Todo.class);
        String todoId = createResponse.getBody().getId();

        // Deleta o Todo
        restTemplate.delete(baseUrl + "/" + todoId);

        // Tenta buscar o Todo deletado
        ResponseEntity<Todo> response = restTemplate.getForEntity(baseUrl + "/" + todoId, Todo.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}