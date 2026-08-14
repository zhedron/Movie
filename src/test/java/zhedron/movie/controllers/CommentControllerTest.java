package zhedron.movie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zhedron.movie.config.SecurityConfig;
import zhedron.movie.dto.response.CommentResponse;
import zhedron.movie.dto.response.request.CommentRequest;
import zhedron.movie.repository.UserRepository;
import zhedron.movie.services.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import({SecurityConfig.class, ControllerSecurityTestConfig.class})
class CommentControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void createCommentReturnsCreated() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("Nice");
        when(commentService.addComment(any(CommentRequest.class), org.mockito.ArgumentMatchers.eq(9L)))
                .thenReturn(new CommentResponse(3L, "Nice", null, null, null, false));

        mockMvc.perform(post("/api/comment/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.text").value("Nice"));
    }

    @Test
    void createCommentRejectsAnonymousUser() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("Nice");

        mockMvc.perform(post("/api/comment/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void findCommentDelegatesToService() throws Exception {
        when(commentService.findById(3L, 9L)).thenReturn(new CommentResponse(3L, "Nice", null, null, null, false));

        mockMvc.perform(get("/api/comment/3/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void findCommentRejectsAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/comment/3/9"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deleteCommentReturnsMessage() throws Exception {
        mockMvc.perform(delete("/api/comment/delete/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment deleted successfully"));

        verify(commentService).deleteComment(3L);
    }

    @Test
    void deleteCommentRejectsAnonymousUser() throws Exception {
        mockMvc.perform(delete("/api/comment/delete/3"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void editCommentReturnsUpdatedComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("Updated");
        when(commentService.editComment(any(CommentRequest.class), org.mockito.ArgumentMatchers.eq(3L)))
                .thenReturn(new CommentResponse(3L, "Updated", null, null, null, true));

        mockMvc.perform(put("/api/comment/edit/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.edited").value(true))
                .andExpect(jsonPath("$.text").value("Updated"));
    }
}
