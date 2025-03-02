package com.motycka.edu.game.account

import com.fasterxml.jackson.databind.ObjectMapper
import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import com.motycka.edu.game.account.rest.AccountResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AccountController::class)
class AccountControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountService: AccountService

    private val objectMapper = ObjectMapper()

    private val accountRegistrationRequest = AccountRegistrationRequest(
        name = "Motyka User",
        username = "motyka",
        password = "heslo"
    )

    private val account = Account(
        id = 1L,
        name = "Motyka User",
        username = "motyka",
        password = "heslo" // Plain text password
    )

    @BeforeEach
    fun setUp() {
        // Mock the accountService behavior
        every { accountService.createAccount(any()) } returns account
    }

    @Test
    fun `postAccount should create account and return 201 Created`() {
        mockMvc.perform(
            post("/api/accounts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRegistrationRequest))
        )
            .andExpect(status().isCreated) // Expect 201 Created
            .andExpect(jsonPath("$.id").value(account.id!!))
            .andExpect(jsonPath("$.name").value(account.name))
            .andExpect(jsonPath("$.username").value(account.username))
            .andExpect(jsonPath("$.password").value("***")) // Masked in response
    }

    @Test
    fun `postAccount should return 400 Bad Request for invalid input`() {
        val invalidRequest = AccountRegistrationRequest(
            name = "", // Empty name (invalid)
            username = "motyka",
            password = "heslo"
        )

        mockMvc.perform(
            post("/api/accounts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Name cannot be empty"))
            .andExpect(jsonPath("$.status").value(400))
    }

    @Test
    fun `postAccount should return 400 Bad Request for duplicate username`() {
        every { accountService.createAccount(any()) } throws IllegalArgumentException("Username already exists")

        mockMvc.perform(
            post("/api/accounts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRegistrationRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Username already exists"))
            .andExpect(jsonPath("$.status").value(400))
    }
}