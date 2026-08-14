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
import zhedron.movie.dto.response.SeasonResponse;
import zhedron.movie.dto.response.request.SeasonCreateRequest;
import zhedron.movie.repository.UserRepository;
import zhedron.movie.services.SeasonService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeasonController.class)
@Import({SecurityConfig.class, ControllerSecurityTestConfig.class})
class SeasonControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SeasonService seasonService;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createSeasonReturnsCreatedSeason() throws Exception {
        SeasonCreateRequest request = new SeasonCreateRequest();
        request.setSeasonNumber(2);
        when(seasonService.createSeason(any(SeasonCreateRequest.class))).thenReturn(new SeasonResponse(4L, List.of(), 2));

        mockMvc.perform(post("/api/season/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.seasonNumber").value(2));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void getSeasonByIdReturnsSeason() throws Exception {
        when(seasonService.findById(4L)).thenReturn(new SeasonResponse(4L, List.of(), 2));

        mockMvc.perform(get("/api/season/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void addEpisodeUsesRequestParameters() throws Exception {
        when(seasonService.addEpisodeToSeason(4L, 12L)).thenReturn(new SeasonResponse(4L, List.of(), 2));

        mockMvc.perform(post("/api/season/add/episode")
                        .param("seasonId", "4")
                        .param("episodeId", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteSeasonReturnsMessage() throws Exception {
        mockMvc.perform(delete("/api/season/delete/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("Season deleted successfully"));

        verify(seasonService).deleteById(4L);
    }
}
