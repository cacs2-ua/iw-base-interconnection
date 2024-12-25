// model/Usuario.java

package interconnection.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String nombre;

    @NotNull
    private String contrasenya;

    private boolean activo = true;

    // Relación Many-to-One con Comercio
    @NotNull
    @ManyToOne
    @JoinColumn(name = "comercio_id", nullable = false)
    private Comercio comercio;

    @OneToMany(mappedBy = "usuario_comercio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Incidencia> incidencias_comercio = new HashSet<>();

    @OneToMany(mappedBy = "usuario_tecnico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Incidencia> incidencias_tecnico = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Mensaje> mensajes = new HashSet<>();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoUsuario tipo;

    @OneToOne(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true)
    private ValoracionTecnico valoracionTecnico;

    public Usuario() {}

    public Usuario(String email) {
        this.email = email;
        this.nombre = "default";
        this.contrasenya = "default";
        Comercio comercio = new Comercio("default");
        this.setComercio(comercio);
        this.tipo = new TipoUsuario("default");
    }

    public  Usuario (String email, String nombre, String contrasenya, Comercio comercio) {
        this.email = email;
        this.nombre = nombre;
        this.contrasenya = contrasenya;
        this.setComercio(comercio);
        this.tipo = new TipoUsuario("default");
    }

    // Getters y Setters básicos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    // Getter y Setter de la Relación Many-to-One

    public Comercio getComercio() {
        return comercio;
    }


    public void setComercio(Comercio comercio) {
        // Si el nuevo comercio es el mismo que el actual, no hace nada
        if (this.comercio == comercio || comercio == null) {
            return;
        }

        // Si ya tiene un comercio, lo desvincula de la lista de usuarios de ese comercio
        if (this.comercio != null) {
            this.comercio.getUsuarios().remove(this);
        }

        // Asigna el nuevo comercio
        this.comercio = comercio;

        // Si el comercio no es nulo, lo añade a la lista de usuarios de ese comercio
        if (!comercio.getUsuarios().contains(this)) {
            comercio.addUsuario(this);
        }
    }


    public Set<Incidencia> getIncidencias_comercio() {
        return incidencias_comercio;
    }

    public void addIncidencia_comercio(Incidencia incidencia) {
        if (incidencias_comercio.contains(incidencia)) return;
        incidencias_comercio.add(incidencia);
        if (incidencia.getUsuario_comercio() != this) {
            incidencia.setUsuario_comercio(this);
        }
    }

    public Set<Incidencia> getIncidencias_tecnico() {
        return incidencias_tecnico;
    }

    public void addIncidencia_tecnico(Incidencia incidencia) {
        if (incidencias_tecnico.contains(incidencia)) return;
        incidencias_tecnico.add(incidencia);
        if (incidencia.getUsuario_tecnico() != this) {
            incidencia.setUsuario_tecnico(this);
        }
    }

    public Set<Mensaje> getMensajes() {
        return mensajes;
    }

    public void addMensaje(Mensaje mensaje) {
        if (mensajes.contains(mensaje)) return;
        mensajes.add(mensaje);
        if (mensaje.getUsuario() != this) {
            mensaje.setUsuario(this);
        }
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        // Si el nuevo tipo es el mismo que el actual, no hace nada
        if (this.tipo == tipo || tipo == null) {
            return;
        }

        // Si ya tiene un tipo, lo desvincula de la lista de usuarios de ese tipo
        if (this.tipo != null) {
            this.tipo.getUsuarios().remove(this);
        }

        // Asigna el nuevo tipo
        this.tipo = tipo;

        // Si el tipo no es nulo, lo añade a la lista de usuarios de ese tipo
        if (!tipo.getUsuarios().contains(this)) {
            tipo.addUsuario(this);
        }
    }

    public ValoracionTecnico getValoracionTecnico() {
        return valoracionTecnico;
    }

    public void setValoracionTecnico(ValoracionTecnico valoracionTecnico) {
        if (this.valoracionTecnico != null) {
            this.valoracionTecnico.setTecnico(null);
        }
        this.valoracionTecnico = valoracionTecnico;
        if (valoracionTecnico != null && valoracionTecnico.getTecnico() != this) {
            valoracionTecnico.setTecnico(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;
        if (id != null && usuario.id != null)
            return Objects.equals(id, usuario.id);
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
