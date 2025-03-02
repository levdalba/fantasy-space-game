package com.motycka.edu.game.config

import com.motycka.edu.game.account.AccountService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration(private val userService: AccountService?) {

    init {
        // Validate that userService is not null
        requireNotNull(userService) { "AccountService must not be null" }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.GET, "/login.html").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/accounts").permitAll()
                auth.anyRequest().authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .logout { logout ->
                logout.permitAll()
            }

        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService = UserDetailsService { username ->
        val user = userService?.getByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        User.builder()
            .username(user.username)
            .password(user.password) // Password is now plain text
            .roles("USER")
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        @Suppress("DEPRECATION")
        return NoOpPasswordEncoder.getInstance() // Use plain text passwords
    }
}