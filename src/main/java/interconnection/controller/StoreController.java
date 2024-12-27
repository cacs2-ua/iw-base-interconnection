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
 *
 * Escenario: No podemos incluir cabeceras HTTP en un redireccionamiento,
 *           así que en lugar de redirigir directamente, hacemos un proxy:
 *  1) Llamamos desde aquí al TPVV con la cabecera Authorization (GET).
 *  2) Obtenemos el HTML del formulario, pero le cambiamos la acción del <form>
 *     para que el POST se haga también contra un proxy interno de la Tienda.
 *  3) Cuando el usuario hace el POST, la Tienda vuelve a llamar al TPVV
 *     pasando la cabecera Authorization y devolviendo la respuesta final.
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
     * @return checkout.html
     */
    @GetMapping("/checkout")
    public String mostrarCheckout(Model model) {
        // Datos hardcodeados para el ticket y el precio
        String ticket = "TICKET-666";
        double precio = 666.666;

        model.addAttribute("ticket", ticket);
        model.addAttribute("precio", precio);
        return "checkout";
    }

    /**
     * Maneja la acción de finalizar compra (GET) y obtiene via proxy
     * el formulario de pago desde la app TPVV (puerto 8123).
     *
     * El HTML del formulario se inyecta en paymentFormProxy.html
     * para que el usuario lo vea y lo use. Además, se REEMPLAZA la action="/pago/realizar"
     * por action="/tienda/realizarPagoProxy" para que el POST también vaya por proxy.
     */
    @GetMapping("/pagoFormProxy")
    public String pagoFormProxy(Model model) {
        String ticket = "TICKET-666";
        double precio = 666.666;

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
        String url = "http://localhost:8123/pago/form?importe=" + precio + "&idTicket=" + ticket;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 4) Realizar la llamada GET con RestTemplate (proxy) para obtener el HTML del form
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 5) Reemplazar la acción del formulario para el POST
                //    Queremos que en lugar de <form th:action="@{/pago/realizar}">
                //    el HTML devuelto use <form action="/tienda/realizarPagoProxy"> (o similar).
                String originalHtml = response.getBody();
                // Sustituimos la URL original (ej. "/pago/realizar") por "/tienda/realizarPagoProxy"
                // Ajusta si tu form usa otra ruta o thymeleaf. Lo importante es interceptar la acción real.
                String modificadoHtml = originalHtml.replace(
                        "/pago/realizar",
                        "/tienda/realizarPagoProxy"
                );

                // 6) Insertar el HTML resultante en el modelo
                model.addAttribute("paymentFormContent", modificadoHtml);
                return "paymentFormProxy"; // Plantilla que muestra el HTML ya modificado
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
     * El formulario del TPVV (ya modificado) vendrá aquí en lugar de /pago/realizar directamente.
     * Nosotros leemos los datos del formulario (con @ModelAttribute o @RequestBody)
     * y hacemos un POST a http://localhost:8123/pago/realizar adjuntando la cabecera Authorization.
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
        HttpEntity<PagoData> requestEntity =
                new HttpEntity<>(pagoData, headers);

        // 4) Llamamos al /pago/realizar de TPVV
        String urlTPVV = "http://localhost:8123/pago/realizar";
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
