package testsJornadas;

import dto.LoginDTO;
import dto.ProdutoDTO;
import dto.UsuarioDTO;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static utils.Config.BASE_URL;

public class UsuarioCompradorBase {

    static String usuarioID;
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

        // Primeiro, verificamos se o usuário comum já existe
        System.out.println("------------------------------------------------");
        System.out.println("VERIFICANDO SE O USUÁRIO COMUM EXISTE");
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

            // Após verificar que o usuário comum existe, realizamos o login
            System.out.println("USUÁRIO COMUM ENCONTRADO - REALIZANDO LOGIN");
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
            System.out.println("LOGIN USUÁRIO COMUM REALIZADO COM SUCESSO");

            // Verificamos se existe um carrinho em progresso para o usuário comum
            System.out.println("------------------------------------------------");
            System.out.println("VERIFICANDO SE EXISTE CARRINHO EM PROGRESSO PARA O USUÁRIO COMUM");
            // Verifica se existe carrinho para o usuário
            ValidatableResponse responseCarrinho = given()
                    .baseUri(BASE_URL)
                    .header("Authorization", tokenTemp)
                    .log().all()
                    .when()
                    .get("/carrinhos")
                    .then()
                    .log().all()
                    .statusCode(200);

            // Se encontrar um carrinho em progresso iremos cancelar a compra, senão iremos prosseguir com os testes
            int quantidadeCarrinho = responseCarrinho.extract().path("quantidade");
            if (quantidadeCarrinho > 0) {
                System.out.println("CARRINHO ENCONTRADO - REALIZANDO CANCELAMENTO");
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

            // Agora que validamos que o carrinho não existe, iremos excluir o usuário comum, pois seguindo a regra de negócio
            // Não podemos excluir um usuário com carrinho em progresso
            System.out.println("------------------------------------------------");
            System.out.println("EXCLUINDO USUÁRIO COMUM");
            // Agora que o carrinho foi verificado/limpo, podemos excluir o usuário
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
        System.out.println("USUÁRIO COMUM EXCLUÍDO COM SUCESSO");

        // Verificamos se já existe um usuário admin cadastrado e se existir, iremos excluir esse usuário
        System.out.println("------------------------------------------------");
        System.out.println("VERIFICA SE JÁ EXISTE UM USUÁRIO ADMIN CADASTRADO");
        // Envia uma requisicao GET para buscar usuarios pelo e-mail especificado
        ValidatableResponse responseAdmin = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .queryParam("email", emailAdmin)
                .when().log().all()
                .get("/usuarios")
                .then().log().all()
                // Valida que a listagem foi realizada com sucesso
                .statusCode(200)
                // Valida que a estrutura do JSON da resposta corresponde ao schema esperado
                .body(matchesJsonSchemaInClasspath("schemas/listarUsuarioSucessoSchema.json"));

        // Extrai o numero de usuarios retornados na resposta
        int quantidadeAdmin = responseAdmin.extract().path("quantidade");

        // Entao, se ja existir um ou mais usuarios com o e-mail fornecido
        if (quantidadeAdmin > 0) {
            // Ira extrair o ID do primeiro usuario retornado
            String id = responseAdmin.extract().path("usuarios[0]._id");

            // Se o ID for valido, realiza a exclusão desse usuario
            if (id != null && !id.isEmpty()) {
                given()
                        .baseUri(BASE_URL)
                        .when().log().all()
                        .delete("/usuarios/" + id)
                        .then().log().all()
                        .statusCode(200);
            }
        }

        System.out.println("------------------------------------------------");
        System.out.println("TESTE PARA CADASTRAR USUARIO ADMIN COM SUCESSO");
        // Cria um objeto DTO com os dados do novo usuario a ser cadastrado
        UsuarioDTO usuarioAdmin = new UsuarioDTO("Usuario Admin", emailAdmin, passwordAdmin, "true");
        // Envia uma requisicao POST para a API /usuarios com os dados do usuario
        usuarioID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(usuarioAdmin)
                .when()
                .post("/usuarios")
                .then()
                .log().all()
                // Valida que o cadastro foi realizado com sucesso
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                // Valida que o campo _id foi retornado e nao e nulo
                .body("_id", notNullValue())
                // Valida que o corpo da resposta segue o schema esperado
                .body(matchesJsonSchemaInClasspath("schemas/cadastroUsuarioSucessoSchema.json"))
                // Extrai o _id do novo usuario cadastrado e armazena para uso em testes futuros
                .extract().path("_id");
        System.out.println("CADASTRO DO USUARIO ADMIN REALIZADO COM SUCESSO");

        System.out.println("------------------------------------------------");
        System.out.println("TESTE PARA REALIZAR LOGIN DO USUARIO ADMIN COM SUCESSO");
        // Cria um objeto DTO com os dados necessarios para login
        LoginDTO loginAdmin = new LoginDTO(emailAdmin, passwordAdmin);
        // Envia uma requisicao POST para a API /usuarios com os dados do login
        tokenAdmin = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(loginAdmin)
                .when()
                .post("/login")
                .then()
                .log().all()
                // Valida que o login foi realizado com sucesso
                .statusCode(200)
                .body("message", equalTo("Login realizado com sucesso"))
                .body("authorization", notNullValue())
                // Extrai o authorization do login e armazena para uso em testes futuros
                .extract().path("authorization");
        System.out.println("LOGIN DO USUARIO ADMIN REALIZADO COM SUCESSO");

        System.out.println("------------------------------------------------");
        System.out.println("VERIFICA SE JÁ EXISTE UM PRODUTO COMUM CADASTRADO");
        // Envia uma requisicao GET para buscar produtos pelo nome especificado
        ValidatableResponse responseProduto = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenAdmin).log().all()
                .queryParam("nome", nomeProduto)
                .when().log().all()
                .get("/produtos")
                .then().log().all()
                // Valida que a listagem foi realizada com sucesso
                .statusCode(200);

        // Extrai o numero de produtos retornados na resposta
        int quantidadeProduto = responseProduto.extract().path("quantidade");

        // Entao, se ja existir um ou mais produtos com o nome fornecido
        if (quantidadeProduto > 0) {
            // Ira extrair o ID do primeiro produto retornado
            String id = responseProduto.extract().path("produtos[0]._id");

            // Se o ID for valido, realiza a exclusão desse produto
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

        System.out.println("------------------------------------------------");
        System.out.println("TESTE PARA CADASTRAR UM PRODUTO COMUM COM SUCESSO");
        // Cria um objeto DTO com os dados do novo produto a ser cadastrado
        ProdutoDTO produtoComum = new ProdutoDTO(nomeProduto, precoProduto, descricaoProduto, 100);
        // Envia uma requisicao POST para a API /produtos com os dados necessarios para cadastrar um produto
        produtoID = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", tokenAdmin).log().all()
                .body(produtoComum)
                .when()
                .log().all()
                .post("/produtos")
                .then().log().all()
                // Valida que o produto foi cadastrado com sucesso
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                // Valida que o campo _id foi retornado e nao e nulo
                .body("_id", notNullValue())
                // Extrai o _id do novo produto cadastrado e armazena para uso em testes futuros
                .extract().path("_id");
        System.out.println("CADASTRO DO PRODUTO REALIZADO COM SUCESSO");
    }

}
