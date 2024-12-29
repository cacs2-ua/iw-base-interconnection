package interconnection.controller;

import interconnection.dto.PagoData;
import interconnection.service.ParametroComercioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Controlador de la Tienda que actúa como 'Cliente' ante el TPVV.
 */
@Controller
@RequestMapping("/tienda")
public class StoreController {

    @Autowired
    private ParametroComercioService parametroComercioService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Muestra la página de checkout con el ticket y el precio (datos "hardcodeados" de ejemplo).
     *
     * @param model El modelo para la vista.
     * @return checkout.html
     */
    @GetMapping("/checkout")
    public String mostrarCheckout(Model model) {
        // Datos hardcodeados para el ejemplo
        String ticket = "TICKET-777";
        double precio = 777.777;
        String nombreComercio = "Tienda Online v2";
        String fecha = "09/09/2029";
        String hora = "13:45";

        model.addAttribute("ticket", ticket);
        model.addAttribute("precio", precio);
        model.addAttribute("nombreComercio", nombreComercio);
        model.addAttribute("fecha", fecha);
        model.addAttribute("hora", hora);

        return "checkout";
    }

    /**
     * Maneja la acción de finalizar compra (GET) y obtiene via proxy
     * el formulario de pago desde la app TPVV (puerto 8123).
     */
    @GetMapping("/pagoFormProxy")
    public String pagoFormProxy(@RequestParam("ticket") String ticket,
                                @RequestParam("precio") double precio,
                                @RequestParam("nombreComercio") String nombreComercio,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("hora") String hora,
                                Model model) {

        // 1) Recuperar la API Key desde la base de datos
        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "Error: API Key no encontrada en los parámetros.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        // 2) Configurar la cabecera "Authorization" con la apiKey
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        // 3) Construir la URL al TPVV
        //    Observa que enviamos importe y idTicket para que el TPVV forme "pagoData"
        String url = "http://localhost:8123/tpvv/boardalo/pago/form?importe=" + precio
                + "&idTicket=" + ticket;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 4) Realizar la llamada GET con RestTemplate (proxy) para obtener el HTML del form
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // *** HTML original que viene del TPVV ***
                String originalHtml = response.getBody();

                // 5) Reemplazar la acción del formulario para el POST
                //    (de /tpvv/boardalo/pago/realizar a /tienda/realizarPagoProxy)
                String modificadoHtml = originalHtml.replace(
                        "/tpvv/boardalo/pago/realizar",
                        "/tienda/realizarPagoProxy"
                );

                // 6) Reemplazar los valores de ejemplo por los reales recibidos del checkout
                //    Ojo: Asegúrate de que coincidan EXACTAMENTE con lo que aparece en paymentForm.html
                //    - 218,42 €
                //    - Paquetería
                //    - 000000001
                //    - 09/12/2024
                //    - 10:46

                // Si quieres formatear "precio" a 2 decimales:
                String precioFormateado = String.format("%.2f", precio);

                modificadoHtml = modificadoHtml
                        .replace("218,42 €", precioFormateado + " €")
                        .replace("Paquetería", nombreComercio)
                        .replace("000000001", ticket)
                        .replace("09/12/2024", fecha)
                        .replace("10:46", hora);

                // 7) Insertar el HTML resultante en el modelo
                model.addAttribute("paymentFormContent", modificadoHtml);
                model.addAttribute("fullPage", false);

                return "paymentFormProxy"; // Plantilla que muestra el HTML inyectado
            } else {
                model.addAttribute("error", "Error al acceder al formulario de pago en TPVV.");
                return "error/404";
            }
        } catch (Exception e) {
            model.addAttribute("error",
                    "Error al procesar la solicitud contra TPVV: " + e.getMessage());
            return "error/404";
        }
    }

    /**
     * Reverse Proxy para manejar el POST de realizar pago.
     * Redirige la petición final al TPVV incluyendo la cabecera Authorization.
     */
    @PostMapping("/realizarPagoProxy")
    public String realizarPagoProxy(@ModelAttribute("pagoData") PagoData pagoData,
                                    Model model) {
        // 1) Recuperar la API Key
        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "API Key no encontrada en los parámetros.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        // 2) Creamos la cabecera con Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3) Enviamos en el body un JSON con los datos de pago
        HttpEntity<PagoData> requestEntity = new HttpEntity<>(pagoData, headers);

        // 4) Llamamos al /pago/realizar de TPVV
        String urlTPVV = "http://localhost:8123/tpvv/boardalo/pago/realizar";
        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(urlTPVV, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Ej. "Pago procesado correctamente."
                String mensajeTPVV = response.getBody();
                model.addAttribute("msgOk", mensajeTPVV);
                return "pagoOk";  // Muestra una vista de éxito con "msgOk"
            } else {
                model.addAttribute("error", "Error en la respuesta del TPVV: "
                        + response.getStatusCode());
                return "error/404";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Excepción en la llamada POST al TPVV: " + e.getMessage());
            return "error/404";
        }
    }
}
