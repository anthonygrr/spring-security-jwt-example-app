package com.rawr.springsecutiryapp.config;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //Optional
public class SecurityConfig {


// El SecurityFilterChain es el primer metodo que debemos implementar
//    Aqui defininmos toda la cadena de filtros personalizada
//el metodo SecurityFilterchain funciona con el patron `builder`
   @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity.build();
    }

// El segundo metodo es el AuthenticationManager(depende de AuthenticationConfdiguration)
//    Se encarga de autenticar al usuario
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    El tercer metodo es el Autenteication provider(podemos reemplazarlo por OAUTH, Keycloak)
//    Para este caso usamos DAo que es el mas basico(loguear por medio de  Ususario contrasenia en DB)
//     OCUPA dos componentes: PasswordEncoder y UserDetailsService
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        EL DAO depende de un password encoder, de momento sera null
        provider.setPasswordEncoder(passwordEncoder());
//       tambien depende de UserDetailsService, de momento sera null
        provider.setUserDetailsService(userDetailsService());
        return  provider;
    }

// 4to Metodo USerDetailsService que se encarga de Ir A DB a recuperar el usuario
// para datos de prueba creeamos una lista de usuarios de prueba
    @Bean
    public UserDetailsService userDetailsService(){

        List<UserDetails> userDetailsList = new ArrayList<>();

        userDetailsList.add(User.withUsername("tony")
                .password("1234")
                .roles("ADMIN")
                .authorities("READ", "WRITE", "CREATE")
                .build());

        userDetailsList.add(User.withUsername("andrea")
                .password("12345")
                .roles("USER")
                .authorities("READ")
                .build());

        return new InMemoryUserDetailsManager(userDetailsList);
    }

//    Quinto metodo: apssword Encoder
//    Debe ser BCrypt
    @Bean
    public PasswordEncoder passwordEncoder(){
//        SOlo para pruebas desactivamos el password encoder
//        Deprecated
        return NoOpPasswordEncoder.getInstance();
    }

}

