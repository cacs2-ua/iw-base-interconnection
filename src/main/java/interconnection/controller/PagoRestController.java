package interconnection.controller;

import interconnection.dto.PagoData;
import interconnection.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller para manejar las operaciones de pago.
 */
@RestController
@RequestMapping("/pago")
public class PagoRestController {

    @Autowired
    private PagoService pagoService;

    /**
     * Procesa el pago realizado.
     *
     * @param pagoData El formulario de pago enviado como DTO.
     * @return La respuesta de confirmación del pago en formato JSON.
     */
    @PostMapping("/realizar")
    public ResponseEntity<String> realizarPago(@Valid @RequestBody PagoData pagoData) {

        // Validación simple: verificar si los campos necesarios están presentes
        if (pagoData.getImporte() == null || pagoData.getTicketExt() == null || pagoData.getTarjeta() == null) {
            return ResponseEntity.badRequest().body("Error: Faltan datos requeridos (importe, ticketExt, tarjeta).");
        }

        // Intentar convertir el importe de String a double
        double importe;
        try {
            importe = Double.parseDouble(pagoData.getImporte());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Error: Importe no válido.");
        }

        if (importe <= 0) {
            return ResponseEntity.badRequest().body("Error: El importe debe ser mayor que cero.");
        }

        // Si todo está bien, responder con un "ok" en JSON
        return ResponseEntity.ok("Pago procesado correctamente.");
    }
}
