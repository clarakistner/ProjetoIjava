package model;

import java.math.BigDecimal;

public class ItemCompra {
    private Integer id;
    private Integer idCompra;
    private Integer idProduto;
    private String nomeProduto;    
    private int quantidade;
    private BigDecimal preco;      
    private BigDecimal subtotal;   

    public ItemCompra(Integer id, Integer idCompra, Integer idProduto, String nomeProduto,
                      int quantidade, BigDecimal preco) {
        this.id = id;
        this.idCompra = idCompra;
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.preco = preco;
        this.subtotal = preco.multiply(BigDecimal.valueOf(quantidade));
    }

    public Integer getId() { 
    	return id; 
    }
    
    public void setId(Integer id) { 
    	this.id = id; 
    }

    public Integer getIdCompra() { 
    	return idCompra; 
    }
    
    public void setIdCompra(Integer idCompra) { 
    	this.idCompra = idCompra; 
    }

    public Integer getIdProduto() { 
    	return idProduto; 
    }
    
    public void setIdProduto(Integer idProduto) { 
    	this.idProduto = idProduto; 
    }

    public String getNomeProduto() { 
    	return nomeProduto; 
    }
    
    public void setNomeProduto(String nomeProduto) { 
    	this.nomeProduto = nomeProduto; 
    }

    public int getQuantidade() { 
    	return quantidade; 
    }
    
    public void setQuantidade(int quantidade) {
    	this.quantidade = quantidade; 
    }

    public BigDecimal getPreco() { 
    	return preco; 
    }
    
    public void setPreco(BigDecimal preco) { 
    	this.preco = preco; 
    }

    public BigDecimal getSubtotal() { 
    	return subtotal; 
    }
    
    public void setSubtotal(BigDecimal subtotal) { 
    	this.subtotal = subtotal; 
    }

    @Override
    public String toString() {
        return (nomeProduto == null ? ("Produto #" + idProduto) : nomeProduto) +
                " x" + quantidade + " - R$ " + subtotal;
    }
}
