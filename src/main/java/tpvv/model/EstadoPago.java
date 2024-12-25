package tpvv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "estados_pago")
public class EstadoPago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    private String razonEstado;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Pago> pagos = new HashSet<>();

    public EstadoPago() {}

    public EstadoPago(String nombre) {
        this.nombre = nombre;
    }

    public EstadoPago(String nombre, String razonEstado) {
        this.nombre = nombre;
        this.razonEstado = razonEstado;
    }

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

    public Set<Pago> getPagos() {
        return pagos;
    }

    public void addPago(Pago pago) {
        if (pagos.contains(pago)) return;
        pagos.add(pago);
        if (pago.getEstado() != this) {
            pago.setEstado(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EstadoPago that = (EstadoPago) o;

        // Si ambos objetos tienen un ID no nulo, comparamos por ID
        if (this.id != null && that.id != null) {
            return Objects.equals(this.id, that.id);
        }

        // Si no se pueden comparar por ID, consideramos que son diferentes
        return false;
    }

    @Override
    public int hashCode() {
        // Generamos el hashCode basado únicamente en el ID
        return Objects.hash(id);
    }

}
