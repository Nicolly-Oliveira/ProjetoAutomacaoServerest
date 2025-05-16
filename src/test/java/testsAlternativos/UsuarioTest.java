package testsAlternativos;

import dto.LoginDTO;
import dto.UsuarioDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UsuarioTest {

    private static final String BASE_URL = "https://serverest.dev";
    private static String usuarioId;
    private static String authToken;

    @BeforeAll
    public static void prepararDados() {
        baseURI = BASE_URL;
    }

    @AfterAll
    public static void limparDados() {
        if (usuarioId != null) {
            given()
                    .header("Authorization", authToken)
                    .delete("/usuarios/" + usuarioId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    public void cadastrarUsuarioComTipoDadoInvalido() {
        String jsonInvalido = "{"
                + "\"nome\": 123,"
                + "\"email\": \"tipoerrado@teste.com\","
                + "\"password\": \"senha123\","
                + "\"administrador\": \"sim\""
                + "}";

        given()
                .contentType(ContentType.JSON)
                .body(jsonInvalido)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(400)
                .body("nome", equalTo("nome deve ser uma string"))
                .body("administrador", equalTo("administrador deve ser 'true' ou 'false'"));
    }

    @Test
    public void cadastrarUsuarioNomeVazio() {
        UsuarioDTO usuarioInvalido = new UsuarioDTO(
                "",
                "email@invalido.com",
                "senha123",
                "false"
        );

        given()
                .contentType(ContentType.JSON)
                .body(usuarioInvalido)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(400)
                .body("nome", equalTo("nome não pode ficar em branco"));
    }

    @Test
    public void cadastrarUsuarioSemCampoAdministrador() {
        String usuarioJson = "{"
                + "\"nome\": \"Usuário Incompleto\","
                + "\"email\": \"incompleto@teste.com\","
                + "\"password\": \"senha123\""
                + "}";

        given()
                .contentType(ContentType.JSON)
                .body(usuarioJson)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(400)
                .body("administrador", equalTo("administrador é obrigatório"));
    }

    @Test
    public void loginUsuarioNaoCadastrado() {
        LoginDTO loginInexistente = new LoginDTO("nao.cadastrado@teste.com", "123456");

        given()
                .contentType(ContentType.JSON)
                .body(loginInexistente)
                .when()
                .post("/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Email e/ou senha inválidos"));
    }

    @Test
    public void cadastrarUsuarioComEmailExistente() {
        UsuarioDTO usuarioExistente = new UsuarioDTO(
                "Usuário Duplicado",
                "fulano@qa.com",
                "teste",
                "true"
        );

        given()
                .contentType(ContentType.JSON)
                .body(usuarioExistente)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(400)
                .body("message", equalTo("Este email já está sendo usado"));
    }

    @Test
    public void loginComEmailInvalido() {
        LoginDTO loginInvalido = new LoginDTO("naoexiste@teste.com", "senha123");

        given()
                .contentType(ContentType.JSON)
                .body(loginInvalido)
                .when()
                .post("/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Email e/ou senha inválidos"));
    }
    @Test
    public void loginComEmailVazio() {
        LoginDTO loginCampoVazio = new LoginDTO("", "senha123");

        given()
                .contentType(ContentType.JSON)
                .body(loginCampoVazio)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("email", equalTo("email não pode ficar em branco"));
    }

    @Test
    public void loginComEmailNulo() {
        LoginDTO loginInvalido = new LoginDTO(null, "senha123");

        given()
                .contentType(ContentType.JSON)
                .body(loginInvalido)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("email", equalTo("email deve ser uma string"));
    }

    @Test
    public void loginComSenhaIncorreta() {
        LoginDTO loginSenhaErrada = new LoginDTO("fulano@qa.com", "senhaerrada");

        given()
                .contentType(ContentType.JSON)
                .body(loginSenhaErrada)
                .when()
                .post("/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Email e/ou senha inválidos"));
    }
    @Test
    public void loginSemSenha() {
        LoginDTO loginInvalido = new LoginDTO("fulano@qa.com", "");

        given()
                .contentType(ContentType.JSON)
                .body(loginInvalido)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("password", equalTo("password não pode ficar em branco"));
    }
    @Test
    public void loginComSenhaNula() {
        LoginDTO loginInvalido = new LoginDTO("fulano@qa.com", null);

        given()
                .contentType(ContentType.JSON)
                .body(loginInvalido)
                .post("/login")
                .then()
                .statusCode(400)
                .body("password", equalTo("password deve ser uma string"));
    }

}