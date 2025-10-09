package model;

import java.math.BigDecimal;

public class Carrinho {
	
    private Integer id;         
    private String idUsuario;    
    private int idProduto;       
    private int quantidade;
    private String produtoNome;
    private BigDecimal preco;    
    private BigDecimal subtotal; 

    public Carrinho(Integer id, String idUsuario, int idProduto, int quantidade,
                    String produtoNome, BigDecimal preco, BigDecimal subtotal) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.produtoNome = produtoNome;
        this.preco = preco;
        this.subtotal = subtotal;
    }

   
    public Integer getId() { 
    	return id; 
    }
    
    public void setId(Integer id) { 
    	this.id = id; 
    }

    public String getIdUsuario() { 
    	return idUsuario; 
    }
    
    public void setIdUsuario(String idUsuario) { 
    	this.idUsuario = idUsuario; 
    }

    public int getIdProduto() { 
    	return idProduto; 
    }
    
    public void setIdProduto(int idProduto) { 
    	this.idProduto = idProduto; 
    }

    public int getQuantidade() { 
    	return quantidade; 
    }
    
    public void setQuantidade(int quantidade) { 
    	this.quantidade = quantidade; 
    }

    public String getProdutoNome() { 
    	return produtoNome; 
    }
    
    public void setProdutoNome(String produtoNome) { 
    	this.produtoNome = produtoNome; 
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
        String nome = (produtoNome == null ? ("Produto #" + idProduto) : produtoNome);
        String sub = (subtotal == null ? "" : (" - R$ " + subtotal));
        return nome + " x" + quantidade + sub;
    }
}
