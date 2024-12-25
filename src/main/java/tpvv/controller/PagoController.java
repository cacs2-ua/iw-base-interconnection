package tpvv.controller;

import tpvv.dto.PagoData;
import tpvv.model.Comercio;
import tpvv.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private PagoService pagoService;


    @ModelAttribute("comercio")
    public Comercio inicializarComercio() {
        // Aquí recuperas el comercio de la base de datos por su CIF, o lo creas por defecto
        return new Comercio("B12345678");
    }

    /**
     * Muestra el formulario de pago.
     *
     * @param importe  El importe del pago (parámetro GET).
     * @param ticketId El ID del ticket (parámetro GET).
     * @param model    El modelo para la vista.
     * @return La vista del formulario de pago.
     */
    @GetMapping("/form")
    public String mostrarFormularioPago(@RequestParam("importe") double importe,
                                        @RequestParam("idTicket") String ticketId,
                                        Model model) {
        PagoData pagoData = new PagoData();
        pagoData.setImporte(importe);
        pagoData.setTicketExt(ticketId);
        model.addAttribute("pagoData", pagoData); // Nota: Asegúrate de usar "pagoData" como th:object
        return "paymentForm"; // Esto devuelve la vista paymentForm.html
    }

    /**
     * Procesa el pago realizado.
     *
     * @param pagoData El formulario de pago enviado como DTO.
     * @return La respuesta de confirmación del pago en formato JSON.
     */
    @PostMapping("/realizar")
    @ResponseBody // Esto asegura que la respuesta se devuelva en formato JSON
    public ResponseEntity<String> realizarPago(@Valid @RequestBody PagoData pagoData) {

        // Validación simple: verificar si los campos necesarios están presentes
        if (pagoData.getImporte() <= 0 || pagoData.getTicketExt() == null || pagoData.getTarjeta() == null) {
            return ResponseEntity.badRequest().body("Error: Faltan datos requeridos (importe, ticketExt, tarjeta).");
        }

        // Si todo está bien, responder con un "ok" en JSON
        return ResponseEntity.ok("Pago procesado correctamente.");
    }
}
