package testsJornadas;

import dto.CarrinhoDTO;
import dto.LoginDTO;
import dto.UsuarioDTO;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static utils.Config.BASE_URL;

@Epic("Jornada de compra do usuário com sucesso")
@Feature("Jornada de compra de sucesso")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UsuarioComprador extends UsuarioCompradorBase {

    @BeforeAll
    @Description("Prepara o ambiente com os dados necessários para realizar o fluxo de jornada de sucesso")
    public static void devePrepararOAmbiente() {
        UsuarioCompradorBase.devePrepararOAmbiente();
    }

    @Test
    @Order(1)
    @Story("Criar novo usuário comum com sucesso")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verificar se a criação do novo usuário comum foi realizada com sucesso")
    public void deveCriarUsuarioComumComSucesso() {
        UsuarioDTO usuarioComum = new UsuarioDTO("Usuario Comum", emailComum, passwordComum, "false");
        usuarioComumID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(usuarioComum)
            .when()
                .post("/usuarios")
            .then()
                .statusCode(HttpStatus.SC_CREATED)
                .time(lessThan(2000L))
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/cadastroSucessoSchema.json"))
                .extract().path("_id");
    }

    @Test
    @Order(2)
    @Story("Realizar login de usuário comum com sucesso")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verificar se o login do usuário comum foi realizado com sucesso")
    public void deveRealizarLoginUsuarioComumComSucesso() {
        LoginDTO loginComum = new LoginDTO(emailComum, passwordComum);
        tokenComum = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(loginComum)
            .when()
                .post("/login")
            .then()
                .statusCode(HttpStatus.SC_OK)
                .time(lessThan(2000L))
                .body("message", equalTo("Login realizado com sucesso"))
                .body("authorization", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/loginUsuarioSucessoSchema.json"))
                .extract().path("authorization");
    }

    @Test
    @Order(3)
    @Story("Lista usuário comum com sucesso")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verificar se a listagem do usuário comum foi realizada com sucesso")
    public void deveListarUsuarioComumComSucesso () {
        ValidatableResponse body = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenComum)
                .queryParam("email", emailComum)
             .when().log().all()
                .get("/usuarios")
             .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("quantidade", equalTo(1))
                .body("usuarios", not(empty()))
                .body("usuarios.email", hasItem(emailComum))
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));
    }

    @Test
    @Order(4)
    @Story("Buscar produto pelo ID com sucesso")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verificar se a listagem do produto pelo ID foi realizada com sucesso")
    public void deveBuscarProdutoPeloIDComSucesso() {
        LoginDTO loginComum = new LoginDTO(emailComum, passwordComum);
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenComum)
                .queryParam("idUsuario", usuarioComumID)
            .when().log().all()
                .get("/produtos/" + produtoID)
            .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .time(lessThan(2000L))
                .body("nome", equalTo(nomeProduto))
                .body("descricao", equalTo(descricaoProduto))
                .body("preco", equalTo(precoProduto))
                .body("quantidade", equalTo(100))
                .body(matchesJsonSchemaInClasspath("schemas/buscarProdutoPorIDSucessoSchema.json"));
    }

    @Test
    @Order(5)
    @Story("Adicionar produto ao carrinho com sucesso")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verificar se o produto foi adicionado ao carrinho com sucesso")
    public void deveAdicionarProdutoAoCarrinhoComSucesso() {
        CarrinhoDTO carrinhoComum = new CarrinhoDTO(produtoID, 5);
        carrinhoID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenComum)
                .queryParam("idUsuario", usuarioComumID)
                .body(carrinhoComum)
            .when().log().all()
                .post("/carrinhos")
            .then().log().all()
                .statusCode(HttpStatus.SC_CREATED)
                .time(lessThan(2000L))
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/cadastroSucessoSchema.json"))
                .extract().path("_id");
    }

    @Test
    @Order(6)
    @Story("Concluir compra com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verificar se a compra foi concluída com sucesso")
    public void deveConcluirCompraComSucesso() {
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenComum)
                .queryParam("idUsuario", usuarioComumID)
            .when().log().all()
                .delete("/carrinhos/concluir-compra")
            .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Order(7)
    @Story("Carrinho não deve existir")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verificar que o carrinho não existe")
    public void deveVerificarQueOCarrinhoNãoExiste () {
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenComum)
                .queryParam("idUsuario", usuarioComumID)
            .when().log().all().when()
                .get("/carrinhos")
            .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("quantidade", equalTo(0))
                .body("carrinhos", empty())
                .time(lessThan(2000L));
    }

    @Test
    @Order(8)
    @Story("Novo usuário deve conseguir realizar login com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifica se o produto comprado saiu do estoque")
    public void deveRetirarProdutoDoEstoque() {
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("nome", nomeProduto)
            .when().log().all()
                .get("/produtos")
            .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("produtos[0].quantidade", equalTo(95))
                .time(lessThan(2000L));
    }

}
