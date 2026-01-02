package com.rawr.springsecutiryapp.service;

import com.rawr.springsecutiryapp.dto.AuthCreateUserRequest;
import com.rawr.springsecutiryapp.dto.AuthLoginRequest;
import com.rawr.springsecutiryapp.dto.AuthResponse;
import com.rawr.springsecutiryapp.entity.RoleEntity;
import com.rawr.springsecutiryapp.entity.UserEntity;
import com.rawr.springsecutiryapp.repository.RoleRepository;
import com.rawr.springsecutiryapp.repository.UserRepository;
import com.rawr.springsecutiryapp.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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

//    Metodo para crear y guardar el User en DB
    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest){
//        Obteniendo datos del usuario
        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        List<String> roleRequest = authCreateUserRequest.roleRequest().roleListName();

//        Buscar roles
        Set<RoleEntity> roleEntitySet = roleRepository
                .findRoleEntitiesByRoleEnumIn(roleRequest)
                .stream()
                .collect(Collectors.toSet());


//        Validar roles
        if (roleEntitySet.isEmpty()){
            throw new IllegalArgumentException("The specified roles doesn exist.");
        }

//        Creando Usuario y guardando informacion
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntitySet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
//        Guardando usuario en DB
        UserEntity userCreated = userRepository.save(userEntity);

//        Crear Lista de Authorities
        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

//        Guardar los roles en el usuario recuperado
        userCreated.getRoles().forEach(
                role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name())))
        );

//        Guardar los permisos en el usuario recuperado
        userCreated.getRoles()
                .stream()
                .flatMap(
                        role -> role
                                .getPermissionList()
                                .stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

//        Creando el conexto de Spring
        SecurityContext context =  SecurityContextHolder.getContext();
//        Estableciendo los datos de authenticacion del usuario
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

//        Creando el token para el usuario
        String accessToken = jwtUtils.createToken(authentication);

//        Retornando informacion
        AuthResponse authResponse = new AuthResponse(
                userCreated.getUsername(),
                "User created successfully",
                accessToken,
                true
        );

        return authResponse;
    }


}
