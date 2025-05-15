package testsJornadas;

import dto.LoginDTO;
import dto.ProdutoDTO;
import dto.UsuarioDTO;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static utils.Config.BASE_URL;

public class UsuarioCompradorBase {

    static String usuarioAdminID;
    static String usuarioComumID;
    static String produtoID;
    static String carrinhoID;
    static String tokenComum;
    static String tokenAdmin;

    private static UsuarioDTO usuarioComum;
    static String emailComum = "deincomum@mail.com";
    static String passwordComum = "password";

    private static UsuarioDTO usuarioAdmin;
    static String emailAdmin = "deinadmin@mail.com";
    static String passwordAdmin = "password";

    private static ProdutoDTO produtoComum;
    static String nomeProduto = "Whey Growth Cappuccino 1kg";
    static int precoProduto = 100;
    static String descricaoProduto = "Whey Protein Concentrado (1kg)";

    public static void devePrepararOAmbiente() {

        System.out.println("VERIFICA SE O USUARIO COMUM EXISTE E SE EXISITR, REALIZA LOGIN");
        ValidatableResponse responseUsuario = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", emailComum)
                .log().all()
                .when()
                .get("/usuarios")
                .then()
                .log().all()
                .statusCode(200);

        int quantidadeUsuarios = responseUsuario.extract().path("quantidade");

        if (quantidadeUsuarios > 0) {
            String idUsuario = responseUsuario.extract().path("usuarios[0]._id");

            LoginDTO loginComum = new LoginDTO(emailComum, passwordComum);
            String tokenTemp = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(loginComum)
                    .log().all()
                    .when()
                    .post("/login")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .body("message", equalTo("Login realizado com sucesso"))
                    .body("authorization", notNullValue())
                    .extract().path("authorization");

            System.out.println("VERIFICA SE ELE POSSUI CARRINHOS, SE POSSUIR IRÁ EXCLUIR O CARRINHO");
            ValidatableResponse responseCarrinho = given()
                    .baseUri(BASE_URL)
                    .header("Authorization", tokenTemp)
                    .log().all()
                    .when()
                    .get("/carrinhos")
                    .then()
                    .log().all()
                    .statusCode(200);

            int quantidadeCarrinho = responseCarrinho.extract().path("quantidade");
            if (quantidadeCarrinho > 0) {
                String idCarrinho = responseCarrinho.extract().path("carrinhos[0]._id");
                if (idCarrinho != null && !idCarrinho.isEmpty()) {
                    given()
                            .baseUri(BASE_URL)
                            .header("Authorization", tokenTemp)
                            .log().all()
                            .when()
                            .delete("/carrinhos/cancelar-compra")
                            .then()
                            .log().all()
                            .statusCode(200);
                }
            } else {
                System.out.println("NENHUM CARRINHO ENCONTRADO");
            }

            System.out.println("EXCLUINDO O USUARIO COMUM APÓS DELETAR O CARRINHO");
            if (idUsuario != null && !idUsuario.isEmpty()) {
                given()
                        .baseUri(BASE_URL)
                        .log().all()
                        .when()
                        .delete("/usuarios/" + idUsuario)
                        .then()
                        .log().all()
                        .statusCode(200);
            }
        }

        System.out.println("VERIFICA SE O USUARIO ADMIN EXISTE, E SE EXISITR SERÁ EXCLUÍDO");
        ValidatableResponse responseAdmin = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", emailAdmin)
                .when().log().all()
                .get("/usuarios")
                .then().log().all()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));

        int quantidadeAdmin = responseAdmin.extract().path("quantidade");

        if (quantidadeAdmin > 0) {
            String id = responseAdmin.extract().path("usuarios[0]._id");

            if (id != null && !id.isEmpty()) {
                given()
                        .baseUri(BASE_URL)
                        .when().log().all()
                        .delete("/usuarios/" + id)
                        .then().log().all()
                        .statusCode(200);
            }
        }

        System.out.println("REALIZA O CADASTRO DO USUÁRIO ADMIN");
        UsuarioDTO usuarioAdmin = new UsuarioDTO("Usuario Admin", emailAdmin, passwordAdmin, "true");
        usuarioAdminID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(usuarioAdmin)
                .when()
                .post("/usuarios")
                .then()
                .log().all()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/cadastroUsuarioSucessoSchema.json"))
                .extract().path("_id");

        System.out.println("REALIZA O LOGIN DO USUÁRIO ADMIN");
        LoginDTO loginAdmin = new LoginDTO(emailAdmin, passwordAdmin);
        tokenAdmin = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(loginAdmin)
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(200)
                .body("message", equalTo("Login realizado com sucesso"))
                .body("authorization", notNullValue())
                .extract().path("authorization");

        System.out.println("VERIFICA SE O PRODUTO EXISTE E SE EXISTIR REALIZA A EXCLUSÃO");
        ValidatableResponse responseProduto = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenAdmin).log().all()
                .queryParam("nome", nomeProduto)
                .when().log().all()
                .get("/produtos")
                .then().log().all()
                .statusCode(200);

        int quantidadeProduto = responseProduto.extract().path("quantidade");

        if (quantidadeProduto > 0) {
            String id = responseProduto.extract().path("produtos[0]._id");

            if (id != null && !id.isEmpty()) {
                given()
                        .baseUri(BASE_URL)
                        .header("Authorization", tokenAdmin).log().all()
                        .when()
                        .delete("/produtos/" + id)
                        .then()
                        .statusCode(200);
            }
        }

        System.out.println("CADASTRA NOVO PROUTO");
        ProdutoDTO produtoComum = new ProdutoDTO(nomeProduto, precoProduto, descricaoProduto, 100);
        produtoID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenAdmin).log().all()
                .body(produtoComum)
                .when()
                .log().all()
                .post("/produtos")
                .then().log().all()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .extract().path("_id");
    }

}
