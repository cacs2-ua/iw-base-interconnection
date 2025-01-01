package interconnection.service;

import interconnection.dto.PedidoCompletoRequest;
import interconnection.model.Comercio;
import interconnection.model.Pago;
import interconnection.model.PedidoCompletado;
import interconnection.repository.PagoRepository;
import interconnection.repository.PedidoCompletadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PedidoCompletadoRepository pedidoCompletadoRepository;

    /**
     * Realiza un pago con los detalles proporcionados.
     */
    public Pago realizarPago(Comercio comercio, double importe, String ticketId, String tarjeta) {
        Pago pago = new Pago();
        pago.setComercio(comercio);
        pago.setImporte(importe);
        pago.setTicketExt(ticketId);
        pago.setFecha(new Date());
        pago.setTarjeta(tarjeta);
        return pagoRepository.save(pago);
    }

    /**
     * Obtiene un pago por su ID.
     */
    public Optional<Pago> getPagoById(Long id) {
        return pagoRepository.findById(id);
    }

    /**
     * Procesa el PedidoCompletoRequest que llega desde el TPVV (servidor).
     * Ahora fecha e importe son String en el DTO, así que se parsean aquí
     * antes de persistir en la base de datos.
     */
    public void procesarPedido(PedidoCompletoRequest request) {

        Long pagoId = request.getPagoId();
        Long pedidoId = request.getPedidoId();
        String ticketId = request.getTicketExt();

        // MODIFICADO: parsear fecha (String -> Date)
        Date fechaDate = null;
        try {
            String fechaStr = request.getFecha();
            if (fechaStr != null && !fechaStr.isBlank()) {
                // Ajusta el formato a como se envía desde el servidor (dd/MM/yyyy HH:mm)
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                fechaDate = sdf.parse(fechaStr);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error: Formato de fecha no válido en PedidoCompletoRequest.");
        }

        // MODIFICADO: parsear importe (String -> double)
        double importeDouble;
        try {
            String importeStr = request.getImporte();
            importeDouble = Double.parseDouble(importeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error: Importe no válido en PedidoCompletoRequest.");
        }

        String estadoPago = request.getEstadoPago();
        String comercioNombre = request.getComercioNombre();
        String tarjeta = request.getNumeroTarjeta();

        // Construimos el PedidoCompletado con los tipos nativos para la BD
        PedidoCompletado pedidoBD = new PedidoCompletado(
                ticketId,
                fechaDate,
                importeDouble,
                pagoId,
                pedidoId,
                tarjeta,
                estadoPago,
                comercioNombre,
                tarjeta
        );

        // Guardar en BD
        pedidoCompletadoRepository.save(pedidoBD);
    }
}
