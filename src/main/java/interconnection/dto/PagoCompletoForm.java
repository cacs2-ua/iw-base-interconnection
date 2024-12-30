package interconnection.dto;

import java.util.Date;

/**
 * Form que agrupa los campos del pago + los campos de la tarjeta.
 */
public class PagoCompletoForm {

    // PagoData
    private Double importe;
    private String ticketExt;
    private Date fecha; // en este ejemplo se supone que ya lo guardamos como Date

    // TarjetaPagoData
    private String nombre;
    private String numeroTarjeta;
    private String caducidad; // "mm/aa"
    private String cvc;       // "123"

    public PagoCompletoForm() {}

    // Getters & Setters
    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
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

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
