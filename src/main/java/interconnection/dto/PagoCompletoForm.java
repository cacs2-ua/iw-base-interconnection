package interconnection.dto;

/**
 * Form que agrupa los campos del pago + los campos de la tarjeta.
 */
public class PagoCompletoForm {

    // MODIFICADO: ahora son String en lugar de Double / Date
    private String importe;   // era Double
    private String ticketExt;
    private String fecha;     // era Date

    // TarjetaPagoData
    private String nombre;
    private String numeroTarjeta;
    private String caducidad; // "mm/aa"
    private String cvc;       // "123"

    public PagoCompletoForm() {}

    // Getters & Setters

    // MODIFICADO: Getter/Setter de importe como String
    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    // MODIFICADO: Getter/Setter de fecha como String
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
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

    public String getTicketExt() {
        return ticketExt;
    }

    public void setTicketExt(String ticketExt) {
        this.ticketExt = ticketExt;
    }
}
