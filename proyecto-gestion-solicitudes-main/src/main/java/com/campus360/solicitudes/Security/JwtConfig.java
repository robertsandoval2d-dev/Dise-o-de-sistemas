package com.campus360.solicitudes.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public PublicKey publicKey() throws Exception {

        // ClassPathResource resource = new ClassPathResource("keys/public_key.pem");
        // String key = new String(Files.readAllBytes(resource.getFile().toPath()));

        // key = key
        //         .replace("-----BEGIN PUBLIC KEY-----", "")
        //         .replace("-----END PUBLIC KEY-----", "")
        //         .replaceAll("\\s", ""); 

        // byte[] decoded = Base64.getDecoder().decode(key);

        // X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        // KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // return keyFactory.generatePublic(spec);

        try {
            // Generamos una llave real en memoria al instante
            // Esto evita leer archivos y errores de Base64
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            return keyPairGen.generateKeyPair().getPublic();
        } catch (Exception e) {
            System.err.println("Error generando llave de emergencia: " + e.getMessage());
            return null;
        }
    }
}