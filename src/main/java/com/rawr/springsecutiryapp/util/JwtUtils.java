package com.rawr.springsecutiryapp.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

//    Nota: tanto para crear como para verificar el token necesitamos la llave privada

//    CREAR Y CODIFICAR TOKEN
    public String createToken(Authentication authentication){
    //    Definiendo el algoritmo de encriptacion: SHA256
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

//        Extraer el usuario autenticado
        String username = authentication.getPrincipal().toString();

//        Obtener los authorities(sepearados por comas, usaremos stream)
        //                READ,WRITE,DELETE,UPDATE
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
//                .map(grantedAuthority -> grantedAuthority.getAuthority()) // version 2
//                Collectors.Joinint() toma todos los authorities, los junta y los separa por comas
                .collect(Collectors.joining());

//        Generar TOKEN
        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator) //generador de Token
                .withSubject(username) //usuario para el que se generara el token
                .withClaim("authorities", authorities) //atributos = payload del token
                .withIssuedAt(new Date()) // fecha de creacion del token
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000)) // 1 minuto de expiracion
                .withJWTId(UUID.randomUUID().toString()) // identificador Random al Token
                .withNotBefore(new Date(System.currentTimeMillis())) // a partir de que momento sera valido el token(a partir de este momento)
                .sign(algorithm); //firma: algorithmo de encriptacion con firma

        return jwtToken;
    }


//    Verificar(decodificar) Token
    public DecodedJWT validateToken(String token){
        try {
//            Creando algorithmo
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

//            Verificador del token
            JWTVerifier verifier = JWT.require(algorithm) //debemos saber el algorithmo con el que se encripto
                    .withIssuer(userGenerator) //necesitamos saber quien genero el token(este caso: backend)
                    .build();

//            Devolver el token decodificado si todo salio bien
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
//            devolver la excepcion si no fue exitoso

        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token Invalid, not Authorized");
        }
    }


//    Obtener el usuario al cual se le genero el Token
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

//    Obtener un atributo especifico
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
    return decodedJWT.getHeaderClaim(claimName);
    }

//    Obtener todos los atributos del token
    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }



}
