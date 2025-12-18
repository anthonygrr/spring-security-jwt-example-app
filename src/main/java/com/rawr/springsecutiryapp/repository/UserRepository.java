package com.rawr.springsecutiryapp.repository;

import com.rawr.springsecutiryapp.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

//    Opcion 1 QueryMethod
    Optional<UserEntity> findUserEntityByUsername(String username);
    
//    Opcion 2: Query
//    @Query("SELECT u FROM UserEntity u WHERE u.username = 1")
//    Optional<UserEntity> findUser();
}
