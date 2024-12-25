package tpvv.dto;

import java.util.Date;
import java.util.Objects;

public class IncidenciaData {

    private Long id;
    private Date fecha;
    private String titulo;
    private String descripcion;
    private int valoracion;
    private String razon_valoracion;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    public String getRazon_valoracion() {
        return razon_valoracion;
    }

    public void setRazon_valoracion(String razon_valoracion) {
        this.razon_valoracion = razon_valoracion;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IncidenciaData that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
