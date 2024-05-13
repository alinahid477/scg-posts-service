package dev.scg.example.post;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class PostJsonTest {

    @Autowired
    private JacksonTester<Post> jacksonTester;

    @Test
    void shouldSerializePost() throws Exception {
        Post post = new Post(1,1,"Hello, World!", "This is my first post.",null);
        JSONObject obj = new JSONObject();
        obj.put("id", post.id());
        obj.put("userId", post.userId());
        obj.put("title", post.title());
        obj.put("body", post.body());
        obj.put("version", null);

        String expected = obj.toString();

        assertThat(jacksonTester.write(post)).isEqualToJson(expected);
    }

    @Test
    void shouldDeserializePost() throws Exception {
        Post post = new Post(1,1,"Hello, World!", "This is my first post.",null);
        JSONObject obj = new JSONObject();
        obj.put("id", post.id());
        obj.put("userId", post.userId());
        obj.put("title", post.title());
        obj.put("body", post.body());
        obj.put("version", null);
        String content = obj.toString();
        
        assertThat(jacksonTester.parse(content)).isEqualTo(post);
        assertThat(jacksonTester.parseObject(content).id()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).userId()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).title()).isEqualTo("Hello, World!");
        assertThat(jacksonTester.parseObject(content).body()).isEqualTo("This is my first post.");
    }

}
