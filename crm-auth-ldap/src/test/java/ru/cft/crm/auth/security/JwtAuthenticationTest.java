package ru.cft.crm.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.cft.crm.auth.security.jwt.JwtTokenProvider;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "jwt.secret=TestSecretKeyForJWTThatShouldBeAtLeast256BitsLongForHS256AlgorithmToWork",
        "jwt.expiration=86400000"
})
class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

     */
    @Test
    void whenNoToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/sellers"))
                .andExpect(status().isUnauthorized());
    }

     */
    @Test
    void whenValidTokenWithAuthority_thenSuccess() throws Exception {
        String token = jwtTokenProvider.createToken("testuser", 
                List.of("ROLE_USER", "READ_SELLERS"));

        mockMvc.perform(get("/api/sellers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void whenMockUserWithAdminRole_thenCanDeleteSeller() throws Exception {
        mockMvc.perform(delete("/api/sellers/999"))
                .andExpect(status().isNotFound()); // Не найден, но авторизация прошла
    }

     */
    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void whenMockUserWithoutAdminRole_thenForbidden() throws Exception {
        mockMvc.perform(delete("/api/sellers/1"))
                .andExpect(status().isForbidden());
    }

     */
    @Test
    @WithMockUser(username = "writer", authorities = {"WRITE_SELLERS"})
    void whenUserHasWriteSellersAuthority_thenCanCreate() throws Exception {
        String requestBody = """
                {
                    "name": "Test Seller",
                    "contactInfo": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

     */
    @Test
    void testTokenAuthoritiesExtraction() {
        String username = "testuser";
        List<String> authorities = List.of("ROLE_ADMIN", "READ_SELLERS", "WRITE_TRANSACTIONS");

        String token = jwtTokenProvider.createToken(username, authorities);

        assert jwtTokenProvider.validateToken(token);

        String extractedUsername = jwtTokenProvider.getUsername(token);
        List<String> extractedAuthorities = jwtTokenProvider.getAuthorities(token);

        assert extractedUsername.equals(username);
        assert extractedAuthorities.containsAll(authorities);
    }
}

