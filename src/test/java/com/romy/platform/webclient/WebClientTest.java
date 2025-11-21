package com.romy.platform.webclient;

import com.romy.platform.common.provider.HttpClientProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
public class WebClientTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/posts";

    @Autowired
    private HttpClientProvider httpClientProvider;

    @Nested
    class TestRequest {
        private String title;
        private String body;
        private int userId;

        public TestRequest(String title, String body, int userId) {
            this.title = title;
            this.body = body;
            this.userId = userId;
        }
    }

    static class TestResponse {
        private int id;

        public TestResponse() {
            // Default constructor for deserialization
        }

        public TestResponse(int id) {
            this.id = id;
        }

    }

    @Test
    void testGet() {
        String url =  BASE_URL + "/1";

        String result = httpClientProvider.get(url, String.class);
        System.out.println("result = " + result);

        assertNotNull(result);
    }

    @Test
    void testPost() throws JsonProcessingException {
        String url =  BASE_URL;
        TestRequest request = new TestRequest("foo", "bar", 1);

        TestResponse result = httpClientProvider.post(url, request, TestResponse.class);
        System.out.println("result = " + result);

        assertNotNull(result);
    }

    @Test
    void testDelete() {
        String url = BASE_URL + "/1";

        String result = httpClientProvider.delete(url, String.class);
        System.out.println("result = " + result);

        assertNotNull(result);
    }

    @Test
    void testPut() throws JsonProcessingException {
        String url = BASE_URL + "/1";
        TestRequest request = new TestRequest("foo", "bar", 1);

        String result = httpClientProvider.put(url, request, String.class);
        assertNotNull(result);
    }

    @Test
    void multipartTest() {
        String url = "https://httpbin.org/post";

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", "foo");
        builder.part("body", "bar");
        builder.part("userId", 1);

        builder.part("file", new File("newFiles.txt"));
        builder.part("file", new File("newFiles.txt"));

        String s = httpClientProvider.postMultipart(url, builder, String.class);
        System.out.println("s = " + s);

        assertNotNull(s);
    }

}
