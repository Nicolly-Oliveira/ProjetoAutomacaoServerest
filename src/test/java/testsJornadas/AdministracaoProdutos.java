package testsJornadas;

import dto.UsuarioDTO;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static utils.Config.BASE_URL;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdministracaoProdutos {

    private static UsuarioDTO usuario;

    static String usuarioAdminID;

    static String tokenAdmin;
    static String emailAdmin = "deinadmin@teste.com";

    @BeforeAll
    public static void deveVerificarSeUsuarioAdminJaExiste() {
        ValidatableResponse response = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", emailAdmin)
             .when().log().all()
                .get("/usuarios")
             .then().log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));

        int quantidade = response.extract().path("quantidade");

        if (quantidade > 0) {
            String id = response.extract().path("usuarios[0]._id");

            if (id != null && !id.isEmpty()) {
                given()
                        .baseUri(BASE_URL)
                     .when()
                        .delete("/usuarios/" + id)
                     .then()
                        .statusCode(200);
            }
        }
    }

    @AfterAll
    public static void deveLimparABase() {
        given()
                .baseUri(BASE_URL)
                .when()
                .delete("/usuarios/" + usuarioAdminID)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(1)
    public void deveCriarUsuarioAdminComSucesso() {
        UsuarioDTO usuarioAdmin = new UsuarioDTO("Usuario Admin", emailAdmin, "deinteste", "false");
        usuarioAdminID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(usuarioAdmin)
        .when()
                .post("/usuarios")
        .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/cadastroUsuarioSucessoSchema.json"))
                .extract().path("_id");
    }

    @Test
    @Order(2)
    public void oUsuarioDeveSerlistado () {
        ValidatableResponse body = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", emailAdmin)
             .when()
                .get("/usuarios")
             .then()
                .statusCode(200)
                .body("quantidade", equalTo("1"))
                .body("usuarios", not(empty()))
                .body("usuarios.email", hasItem(emailAdmin))
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));
    }

    @Order(3)
    @Test
    public void deveRealizarLoginUsuario() {
    }

    @Test
    @Order(4)
    public void deveCriarProduto() {

    }

    @Test
    @Order(5)
    public void oProdutoDeveSerlistado() {

    }

    @Test
    @Order(6)
    public void deveSerEditadoOProduto() {

    }

    @Test
    @Order(7)
    public void deveBuscarOProdutoAposAlteracao() {

    }

    @Test
    @Order(8)
    public void deveApagarProduto() {

    }
}
