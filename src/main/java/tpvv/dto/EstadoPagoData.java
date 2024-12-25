package tpvv.dto;

import java.util.Objects;

public class EstadoPagoData {

    private Long id;
    private String nombre;
    private String razonEstado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazonEstado() {
        return razonEstado;
    }

    public void setRazonEstado(String razonEstado) {
        this.razonEstado = razonEstado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstadoPagoData that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
