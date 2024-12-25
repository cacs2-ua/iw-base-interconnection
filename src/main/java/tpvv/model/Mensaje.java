package tpvv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "mensajes")
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @NotNull
    private String contenido;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "incidencia_id", nullable = false)
    private Incidencia incidencia;

    public Mensaje() {}

    public Mensaje(String contenido) {
        this.contenido = contenido;

        // Inicializar la fecha usando LocalDate y convertirla a java.util.Date
        this.fecha = Date.from(LocalDate.of(2000, 12, 12).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Usuario usuario = new Usuario("email");
        this.setUsuario(usuario);
        this.incidencia = new Incidencia("default");
    }

    public Mensaje(String contenido, Usuario usuario) {
        this.contenido = contenido;
        this.setUsuario(usuario);

        // Inicializar la fecha usando LocalDate y convertirla a java.util.Date
        this.fecha = Date.from(LocalDate.of(2000, 12, 12).atStartOfDay(ZoneId.systemDefault()).toInstant());

        this.incidencia = new Incidencia("default");
    }

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (this.usuario == usuario || usuario == null) {
            return;
        }
        if (this.usuario != null) {
            this.usuario.getMensajes().remove(this);
        }
        this.usuario = usuario;
        if (!usuario.getMensajes().contains(this)) {
            usuario.getMensajes().add(this);
        }
    }

    public Incidencia getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(Incidencia incidencia) {
        if (this.incidencia == incidencia || incidencia == null) {
            return;
        }
        if (this.incidencia != null) {
            this.incidencia.getMensajes().remove(this);
        }
        this.incidencia = incidencia;
        if (!incidencia.getMensajes().contains(this)) {
            incidencia.addMensaje(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(id, mensaje.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
