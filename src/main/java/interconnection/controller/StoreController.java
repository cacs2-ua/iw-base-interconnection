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

/**
 * Controlador de la Tienda que actúa como 'Cliente' ante el TPVV.
 *
 * Escenario: No podemos incluir cabeceras HTTP en un redireccionamiento,
 *           así que en lugar de redirigir directamente, hacemos un proxy:
 *  1) Llamamos desde aquí al TPVV con la cabecera Authorization.
 *  2) Obtenemos el HTML del formulario.
 *  3) Servimos (inyectamos) ese HTML al cliente para que lo vea en la misma sesión.
 */
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
        return "checkout";  // Plantilla Thymeleaf
    }

    /**
     * Maneja la acción de finalizar compra y obtiene via proxy
     * el formulario de pago desde la app TPVV (puerto 8123).
     *
     * El HTML del formulario se inyecta en paymentFormProxy.html
     * para que el usuario lo vea y lo use.
     */
    @GetMapping("/finalizarCompra")
    public String finalizarCompra(Model model) {
        String ticket = "TICKET12345";
        double precio = 99.99;

        // 1) Recuperar la API Key (Authorization) desde la tabla parametros_comercio
        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "Error: API Key no encontrada en los parámetros.");
            return "error";  // Plantilla que muestra un mensaje de error
        }

        String apiKey = apiKeyOpt.get();

        // 2) Configurar la cabecera "Authorization" con la apiKey
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        // 3) Construir la URL al TPVV
        String url = "http://localhost:8123/pago/form?importe=" + precio + "&idTicket=" + ticket;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 4) Realizar la llamada GET con RestTemplate (proxy)
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 5) Insertar el HTML del formulario en el modelo
                model.addAttribute("paymentFormContent", response.getBody());
                return "paymentFormProxy"; // Plantilla que inyecta el HTML
            } else {
                model.addAttribute("error", "Error al acceder al formulario de pago en TPVV.");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error",
                    "Error al procesar la solicitud contra TPVV: " + e.getMessage());
            return "error";
        }
    }
}
