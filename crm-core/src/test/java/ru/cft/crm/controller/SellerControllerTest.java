package ru.cft.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.repository.SellerRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DisplayName("Тесты SellerController")
public class SellerControllerTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SellerRepository sellerRepository;

    @BeforeEach
    public void setUp() {
        sellerRepository.deleteAll();
        Seller seller = new Seller();
        seller.setSellerName("Seller");
        seller.setContactInfo("testSeller@mail.ru");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        seller.setIsActive(true);
        sellerRepository.save(seller);

        Seller activeSeller = new Seller();
        activeSeller.setSellerName("Active Seller");
        activeSeller.setContactInfo("testaSeller@mail.ru");
        activeSeller.setRegistrationDate(LocalDateTime.now());
        activeSeller.setUpdatedAt(LocalDateTime.now());
        activeSeller.setIsActive(true);
        sellerRepository.save(activeSeller);

        Seller inactiveSeller = new Seller();
        inactiveSeller.setSellerName("Inactive Seller");
        inactiveSeller.setContactInfo("testiSeller@mail.ru");
        inactiveSeller.setRegistrationDate(LocalDateTime.now());
        inactiveSeller.setUpdatedAt(LocalDateTime.now());
        inactiveSeller.setIsActive(false);
        sellerRepository.save(inactiveSeller);
    }

    @Test
    @DisplayName("Тест на создание продавца")
    public void testCreateSeller() throws Exception {
        String requestBody = """
                {
                  "sellerName": "Test Seller",
                  "contactInfo": "test@mail.ru"
                }
                """;

        mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerName").value("Test Seller"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на создание продавца с уже существующим email")
    public void testCreateSellerWithSameEmail() throws Exception {
        String requestBody = """
                {
                  "sellerName": "Test Seller",
                  "contactInfo": "testSeller@mail.ru"
                }
                """;

        mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Продавец с таким email уже существует"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на создание продавца(некорректный запрос)")
    public void testCreateSellerValidationFailure() throws Exception {
        String requestBody = """
                {
                    "sellerName": "",
                    "contactInfo": ""
                }
                """;

        mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Ошибка валидации"))
                .andDo(print());

    }

    @Test
    @DisplayName("Тест на получение всех активных продавцов")
    public void testGetAllActiveSellers() throws Exception {
        mockMvc.perform(get("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].sellerName").value("Seller"))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[1].sellerName").value("Active Seller"))
                .andExpect(jsonPath("$[1].isActive").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение всех продавцов (активных и неактивных)")
    public void testGetAllSellers() throws Exception {
        mockMvc.perform(get("/api/sellers/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].sellerName").value("Seller"))
                .andExpect(jsonPath("$[1].sellerName").value("Active Seller"))
                .andExpect(jsonPath("$[2].sellerName").value("Inactive Seller"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение продавца по id")
    public void testGetSellerById() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);

        mockMvc.perform(get("/api/sellers/" + seller.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerName").value("Seller"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на удаление продавца по id")
    public void testDeleteSellerById() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);

        mockMvc.perform(delete("/api/sellers/" + seller.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на обновление продавца")
    public void testUpdateSeller() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);

        String updateRequestBody = """
                {
                  "sellerName": "Updated Seller Name",
                  "isActive": false
                }
                """;

        mockMvc.perform(patch("/api/sellers/" + seller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerName").value("Updated Seller Name"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на создание продавца(некорректный запрос)")
    public void testUpdateSellerValidationFailure() throws Exception {
        String requestBody = """
                {
                    "sellerName": "",
                    "contactInfo": ""
                }
                """;

        mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Ошибка валидации"))
                .andDo(print());
    }
}
