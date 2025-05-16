package testsAlternativos;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ProdutosTest {

    private static final String BASE_URL = "https://serverest.dev";
    private static String usuarioId;
    private static String authToken;

    @BeforeAll
    public static void prepararDados() {
            baseURI = BASE_URL;
    }

    @AfterAll
    public static void limparDados() {
    }

    @Test
    public void listarProdutos(){
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/produtos")
                .then()
                .statusCode(200)
                .body("produtos._id[0]", notNullValue());
    }
    @Test
    public void listarParametrosDeProdutosIncorretos(){
        given()
                .contentType(ContentType.JSON)
                .queryParam("Preco", 100)
                .queryParam("Quantidade", 3)
                .when()
                .get("/produtos")
                .then()
                .statusCode(400)
                .body("Preco", equalTo("Preco não é permitido"))
                .body("Quantidade", equalTo("Quantidade não é permitido"));
    }

}
