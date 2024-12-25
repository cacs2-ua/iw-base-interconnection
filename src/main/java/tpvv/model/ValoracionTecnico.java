package tpvv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "valoraciones_tecnico")
public class ValoracionTecnico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private double valoracion;


    @OneToOne
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    public ValoracionTecnico() {}

    public ValoracionTecnico(double valoracion) {
        this.valoracion = valoracion;
        this.tecnico = new Usuario("default");
    }

    public  ValoracionTecnico(double valoracion, Usuario tecnico) {
        this.valoracion = valoracion;
        this.tecnico = tecnico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValoracion() {
        return valoracion;
    }

    public void setValoracion(double valoracion) {
        this.valoracion = valoracion;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        if (this.tecnico == tecnico || tecnico == null) {
            return; // No hacer nada si es el mismo técnico
        }

        // Desvincular el técnico anterior si existe
        if (this.tecnico != null) {
            this.tecnico.setValoracionTecnico(null);
        }

        // Asignar el nuevo técnico
        this.tecnico = tecnico;

        // Vincular la relación inversa
        if (tecnico.getValoracionTecnico() != this) {
            tecnico.setValoracionTecnico(this);
        }
    }

}


