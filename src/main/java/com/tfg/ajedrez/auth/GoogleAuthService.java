package com.tfg.ajedrez.auth;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Implementación del servicio de autenticación de terceros mediante el protocolo OAuth 2.0 de Google.
 * 
 * Esta clase gestiona el flujo de autorización de tipo 'Authorization Code' diseñado para 
 * aplicaciones nativas (Installed Apps). Utiliza un receptor de servidor local (Loopback) 
 * para la captura segura del código de autorización, cumpliendo con las especificaciones del 
 * estándar RFC 8252 (OAuth 2.0 for Native Apps).
 */
public class GoogleAuthService {

    /**
     * Motor de transporte HTTP para la ejecución de peticiones seguras sobre el protocolo TLS.
     */
    private static HttpTransport httpTransport;
    
    /**
     * Fábrica para el procesamiento y modelado de datos en formato JSON (JavaScript Object Notation).
     */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    
    /**
     * Lista de ámbitos de autorización (Scopes) definidos bajo la especificación OpenID Connect 
     * para la recuperación de la identidad y metadatos básicos del usuario.
     */
    private static final List<String> SCOPES = Arrays.asList(
            "openid",
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
    );
    
    /**
     * Localizador de recursos para el archivo de configuración de credenciales de cliente.
     */
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";

    /**
     * Ejecuta el proceso de autenticación federada y recuperación del Token de Identidad (ID Token).
     * 
     * El flujo comprende la inicialización del objeto de autorización, la instanciación de un 
     * servidor HTTP local para la captura de callbacks y la delegación de la validación del 
     * usuario al agente de usuario (navegador) del sistema operativo.
     * 
     * @return String Representación en formato JWS del ID Token proporcionado por el emisor (Google).
     * @throws Exception Si ocurre una interrupción en la comunicación de red o inconsistencias en la 
     *                   carga de metadatos del cliente.
     */
    public static String authenticate() throws Exception {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleAuthService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new Exception("Error: El archivo " + CREDENTIALS_FILE_PATH + " no se encuentra en el classpath de recursos.");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        
        try {
            // 1. Obtención de la URL de autorización
            String redirectUri = receiver.getRedirectUri();
            String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();

            // 2. Apertura del navegador del sistema (Acceso estático recomendado)
            AuthorizationCodeInstalledApp.browse(authorizationUrl);

            // 3. Captura del código de autorización mediante el receptor local
            String code = receiver.waitForCode();

            // 4. Intercambio del código por el Token de Google
            GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            
            return response.getIdToken();
        } finally {
            receiver.stop();
        }
    }
}
