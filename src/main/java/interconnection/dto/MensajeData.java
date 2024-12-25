package interconnection.dto;

import java.util.Date;
import java.util.Objects;

public class MensajeData {

    // Todos los atributos
    private Long id;
    private Date fecha;
    private String contenido;

    // Getters y Setters
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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    // Sobreescribimos equals y hashCode para que dos mensajes sean iguales
    // si tienen el mismo ID (ignoramos el resto de atributos)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MensajeData that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
