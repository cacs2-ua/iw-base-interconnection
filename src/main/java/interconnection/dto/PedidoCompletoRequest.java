package interconnection.dto;

import java.util.Date;
import java.util.Objects;

public class PedidoCompletoRequest {


    private Long id;
    private Long pagoId;
    private Long pedidoId;
    private String ticketExt;
    private Date fecha;
    private double importe;
    private String estadoPago;
    private String comercioNombre;
    private String numeroTarjeta;

    public PedidoCompletoRequest() {}


    public PedidoCompletoRequest(Long id, Long pagoId, Long pedidoId, String ticketExt, Date fecha, double importe, String estadoPago, String comercioNombre, String numeroTarjeta) {
        this.id = id;
        this.pagoId = pagoId;
        this.pedidoId = pedidoId;
        this.ticketExt = ticketExt;
        this.fecha = fecha;
        this.importe = importe;
        this.estadoPago = estadoPago;
        this.comercioNombre = comercioNombre;
        this.numeroTarjeta = numeroTarjeta;
    }


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

    public Date getFecha() {
        return fecha;
    }

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }


    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
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

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PedidoCompletoRequest that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String toString() {
        return "PedidoCompletoRequest{" +
                "id=" + id +
                ", ticketExt='" + ticketExt + '\'' +
                ", fecha=" + fecha +
                ", importe=" + importe +
                ", estadoPago='" + estadoPago + '\'' +
                ", comercioNombre='" + comercioNombre + '\'' +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
