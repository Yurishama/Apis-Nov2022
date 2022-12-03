import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class practica_hotel {

    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void create_booking_200(){
        RestAssured.baseURI = String.format("https://restful-booker.herokuapp.com/booking");

        String body_request = "{\n" +
                "    \"firstname\" : \"Coby\",\n" +
                "    \"lastname\" : \"Goldner\",\n" +
                "    \"totalprice\" : \"320\",\n" +
                "    \"depositpaid\" : \"true\",\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2022-12-03\",\n" +
                "        \"checkout\" : \"2022-12-03\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        Response response=given()
                .log().all()
                .filter(new AllureRestAssured())
                .headers("Content-Type","application/json")
                .headers("Accept","*/*")
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
        assertTrue(body_response.contains("bookingid"));
        assertTrue(body_response.contains("firstname"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }
}
