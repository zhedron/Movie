package zhedron.movie.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zhedron.movie.config.SecurityConfig;
import zhedron.movie.dto.response.FilmResponse;
import zhedron.movie.repository.UserRepository;
import zhedron.movie.services.FilmService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@Import({SecurityConfig.class, ControllerSecurityTestConfig.class})
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FilmService filmService;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void uploadFilmAcceptsMp4() throws Exception {
        MockMultipartFile video = new MockMultipartFile("video", "film.mp4", "video/mp4", "video".getBytes());
        when(filmService.uploadFilm(any())).thenReturn(new FilmResponse(6L, "film.mp4", "video/mp4"));

        mockMvc.perform(multipart("/api/film/upload").file(video))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.contentType").value("video/mp4"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void uploadFilmRejectsNonMp4() throws Exception {
        MockMultipartFile video = new MockMultipartFile("video", "film.mov", "video/quicktime", "video".getBytes());

        mockMvc.perform(multipart("/api/film/upload").file(video))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Upload video file with mp4 format"));
    }

    @Test
    void uploadFilmRejectsAnonymousUser() throws Exception {
        MockMultipartFile video = new MockMultipartFile("video", "film.mp4", "video/mp4", "video".getBytes());

        mockMvc.perform(multipart("/api/film/upload").file(video))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void uploadFilmRejectsNonAdminUser() throws Exception {
        MockMultipartFile video = new MockMultipartFile("video", "film.mp4", "video/mp4", "video".getBytes());

        mockMvc.perform(multipart("/api/film/upload").file(video))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteFilmReturnsMessage() throws Exception {
        mockMvc.perform(delete("/api/film/delete/6"))
                .andExpect(status().isOk())
                .andExpect(content().string("Film deleted successfully"));

        verify(filmService).deleteById(6L);
    }
}
