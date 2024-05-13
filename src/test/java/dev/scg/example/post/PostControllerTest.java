package dev.scg.example.post;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository repository;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        posts = List.of(
                new Post(1,1,"Hello, World!", "This is my first post.",null),
                new Post(2,1,"Second Post", "This is my second post.",null)
        );
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post.",
                        "version": null
                    },
                    {
                        "id":2,
                        "userId":1,
                        "title":"Second Post",
                        "body":"This is my second post.",
                        "version": null
                    }
                ]
                """;

        when(repository.findAll()).thenReturn(posts);

        ResultActions resultActions = mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        Post post = new Post(1,1,"Test Title", "Test Body",null);
        when(repository.findById(1)).thenReturn(Optional.of(post));
        JSONObject obj = new JSONObject();
        obj.put("id", post.id());
        obj.put("userId", post.userId());
        obj.put("title", post.title());
        obj.put("body", post.body());
        obj.put("version", null);

        String json = obj.toString();

        System.out.println(json);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldCreateNewPostWhenGivenValidID() throws Exception {
        Post post = new Post(3,1,"This is my brand new post", "TEST BODY",null);
        when(repository.save(post)).thenReturn(post);
        JSONObject obj = new JSONObject();
        obj.put("id", post.id());
        obj.put("userId", post.userId());
        obj.put("title", post.title());
        obj.put("body", post.body());
        obj.put("version", null);

        String json = obj.toString();

        System.out.println(json);
        

        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        Post updated = new Post(1,1,"This is my brand new post", "UPDATED BODY",1);
        when(repository.findById(1)).thenReturn(Optional.of(posts.get(0)));
        when(repository.save(updated)).thenReturn(updated);
        JSONObject obj = new JSONObject();
        obj.put("id", updated.id());
        obj.put("userId", updated.userId());
        obj.put("title", updated.title());
        obj.put("body", updated.body());
        obj.put("version", null);

        String requestBody = obj.toString();

        System.out.println(requestBody);
        

        mockMvc.perform(put("/api/posts/1")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostID() throws Exception {
        Post updated = new Post(50,1,"This is my brand new post", "UPDATED BODY",1);
        when(repository.save(updated)).thenReturn(updated);
        JSONObject obj = new JSONObject();
        obj.put("id", updated.id());
        obj.put("userId", updated.userId());
        obj.put("title", updated.title());
        obj.put("body", updated.body());
        obj.put("version", null);

        String json = obj.toString();

        System.out.println(json);

        mockMvc.perform(put("/api/posts/999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePostWhenGivenValidID() throws Exception {
        doNothing().when(repository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(repository, times(1)).deleteById(1);
    }

}