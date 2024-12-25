package tpvv.service;

import tpvv.model.Comercio;
import tpvv.model.Pago;
import tpvv.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    /**
     * Realiza un pago con los detalles proporcionados.
     *
     * @param comercio El comercio que realiza el pago.
     * @param importe  El importe del pago.
     * @param ticketId El ID del ticket asociado al pago.
     * @return El pago realizado.
     */
    public Pago realizarPago(Comercio comercio, double importe, String ticketId, String tarjeta) {
        Pago pago = new Pago();
        pago.setComercio(comercio);
        pago.setImporte(importe);
        pago.setTicketExt(ticketId);
        pago.setFecha(new Date());
        pago.setTarjeta(tarjeta);
        // Aquí puedes añadir lógica adicional, como validar el ticket, procesar el pago, etc.

        return pagoRepository.save(pago);
    }

    /**
     * Obtiene un pago por su ID.
     *
     * @param id El ID del pago.
     * @return El pago si existe, de lo contrario, Optional vacío.
     */
    public Optional<Pago> getPagoById(Long id) {
        return pagoRepository.findById(id);
    }
}
