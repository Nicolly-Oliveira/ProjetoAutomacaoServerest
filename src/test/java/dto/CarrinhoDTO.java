package dto;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoDTO {
    private List<ItemCarrinhoDTO> produtos;

    public CarrinhoDTO(String idProduto, int quantidade) {
        this.produtos = new ArrayList<>();
        this.produtos.add(new ItemCarrinhoDTO(idProduto, quantidade));
    }

    public List<ItemCarrinhoDTO> getProdutos() {
        return produtos;
    }
}

