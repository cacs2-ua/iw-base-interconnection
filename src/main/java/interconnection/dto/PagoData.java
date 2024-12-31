package interconnection.dto;

import java.util.Objects;

public class PagoData {
    private Long id;
    private String ticketExt;

    // MODIFICADO: Antes era Date, ahora String
    private String fecha;

    // MODIFICADO: Antes era double, ahora String
    private String importe;

    private String tarjeta;
    private String estadoPago;
    private String comercioNombre;
    private String tarjetaPagoNumero;

    // Constructor vacío
    public PagoData() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketExt() {
        return ticketExt;
    }
    public void setTicketExt(String ticketExt) {
        this.ticketExt = ticketExt;
    }

    // MODIFICADO: Getter/Setter fecha como String
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    // MODIFICADO: Getter/Setter importe como String
    public String getImporte() {
        return importe;
    }
    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getTarjeta() {
        return tarjeta;
    }
    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getEstadoPago() {
        return estadoPago;
    }
    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getComercioNombre() {
        return comercioNombre;
    }
    public void setComercioNombre(String comercioNombre) {
        this.comercioNombre = comercioNombre;
    }

    public String getTarjetaPagoNumero() {
        return tarjetaPagoNumero;
    }
    public void setTarjetaPagoNumero(String tarjetaPagoNumero) {
        this.tarjetaPagoNumero = tarjetaPagoNumero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PagoData that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "PagoData{" +
                "id=" + id +
                ", ticketExt='" + ticketExt + '\'' +
                ", fecha='" + fecha + '\'' +
                ", importe='" + importe + '\'' +
                ", tarjeta='" + tarjeta + '\'' +
                ", estadoPago='" + estadoPago + '\'' +
                ", comercioNombre='" + comercioNombre + '\'' +
                ", tarjetaPagoNumero='" + tarjetaPagoNumero + '\'' +
                '}';
    }
}
