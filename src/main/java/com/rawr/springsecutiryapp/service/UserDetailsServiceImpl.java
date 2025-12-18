package com.rawr.springsecutiryapp.service;

import com.rawr.springsecutiryapp.entity.UserEntity;
import com.rawr.springsecutiryapp.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//      1  Buscar usuario en DB
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("El usuario: " + username + " no existe"));


//       2 Spring security maneja los permisos con GranthedAuthorities
//        Debemos crear una lista de esos permisos
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

//        Obtenemos los roles que posee el usuario y los agregamos a la lista de SimpleGranthedAuthorities
//        Es el objeto que entiende Spring Security
//        Ponemos "ROLE_" porque sporign trabaja con el prefijo,
//        si los definimos con ROLE en la DB desde el principio no es necesario
        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

//        Tomando los permisos de los roles y tambien los agregamos
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

//Retornando el Usuario con sus roles y permisos
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }


}
