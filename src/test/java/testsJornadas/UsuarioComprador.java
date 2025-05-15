package testsJornadas;

import dto.CarrinhoDTO;
import dto.ProdutoDTO;
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

@Epic("Jornada de sucesso")
@Feature("Jornada de compra de sucesso")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UsuarioComprador extends UsuarioCompradorBase {

    @BeforeAll
    @Description("Prepara o ambiente com os dados necessários para realizar o fluxo de jornada de sucesso")
    public static void devePrepararOAmbiente() {
        UsuarioCompradorBase.devePrepararOAmbiente();
    }

//    @AfterAll
//    public static void deveLimparABase() {
//        given()
//                .baseUri(BASE_URL)
//                .when()
//                .delete("/usuarios/" + usuarioID)
//                .then()
//                .statusCode(200);
//    }

    @Test
    @Order(1)
    @Story("Criar novo usuário comum com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verificar se a criação do novo usuário comum foi realizada com sucesso")
    public void deveCriarUsuarioComumComSucesso() {
        UsuarioDTO usuarioComum = new UsuarioDTO("Usuario Comum", emailComum, passwordComum, "false");
        usuarioID = given()
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
                .body(matchesJsonSchemaInClasspath("schemas/cadastroUsuarioSucessoSchema.json"))
                .extract().path("_id");
    }

    @Test
    @Order(2)
    @Story("Realizar login de usuário comum com sucesso")
    @Severity(SeverityLevel.CRITICAL)
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
                .extract().path("authorization");
    }

//    @Test
      // Definimos uma ordem, pois precisamos seguir uma sequencia logica desde que é uma jornada de compra
//    @Order(3)
//    @Story("Listar novo usuário comum com sucesso")
    // Definimos a severidade do teste como critica, indicando que falhas aqui sao graves
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("Verificar se a listagem do novo usuário comum foi realizada com sucesso")
//    public void deveListarUsuarioComumComSucesso () {
//        ValidatableResponse body = given()
//                .baseUri(BASE_URL)
//                .contentType(ContentType.JSON)
//                .queryParam("email", email)
//             .when().log().all()
//                .get("/usuarios")
//             .then().log().all()
//                .statusCode(200)
//                .body("quantidade", equalTo(1))
//                .body("usuarios", not(empty()))
//                .body("usuarios.email", hasItem(email))
//                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));
//    }

    @Test
    @Order(4)
    @Story("Buscar produto pelo ID com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verificar se a listagem do produto pelo ID foi realizada com sucesso")
    public void deveBuscarProdutoPeloIDComSucesso() {
        LoginDTO loginComum = new LoginDTO(emailComum, passwordComum);
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
            .when()
                .get("/produtos/" + produtoID)
            .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .time(lessThan(2000L))
                .body("nome", equalTo(nomeProduto))
                .body("descricao", equalTo(descricaoProduto))
                .body("preco", equalTo(precoProduto)) // Atenção: use float aqui para comparação
                .body("quantidade", equalTo(100));
    }

    @Test
    @Order(5)
    @Story("Adicionar produto ao carrinho com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verificar se o produto foi adicionado ao carrinho com sucesso")
    public void deveAdicionarProdutoAoCarrinhoComSucesso() {
        CarrinhoDTO carrinhoComum = new CarrinhoDTO(produtoID, 5);
        carrinhoID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenComum)
                .body(carrinhoComum)
            .when()
                .post("/carrinhos")
            .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED)
                .time(lessThan(2000L))
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .extract().path("_id");
    }

    @Test
    @Order(6)
    @Story("Concluir compra com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verificar se a compra foi concluída com sucesso")
    public void deveConcluirCompraComSucesso() {
        //Listamos os carrinhos associados ao token do usuário comum
        ValidatableResponse responseCarrinho = given()
                .baseUri(BASE_URL)
                .header("Authorization", tokenComum)
                .log().all()
            .when()
                .get("/carrinhos")
            .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);

        // Extraímos a quantidade
        int quantidadeCarrinho = responseCarrinho.extract().path("quantidade");

        // Se a quantidade de carrinho for maior que 0, concluímos a compra e realizamos o delete do carrinho
        if (quantidadeCarrinho > 0) {
            System.out.println("Concluindo compra");
            String idCarrinho = responseCarrinho.extract().path("carrinhos[0]._id");
            if (idCarrinho != null && !idCarrinho.isEmpty()) {
                given()
                        .baseUri(BASE_URL)
                        .header("Authorization", tokenComum)
                        .log().all()
                    .when()
                        .delete("/carrinhos/concluir-compra")
                    .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .body("message", equalTo("Registro excluído com sucesso"));
            }
        }


//        given()
//                .baseUri(BASE_URL)
//                .header("Authorization", tokenComum)
//                .log().all()
//            .when()
//                .delete("/carrinhos/concluir-compra")
//            .then()
//                .log().all()
//                .statusCode(HttpStatus.SC_OK)
//                .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Order(7)
    @Story("Carrinho não deve existir")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verificar que o carrinho não existe")
    public void deveVerificarQueOCarrinhoNãoExiste() {
        given()
                .baseUri(BASE_URL)
                .header("Authorization", tokenComum)
                .log().all()
            .when()
                .get("/carrinhos")
            .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("quantidade", equalTo(0));
                //.body("carrinhos", empty());
    }

//    @Test
      // Definimos uma ordem, pois precisamos seguir uma sequencia logica desde que é uma jornada de compra
//    @Order(8)
    //@Story("Novo usuário deve conseguir realizar login com sucesso")
    // Definimos a severidade do teste como critica, indicando que falhas aqui sao graves
//    @Severity(SeverityLevel.CRITICAL)
//    @Description("Verifica se um novo usuário realiza login com sucesso")
//    public void deveRetirarProdutoDoEstoque() {
//
//    }

}