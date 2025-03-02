package com.motycka.edu.game

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue // Import assertTrue
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GameApplicationTest {

    @Test
    fun contextLoads() {
        // This test will simply start the application context to verify if the context loads successfully.
        assertTrue(true)
    }
}