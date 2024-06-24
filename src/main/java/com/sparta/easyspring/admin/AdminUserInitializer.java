package com.sparta.easyspring.admin;

import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.entity.UserRoleEnum;
import com.sparta.easyspring.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class AdminUserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.findByUsername("Admin").isEmpty()) {
            User adminUser = new User("admin",passwordEncoder.encode("adminPassword"), UserRoleEnum.ADMIN);

            userRepository.save(adminUser);
        }
    }
}
