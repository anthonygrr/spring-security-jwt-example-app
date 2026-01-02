package com.rawr.springsecutiryapp.repository;

import com.rawr.springsecutiryapp.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

//    Buscar roles mediante lista de Strings
//    asi nos aseguramos de que siempre existan roles
    List<RoleEntity> findRoleEntitiesByRoleEnum(List<String> roleNames);
}
