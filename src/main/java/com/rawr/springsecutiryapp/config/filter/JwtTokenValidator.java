package com.rawr.springsecutiryapp.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.rawr.springsecutiryapp.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

//        Si el token viene lo validamos y agregamos al usuario al context
        if(jwtToken != null){ //bearer asdfasdf
            jwtToken = jwtToken.substring(7); // eliminamos "bearer " del token

//            Validamos el Token
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

//            Extraemos el usuario
            String username = jwtUtils.extractUsername(decodedJWT);

//            Extraemos authorities(permisos)
            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

//            convertir stringAuthorities en lista separada por comas: READ,WRITE,DELETE,UPDATE
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

//            Establecer el usuario con sus datos en el context de spring
//            Extraer el context de spring security
            SecurityContext context = SecurityContextHolder.getContext();

//            Creamos un objeto de tipo authentication que contiene
//            usuario, sin contrasenia(por seguridad) y sus permisos
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

//            Guardamos el authenticacion en el context
            context.setAuthentication(authentication);

//            Guardamos el context en Security
            SecurityContextHolder.setContext(context);

        }
//        Si el token no es valido, rechazamos
        filterChain.doFilter(request, response);
    }
}
