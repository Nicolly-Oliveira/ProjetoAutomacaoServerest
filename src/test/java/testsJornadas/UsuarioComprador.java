package testsJornadas;

import dto.UsuarioDTO;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static utils.Config.BASE_URL;
@Epic("Jornada de Sucesso")
@Feature("Jornada de Compra Usuário")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioComprador {

    static String usuarioID;

    static String tokenComum;
    static String email = "deincomun@teste.com";

    @BeforeAll
    public static void deveVerificarSeUsuarioJaExiste() {
        ValidatableResponse response = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", email)
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
                .delete("/usuarios/" + usuarioID)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(1)
    @Story("Criar novo usuário com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário pode ser criado com sucesso")
    public void deveCriarUsuarioComSucesso() {
        UsuarioDTO usuarioComun = new UsuarioDTO("Usuario Comum", email, "deinteste", "false");
        usuarioID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(usuarioComun)
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
    @Story("Novo usuário deve ser listado com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário é retornado no endpoint de listar com sucesso")
    public void oUsuarioDeveSerlistado () {
        ValidatableResponse body = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", email)
             .when().log().all()
                .get("/usuarios")
             .then().log().all()
                .statusCode(200)
                .body("quantidade", equalTo(1))
                .body("usuarios", not(empty()))
                .body("usuarios.email", hasItem(email))
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));
    }

    @Test
    @Order(3)
    @Story("Novo usuário deve conseguir realizar login com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário realiza login com sucesso")
    public void deveRealizarLoginUsuario() {
    }

    @Test
    @Order(4)
    @Story("Novo usuário deve conseguir realizar login com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário realiza login com sucesso")
    public void deveBuscarProduto() {

    }

    @Test
    @Order(5)
    @Story("Novo usuário deve conseguir realizar login com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário realiza login com sucesso")
    public void deveCriarCarrinho() {

    }

    @Test
    @Order(6)
    @Story("Novo usuário deve conseguir realizar login com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário realiza login com sucesso")
    public void deveConcluirCompra() {

    }

    @Test
    @Order(7)
    @Story("Novo usuário deve conseguir realizar login com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário realiza login com sucesso")
    public void deveVerificarSeCarrinhoNãoExiste() {

    }

    @Test
    @Order(8)
    public void deveRetirarProdutoDoEstoque() {

    }
}
