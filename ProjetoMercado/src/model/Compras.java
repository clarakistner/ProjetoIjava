package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Compras {
    private Integer id;
    private String idUsuario;
    private BigDecimal totalPago;
    private Timestamp dataCompra;
    private List<ItemCompra> itens = new ArrayList<>();

    public Compras() {}

    public Compras(Integer id, String idUsuario, BigDecimal totalPago, Timestamp dataCompra) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.totalPago = totalPago;
        this.dataCompra = dataCompra;
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

    public BigDecimal getTotalPago() { 
    	return totalPago; 
    }
    public void setTotalPago(BigDecimal totalPago) { 
    	this.totalPago = totalPago; 
    }

    public Timestamp getDataCompra() { 
    	return dataCompra; 
    }
    
    public void setDataCompra(Timestamp dataCompra) { 
    	this.dataCompra = dataCompra; 
    }

    public List<ItemCompra> getItens() { 
    	return itens; 
    }
    
    public void setItens(List<ItemCompra> itens) { 
    	this.itens = itens; 
    }

    @Override
    public String toString() {
        return "Compra #" + id + " - Total: R$ " + totalPago + " - Data: " + dataCompra;
    }
}
