package com.tfg.ajedrez.auth;

import com.google.api.client.http.*;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de integración con la infraestructura de Firebase Authentication.
 * 
 * Esta clase encapsula la lógica de comunicación con la API REST de Google
 * Identity Toolkit,
 * permitiendo el intercambio de aserciones de identidad (Identity Tokens) de
 * proveedores
 * de terceros por tokens de sesión persistentes de Firebase, consolidando así
 * el sistema
 * de gestión de identidades de la aplicación.
 */
public class FirebaseAuthService {

    /**
     * Localizador de recursos para la configuración del SDK y claves de API del
     * proyecto Firebase.
     */
    private static final String FIREBASE_CONFIG_PATH = "/firebase_config.json";

    /**
     * URL base del endpoint 'signInWithIdp' de la plataforma Google Identity
     * Toolkit.
     */
    private static final String SIGN_IN_WITH_IDP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=";

    /**
     * Motor de transporte HTTP para la ejecución de peticiones SOAP/REST sobre el
     * protocolo TLS.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Fábrica de procesamiento para el modelado y parseo de datos en formato JSON.
     */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Realiza el intercambio de un ID Token de Google por un perfil de usuario de
     * Firebase.
     * 
     * El método construye una petición POST autenticada que incluye la aserción de
     * identidad
     * recibida del proveedor (Google). Tras la validación en los servidores de
     * Firebase,
     * se retorna un objeto con la información de perfil y tokens de seguridad del
     * usuario.
     * 
     * @param googleIdToken Aserción de identidad JWT (JSON Web Token) firmada por
     *                      Google.
     * @return JsonObject Objeto que contiene el perfil del usuario autenticado y
     *         metadatos de sesión.
     * @throws Exception Si ocurre un fallo en la resolución de la clave de API o
     *                   errores
     *                   en la capa de transporte de red.
     */
    public static JsonObject authenticateWithGoogle(String googleIdToken) throws Exception {
        String apiKey = loadApiKey();

        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();

        // Configuración del cuerpo de la petición según la especificación de Identity
        // Toolkit API
        Map<String, Object> data = new HashMap<>();
        data.put("postBody", "id_token=" + googleIdToken + "&providerId=google.com");
        data.put("requestUri", "http://localhost");
        data.put("returnIdpCredential", true);
        data.put("returnSecureToken", true);

        GenericUrl url = new GenericUrl(SIGN_IN_WITH_IDP_URL + apiKey);
        HttpContent content = new JsonHttpContent(JSON_FACTORY, data);

        HttpRequest request = requestFactory.buildPostRequest(url, content);
        HttpResponse response = request.execute();

        try {
            String responseString = response.parseAsString();
            return JsonParser.parseString(responseString).getAsJsonObject();
        } finally {
            response.disconnect();
        }
    }

    /**
     * Carga la API Key de Firebase desde el archivo de configuración local.
     * 
     * @return String Clave de API web.
     * @throws Exception Si el archivo de configuración no existe o es inválido.
     */
    private static String loadApiKey() throws Exception {
        InputStream in = FirebaseAuthService.class.getResourceAsStream(FIREBASE_CONFIG_PATH);
        if (in == null) {
            throw new Exception("Error: El archivo " + FIREBASE_CONFIG_PATH + " no se encuentra en resources.");
        }

        JsonObject config = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
        if (!config.has("apiKey")) {
            throw new Exception("Error: El archivo de configuración no contiene el campo 'apiKey'.");
        }

        return config.get("apiKey").getAsString();
    }
}
