import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class eCommerce {
    static private String url_base = "webapi.segundamano.mx";


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


    }
}
