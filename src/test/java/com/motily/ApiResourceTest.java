package com.motily;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class ApiResourceTest {

    @Test
    public void testGenerateHumans() {
        given()
            .queryParam("count", 5)
            .when()
            .post("/api/human/generate")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("code", equalTo(200))
            .body("data.generatedCount", equalTo(5));
    }

    @Test
    public void testListHumans() {
        given()
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get("/api/human/list")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }

    @Test
    public void testStartSimulation() {
        given()
            .queryParam("years", 5)
            .when()
            .post("/api/simulation/start")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("code", equalTo(200))
            .body("data.simulationId", notNullValue());
    }

    @Test
    public void testGetIndicatorByYear() {
        given()
            .queryParam("year", 2000)
            .when()
            .get("/api/indicator/year")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("code", equalTo(200));
    }

    @Test
    public void testGetIndicatorTrend() {
        given()
            .queryParam("startYear", 2000)
            .queryParam("endYear", 2005)
            .when()
            .get("/api/indicator/trend")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }

    @Test
    public void testListFamilies() {
        given()
            .when()
            .get("/api/family/list")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("code", equalTo(200))
            .body("data", notNullValue());
    }
}
