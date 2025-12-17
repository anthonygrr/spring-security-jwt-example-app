package com.rawr.springsecutiryapp;

import com.rawr.springsecutiryapp.entity.PermissionEntity;
import com.rawr.springsecutiryapp.entity.RoleEntity;
import com.rawr.springsecutiryapp.entity.RoleEnum;
import com.rawr.springsecutiryapp.entity.UserEntity;
import com.rawr.springsecutiryapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringsecutiryappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringsecutiryappApplication.class, args);
	}

    @Bean
    CommandLineRunner init(UserRepository userRepository){
        return args -> {
//            CREATE PERMISSIONS
            PermissionEntity createPermission = PermissionEntity.builder()
                    .name("CREATE")
                    .build();

            PermissionEntity readPermission = PermissionEntity.builder()
                    .name("READ")
                    .build();

            PermissionEntity refactorPermission = PermissionEntity.builder()
                    .name("REFACTOR")
                    .build();

            PermissionEntity deletePermission = PermissionEntity.builder()
                    .name("DELETE")
                    .build();

//            CREATE ROLES
            RoleEntity roleAdmin = RoleEntity.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionList(Set.of(createPermission, readPermission, deletePermission))
                    .build();

            RoleEntity roleUser = RoleEntity.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionList(Set.of(createPermission, readPermission))
                    .build();

            RoleEntity roleInvited = RoleEntity.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionList(Set.of(readPermission))
                    .build();

            RoleEntity roleDeveloper = RoleEntity.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionList(Set.of(createPermission, readPermission, refactorPermission, deletePermission))
                    .build();

//            CREATE USERS
            UserEntity userTony = UserEntity.builder()
                    .username("tony")
                    .password("1234")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleAdmin))
                    .build();

            UserEntity userSonya = UserEntity.builder()
                    .username("sonya")
                    .password("123456")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleAdmin, roleDeveloper, roleUser))
                    .build();

            UserEntity userAndrea = UserEntity.builder()
                    .username("andy")
                    .password("1234")
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleUser))
                    .build();

//            SAVE INFO
            userRepository.saveAll(List.of(userTony, userSonya, userAndrea));
        };
    }



}
