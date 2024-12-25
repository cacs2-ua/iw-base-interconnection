package interconnection.controller;


import interconnection.service.ParametroComercioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Controller
@RequestMapping("/tienda")
public class StoreController {

    @Autowired
    private ParametroComercioService parametroComercioService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Muestra la página de checkout con el ticket y el precio.
     *
     * @param model El modelo para la vista.
     * @return La vista checkout.html.
     */
    @GetMapping("/checkout")
    public String mostrarCheckout(Model model) {
        // Datos hardcodeados para el ticket y el precio
        String ticket = "TICKET12345";
        double precio = 99.99;

        model.addAttribute("ticket", ticket);
        model.addAttribute("precio", precio);
        return "checkout";
    }

    /**
     * Maneja la acción de finalizar compra y redirige al formulario de pago.
     *
     * @return Redirección al formulario de pago en el servidor TPVV.
     */
    @GetMapping("/finalizarCompra")
    public String finalizarCompra(Model model) {
        String ticket = "TICKET12345";
        double precio = 99.99;

        // Obtener la API Key desde la tabla parametros_comercio
        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");

        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "Error: API Key no encontrada en los parámetros.");
            return "error";
        }

        String apiKey = apiKeyOpt.get();

        // Configurar los headers con la API Key
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        // Configurar los parámetros de la solicitud
        String url = "http://localhost:8123/pago/form?importe=" + precio + "&idTicket=" + ticket;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Realizar la solicitud GET al servidor TPVV
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Retornar el contenido HTML del formulario de pago
                return "redirect:/tienda/pagoForm?importe=" + precio + "&idTicket=" + ticket;
            } else {
                model.addAttribute("error", "Error al acceder al formulario de pago.");
                return "error";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            return "error";
        }
    }
}
