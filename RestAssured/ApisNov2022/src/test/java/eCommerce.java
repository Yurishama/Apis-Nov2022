import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Name;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class eCommerce {
    static private String url_base = "webapi.segundamano.mx";
    static private String email = "ventas_234@mailinator.com";
    static private String password = "12345";
    static private String account_id = "";
    static private String access_token = "";
    static private String uuid = "";
    static private String ad_id;

    @Name("Obtener token")
    private String obtener_Token(){

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response del token: " + body_response);

        JsonPath jsonResponse = response.jsonPath();

        access_token = jsonResponse.get("access_token");
        //pm.environment.set("token",pm.response.json().access_token)
        System.out.println("Token:"+ access_token);

        account_id = jsonResponse.get("account.account_id");
        System.out.println("Account ID: "+ account_id);

        uuid = jsonResponse.get("account.uuid");
        System.out.println("UUID: "+ uuid);

        return access_token;
    }

    @Test
    @Order(1)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Validar el filtrado de categorias")
    public void obtener_Categorias_filtrado_200(){
        RestAssured.baseURI = String.format("https://%s/urls/v1/public",url_base);

        String body_request = "{\n" +
                "\t\"filters\": [{\n" +
                "\t\t\"price\": \"-60000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"60000-80000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"80000-100000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"100000-150000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"150000-\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}]\n" +
                "}";

        Response response=given()
                .log().all()
                .filter(new AllureRestAssured())
                .queryParam("lang","es")
                .contentType("application/json")
                .headers("Accept","application/json, text/plain, */*")
                .body(body_request)
                .post("/ad-listing");

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("urls"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.TRIVIAL)
    @DisplayName("Test case: Listado de Anuncios")
    public void listado_anuncios_200(){
        RestAssured.baseURI = String.format("https://%s/highlights/v1",url_base);

        Response response=given()
                .log().all()
                .filter(new AllureRestAssured())
                .queryParam("prio",1)
                .queryParam("cat","2020")
                .queryParam("lim",1)
                .headers("Accept","*/*")
                .get("/public/highlights");


        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("list_id"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 2500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Crear Usuario")
    public void crear_Usuario_401(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String new_user = "agente_ventas_test" + (Math.floor(Math.random()*6789)) + "@mailinator.com";
        String new_password = "12345";
        String body_request = "{\"account\":{\"email\":\""+new_user+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(new_user,new_password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(401,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ACCOUNT_VERIFICATION_REQUIRED"));
        assertTrue(body_response.contains("error"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }


    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case: Obtener información del usuario")
    public void obtener_info_usuario(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("token"));
        assertTrue(body_response.contains("uuid"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 2500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        //Asignar valores a nuestras variables globales

        JsonPath jsonResponse =response.jsonPath();

        access_token = jsonResponse.get("access_token");
        //pm.environment.set("token",pm.response.json().access_token)
        System.out.println("Token:"+ access_token);

        account_id = jsonResponse.get("account.account_id");
        //pm.environment.set("account_id",pm.response.json().account.account_id)
        System.out.println("Account ID: "+ account_id);

        uuid = jsonResponse.get("account.uuid");
        //pm.environment.set('uuid',pm.response.json().account.uuid)
        System.out.println("UUID: "+ uuid);

    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case: Crear un Anuncio")
    public void crear_un_anuncio(){


        String token = obtener_Token();
        System.out.println(("Token en la prueba Crear anuncio " + token));

        String body_request = "{\n" +
                "    \"images\": \"2265766694.jpg\",\n" +
                "    \"category\": \"4100\",\n" +
                "    \"subject\": \"Unbranded estampillas para colección\",\n" +
                "    \"body\": \"Unbranded Compra y venta e intercambio de estampillas para colección seria\",\n" +
                "    \"is_new\": \"0\",\n" +
                "    \"region\": \"12\",\n" +
                "    \"municipality\": \"316\",\n" +
                "    \"area\": \"69520\",\n" +
                "    \"price\": \"804\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"267-941-5539\"\n" +
                "}";

        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up",url_base,uuid);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("x-source", "PHOENIX_DESKTOP")
                .header("Accept","*/*")
                .header("Content-type","application/json")
                .auth().preemptive().basic(uuid,token)
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("subject"));
        assertTrue(body_response.contains("category"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 3500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        //pm.environment.set("ad_id",pm.response.json().data.ad.ad_id)
        JsonPath jsonResponse = response.jsonPath();
        ad_id = jsonResponse.get("data.ad.ad_id");


    }

}
