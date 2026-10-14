package com.umc.pediupatrao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.umc.pediupatrao.entity.user.UserRole;
import com.umc.pediupatrao.entity.user.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository repo,
            PasswordEncoder passwordEncoder,
            @Value("${security.user.password}") String password,
            @Value("${security.user.username}") String username
    ) {
        return args -> {

            if (repo.findByUsername(username).isEmpty()) {

                Usuario user = new Usuario();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setRole(UserRole.ADMIN);

                repo.save(user);

                log.info("Usuário admin criado!");
            }
        };
    }
}