package dto;

public class ItemCarrinhoDTO { // Adicionado 'public' aqui
    private String idProduto;
    private int quantidade;

    public ItemCarrinhoDTO(String idProduto, int quantidade) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
