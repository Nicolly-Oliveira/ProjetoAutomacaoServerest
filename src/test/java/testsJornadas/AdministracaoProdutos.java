package testsJornadas;

import dto.LoginDTO;
import dto.ProdutoDTO;
import dto.UsuarioDTO;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static utils.Config.BASE_URL;
@Epic("Jornada de Usuário Administrador")
@Feature("Jornada de Gerenciamento de Produtos")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdministracaoProdutos {

    static Random random = new Random();

    static UsuarioDTO usuarioAdmin;
    static String usuarioAdminID;
    static String tokenAdmin;
    static String emailAdmin = "deinadmin@teste.com";
    static String password = "deinteste";

    static ProdutoDTO produtoDTO;
    static String produtoNome = "Controle Xbox Series X " + random.nextInt(1000);
    static String produtoID;

    static ProdutoDTO produtoAtualizado = new ProdutoDTO("Controle DualSense PS5",300, "Controle DualSense", 100);

    @BeforeAll
    public static void deveVerificarSeUsuarioAdminJaExiste() {
        ValidatableResponse responseUsuario = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", emailAdmin)
             .when().log().all()
                .get("/usuarios")
             .then().log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));

        int quantidade = responseUsuario.extract().path("quantidade");

        if (quantidade > 0) {
            String id = responseUsuario.extract().path("usuarios[0]._id");

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
                .header("authorization", tokenAdmin)
            .when()
                .log().all()
                .delete("/produtos/" + produtoID)
            .then()
                .statusCode(200);

        given()
                .baseUri(BASE_URL)
            .when()
                .delete("/usuarios/" + usuarioAdminID)
            .then()
                .statusCode(200);
    }

    @Test
    @Order(1)
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário admin é criado com sucesso")
    public void deveCriarUsuarioAdminComSucesso() {
        usuarioAdmin = new UsuarioDTO("Usuario Admin", emailAdmin, password, "true");
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
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário admin é listado com sucesso")
    public void oUsuarioDeveSerlistado () {
        ValidatableResponse body = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", emailAdmin)
            .when()
                .get("/usuarios")
            .then()
                .statusCode(200)
                .body("quantidade", equalTo(1))
                .body("usuarios", not(empty()))
                .body("usuarios.email", hasItem(emailAdmin))
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));
    }

    @Test
    @Order(3)
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário admin realiza login com sucesso")
    public void deveRealizarLoginUsuario() {
        LoginDTO loginDTO = new LoginDTO(usuarioAdmin.getEmail(), usuarioAdmin.getPassword());
        tokenAdmin = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(loginDTO)
            .when()
                .post("/login")
            .then()
                .statusCode(200)
                .body("message", equalTo("Login realizado com sucesso"))
                .body("authorization", notNullValue())
                .extract().path("authorization");
    }

    @Test
    @Order(4)
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo usuário admin cria produto com sucesso")
    public void deveCriarProduto() {
        produtoDTO = new ProdutoDTO(produtoNome, 333, "Controle Xbox Series X", 117);
        produtoID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(produtoDTO)
                .header("authorization", tokenAdmin)
            .when()
                .post("/produtos")
            .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .extract().path("_id");
    }

    @Test
    @Order(5)
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um novo produto é listado com sucesso")
    public void oProdutoDeveSerlistado() {
        ValidatableResponse body = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("nome", produtoDTO.getNome())
            .when()
                .get("/produtos")
            .then()
                .statusCode(200)
                .body("quantidade", equalTo(1))
                .body("produtos[0].nome", equalTo(produtoDTO.getNome()))
                .body("produtos[0].preco", equalTo(produtoDTO.getPreco()))
                .body("produtos[0].descricao", equalTo(produtoDTO.getDescricao()))
                .body("produtos[0].quantidade", equalTo(produtoDTO.getQuantidade()))
                .body("produtos[0]._id", equalTo(produtoID));
    }

    @Test
    @Order(6)
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um produto é atualizado com sucesso")
    public void deveSerEditadoOProduto() {
        ValidatableResponse body = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("authorization", tokenAdmin)
                .body(produtoAtualizado)
            .when()
                .put("/produtos/" + produtoID)
            .then()
                .statusCode(200)
                .body("message", equalTo("Registro alterado com sucesso"));
    }

    @Test
    @Order(7)
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um produto é listado com sucesso")
    public void deveBuscarOProdutoAposAlteracao() {
        ValidatableResponse body = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("nome", produtoAtualizado.getNome())
            .when()
                .get("/produtos")
            .then()
                .statusCode(200)
                .body("quantidade", equalTo(1))
                .body("produtos[0].nome", equalTo(produtoAtualizado.getNome()))
                .body("produtos[0].preco", equalTo(produtoAtualizado.getPreco()))
                .body("produtos[0].descricao", equalTo(produtoAtualizado.getDescricao()))
                .body("produtos[0].quantidade", equalTo(produtoAtualizado.getQuantidade()))
                .body("produtos[0]._id", equalTo(produtoID));
    }

    @Test
    @Order(8)
    @Story("Administração de produtos")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se um produto é deletado com sucesso")
    public void deveApagarProdutoENaoListar() {
        given()
                .baseUri(BASE_URL)
                .header("authorization", tokenAdmin)
            .when()
                .log().all()
                .delete("/produtos/" + produtoID)
            .then()
                .statusCode(200)
                .body("message", equalTo("Registro excluído com sucesso"));

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("nome", produtoDTO.getNome())
            .when()
                .get("/produtos")
            .then()
                .statusCode(200)
                .body("quantidade", equalTo(0))
                .body("produtos[]", empty());
    }
}
