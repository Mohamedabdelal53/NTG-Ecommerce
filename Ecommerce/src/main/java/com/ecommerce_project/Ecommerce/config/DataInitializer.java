package com.ecommerce_project.Ecommerce.config;

import com.ecommerce_project.Ecommerce.entities.Role;
import com.ecommerce_project.Ecommerce.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepo roleRepository;

    @Autowired
    public DataInitializer(RoleRepo roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
            Optional<Role> admin = roleRepository.findByName("ROLE_ADMIN");
            if(admin.isEmpty()){
                Role adminu = new Role();
                adminu.setRoleId(101L);
                adminu.setName("ROLE_ADMIN");
                roleRepository.save(adminu);
            }
            Optional<Role> user = roleRepository.findByName("ROLE_USER");
            if(user.isEmpty()){
                Role useru = new Role();
                useru.setRoleId(102L);
                useru.setName("ROLE_USER");
                roleRepository.save(useru);
            }
    }
}
