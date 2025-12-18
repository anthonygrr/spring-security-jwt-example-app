package com.rawr.springsecutiryapp.config;


import com.rawr.springsecutiryapp.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //Optional(VALIDACION A NIVLE DE METODO)
public class SecurityConfig {

// Puntero: 1h 2 min
//    SESCURITY FILTER SIN ANOTACIONES(RECOMENDADO)
// El SecurityFilterChain es el primer metodo que debemos implementar
//    Aqui defininmos toda la cadena de filtros personalizada
//el metodo SecurityFilterchain funciona con el patron `builder`
   @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
//                Deshabilitamos lo siguiente csrf(util para Rest sin formularios html)
                .csrf(csrf -> csrf.disable())
//                Logueo basico(usuario y password sin encriptar)
                .httpBasic(Customizer.withDefaults())
//                Trabajar son sesiones sin estado: no vamos a guardar sesion en memoria(la sesion la delegaremos a la duracion del JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                PERMISOS
                .authorizeHttpRequests(http -> {
//                    Configurar los Endpoints publicos
                    http.requestMatchers(HttpMethod.GET, "/auth/hello").permitAll();
//                    Configurar endpoints privados con permisos: hasAuthority(1 permiso), hasAnyAuthority(multiples permisos)
                    http.requestMatchers(HttpMethod.GET, "/auth/hello-secured").hasAuthority("READ");
                    http.requestMatchers(HttpMethod.POST, "/auth/post").hasAnyAuthority("READ", "CREATE");
                    http.requestMatchers(HttpMethod.PATCH, "/auth/patch").hasAuthority("REFACTOR");

//                    VALIDANDO ENDPOINTS PRIVADOS MEDIANTE ROL
                  //  http.requestMatchers(HttpMethod.POST, "/auth/post").hasRole("ADMIN");
                   // http.requestMatchers(HttpMethod.PATCH, "/auth/post").hasAnyRole("ADMIN", "DEVELOPER");

//              Configurar el resto de endpoints =  No especificados
//                    Deny all= denegar lo demas / authenticated = authenticarse
                    http.anyRequest().denyAll();
                })
                .build();
    }


// SPRING SECURITY FILTER CHAIN CON ANOTACIONES
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
//        return httpSecurity
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .build();
//    }

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
//    Implementando UserDetailsService propio
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        EL DAO depende de un password encoder, de momento sera null
        provider.setPasswordEncoder(passwordEncoder());
//       tambien depende de UserDetailsService, de momento sera null
//        provider.setUserDetailsService(userDetailsService());
        provider.setUserDetailsService(userDetailsService);
        return  provider;
    }

// 4to Metodo USerDetailsService que se encarga de Ir A DB a recuperar el usuario
// para datos de prueba creeamos una lista de usuarios de prueba
//    @Bean
//    public UserDetailsService userDetailsService(){
//
//        List<UserDetails> userDetailsList = new ArrayList<>();
//
//        userDetailsList.add(User.withUsername("tony")
//                .password("1234")
//                .roles("ADMIN")
//                .authorities("READ", "WRITE", "CREATE")
//                .build());
//
//        userDetailsList.add(User.withUsername("andrea")
//                .password("12345")
//                .roles("USER")
//                .authorities("READ", "WRITE")
//                .build());
//
//        return new InMemoryUserDetailsManager(userDetailsList);
//    }



//    Quinto metodo: password Encoder
//    Debe ser BCrypt
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    Solo para encriptar de prueba
//    public static void main(String[] args) {
//        System.out.println(new BCryptPasswordEncoder().encode("1234"));
//    }

}

