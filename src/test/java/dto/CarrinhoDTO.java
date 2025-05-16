package dto;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoDTO {

    /**
     * Lista que armazena todos os itens do carrinho
     * Cada item é representado por um ItemCarrinhoDTO
     */
    private List<ItemCarrinhoDTO> produtos;

    /**
     * Construtor que inicializa um novo carrinho com um único produto.
     * @param idProduto identificador único do produto
     * @param quantidade número de unidades do produto
     */
    public CarrinhoDTO(String idProduto, int quantidade) {
        this.produtos = new ArrayList<>();
        this.produtos.add(new ItemCarrinhoDTO(idProduto, quantidade));
    }

    /**
     * Retorna a lista de todos os produtos no carrinho.
     * @return Lista contendo todos os itens do carrinho
     */
    public List<ItemCarrinhoDTO> getProdutos() {
        return produtos;
    }

}

