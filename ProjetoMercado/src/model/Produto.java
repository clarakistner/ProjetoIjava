package model;

public class Produto {

    private String nome, descricao;
    private int id, qtd;
    private float preco;

   public Produto(String nome, String descricao, int qtd, float preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.qtd = qtd;
        this.preco = preco;
    }

    public Produto(String nome, String descricao, int id, int qtd, float preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.id = id;
        this.qtd = qtd;
        this.preco = preco;
    }

    public String getNome() { 
    	return nome; 
    }
    
    public void setNome(String nome) { 
    	this.nome = nome; 
    }

    public String getDescricao() { 
    	return descricao; 
    }
    
    public void setDescricao(String descricao) { 
    	this.descricao = descricao; 
    }

    public int getId() { 
    	return id; 
    }
    
    public void setId(int id) { 
    	this.id = id; 
    }

    public int getQtd() { 
    	return qtd;
    }
    
    public void setQtd(int qtd) { 
    	this.qtd = qtd; 
    }

    public float getPreco() { 
    	return preco; 
    }
    
    public void setPreco(float preco) { 
    	this.preco = preco; 
    }

    @Override
    public String toString() {
        java.text.NumberFormat moeda = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt","BR"));
        String precoFmt = moeda.format(this.preco); 
        String desc = (this.descricao == null || this.descricao.isBlank()) ? "-" : this.descricao.trim();

        return String.format("ID: %d | %s | Qtd: %d | Preço: %s | Desc.: %s",
                this.id, this.nome, this.qtd, precoFmt, desc);
    }
}
