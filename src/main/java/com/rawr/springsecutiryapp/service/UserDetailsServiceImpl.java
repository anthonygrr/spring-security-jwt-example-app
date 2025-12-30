package com.rawr.springsecutiryapp.service;

import com.rawr.springsecutiryapp.dto.AuthLoginRequest;
import com.rawr.springsecutiryapp.dto.AuthResponse;
import com.rawr.springsecutiryapp.entity.UserEntity;
import com.rawr.springsecutiryapp.repository.UserRepository;
import com.rawr.springsecutiryapp.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

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


    // Metodo authenticate: se encarga de que las credenciales sean correctas
    public Authentication authenticate(String username, String password) {
//        Buscamos el usuario
        UserDetails userDetails = this.loadUserByUsername(username);

//        Validacion cuando el usuario no existe
        if (userDetails == null) {
            throw new BadCredentialsException(String.format("Invalid username or password"));
        }

//        Validar Contrasenia incorrecta
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }

    //    Metodo De Login
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {

//        extraemos credenciales
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

//        Creamos objeto authentication y le establecemos las properties
        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

//        Generar Token para el inicio de sesion del user
        String accessToken = jwtUtils.createToken(authentication);

//        Creamos la respuesta mediante el DTO y devolmemos el DTO
        AuthResponse authResponse = new AuthResponse(username, "User loged succesfully", accessToken, true);
        return authResponse;
    }



}
