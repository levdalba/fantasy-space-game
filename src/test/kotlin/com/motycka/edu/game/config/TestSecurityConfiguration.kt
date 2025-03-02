package com.motycka.edu.game.config

import com.motycka.edu.game.account.AccountService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@SpringBootTest
class TestSecurityConfiguration {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var userService: AccountService

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply { SecurityMockMvcConfigurers.springSecurity() }
            .build()

        // Mock userService behavior
        every { userService.getByUsername("motyka") } returns com.motycka.edu.game.account.AccountFixtures.DEVELOPER.copy(
            username = "motyka",
            password = "heslo" // Use plain text password
        )
        every { userService.getByUsername("unknown") } returns null
    }

    @Test
    fun `should permit GET login-html without authentication`() {
        mockMvc.perform(get("/login.html"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should permit POST api-accounts without authentication`() {
        mockMvc.perform(
            post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Test User","username":"testuser","password":"password"}""")
        )
            .andExpect(status().isCreated) // Assuming AccountController returns 201 Created
    }

    @Test
    fun `should require authentication for other endpoints`() {
        mockMvc.perform(get("/api/characters"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "motyka", password = "heslo")
    fun `should allow access to protected endpoints with authentication`() {
        mockMvc.perform(get("/api/characters"))
            .andExpect(status().isOk) // Assuming CharacterController returns 200 OK
    }

    @Test
    fun `userDetailsService should return user details for valid username`() {
        val securityConfiguration = SecurityConfiguration(userService)
        val userDetailsService = securityConfiguration.userDetailsService()
        val userDetails = userDetailsService.loadUserByUsername("motyka")

        assertEquals("motyka", userDetails.username)
        assertEquals("USER", userDetails.authorities.first().authority)
    }

    @Test
    fun `userDetailsService should throw UsernameNotFoundException for invalid username`() {
        val securityConfiguration = SecurityConfiguration(userService)
        val userDetailsService = securityConfiguration.userDetailsService()

        val exception = assertThrows<org.springframework.security.core.userdetails.UsernameNotFoundException> {
            userDetailsService.loadUserByUsername("unknown")
        }
        assertEquals("User not found: unknown", exception.message)
    }

    @Test
    fun `passwordEncoder should be NoOpPasswordEncoder`() {
        val securityConfiguration = SecurityConfiguration(userService)
        val passwordEncoder = securityConfiguration.passwordEncoder()

        assertEquals(org.springframework.security.crypto.password.NoOpPasswordEncoder::class.java, passwordEncoder::class.java)

        // Verify that the encoder works (plain text comparison)
        val password = "heslo"
        val encodedPassword = passwordEncoder.encode(password) // NoOpPasswordEncoder returns the password as-is
        assertTrue(passwordEncoder.matches(password, encodedPassword))
    }

    @Test
    fun `should throw IllegalArgumentException if userService is null`() {
        val exception = assertThrows<IllegalArgumentException> {
            SecurityConfiguration(null)
        }
        assertEquals("AccountService must not be null", exception.message)
    }
}

@Configuration
@EnableWebSecurity
class TestSecurityConfig {

    @Bean
    fun userService(): AccountService = mockk()
}