package interconnection.controller;

import interconnection.dto.PagoCompletoForm;
import interconnection.service.PagoService;
import interconnection.service.ParametroComercioService;
import interconnection.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import interconnection.dto.PagoCompletoRequest;
import interconnection.dto.PedidoCompletoRequest;
import interconnection.dto.PagoData;
import interconnection.dto.TarjetaPagoData;

import java.net.URLEncoder;  // NUEVO IMPORT
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/tienda")
public class StoreController {

    private static final Logger log = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private ParametroComercioService parametroComercioService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PagoService pagoService;

    @GetMapping("/checkout")
    public String mostrarCheckout(Model model) {
        // Ejemplo con datos "hardcodeados"
        String ticket = "TICKET-888";
        double precio = 888.888;
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

    // ======================================================
    // Muestra el formulario del TPVV embebido (proxy),
    // e inyecta los errores si llegan como parámetro "errors"
    // ======================================================
    @GetMapping("/pagoFormProxy")
    public String pagoFormProxy(@RequestParam("ticket") String ticket,
                                @RequestParam("precio") double precio,
                                @RequestParam("nombreComercio") String nombreComercio,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("hora") String hora,
                                @RequestParam(name="errors", required=false) String errors, // NUEVO
                                Model model) {

        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "Error: API Key no encontrada.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        // NUEVO: Si existen errores, los añadimos al modelo para mostrarlos en la vista
        if (errors != null && !errors.isBlank()) {
            model.addAttribute("errorMessages", errors);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        String url = "http://localhost:8123/tpvv/boardalo/pago/form?importe=" + precio
                + "&idTicket=" + ticket;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String originalHtml = response.getBody();

                // Modificamos el <form> para que apunte a nuestro Proxy
                String modificadoHtml = originalHtml.replace(
                        "<form class=\"card-form\">",
                        "<form class=\"card-form\" method=\"post\" action=\"/tienda/realizarPagoProxy\">"
                );

                // Añadir name="..." en los inputs
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input\" placeholder=\"Nombre Completo\">",
                        "<input type=\"text\" class=\"card-input\" name=\"nombre\" placeholder=\"Nombre Completo\">"
                );
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input\" placeholder=\"0000 0000 0000 0000\">",
                        "<input type=\"text\" class=\"card-input\" name=\"numeroTarjeta\" placeholder=\"0000 0000 0000 0000\">"
                );
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input expiry-date\" placeholder=\"mm/aa\">",
                        "<input type=\"text\" class=\"card-input expiry-date\" name=\"caducidad\" placeholder=\"mm/aa\">"
                );
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input security-code\" placeholder=\"***\">",
                        "<input type=\"text\" class=\"card-input security-code\" name=\"cvc\" placeholder=\"***\">"
                );

                // Añadir campos ocultos para Importe, Ticket, Fecha+Hora
                String hiddenFields = ""
                        + "<input type=\"hidden\" name=\"importe\" value=\"" + precio + "\"/>"
                        + "<input type=\"hidden\" name=\"ticketExt\" value=\"" + ticket + "\"/>"
                        + "<input type=\"hidden\" name=\"fecha\" value=\"" + fecha + " " + hora + "\"/>";

                modificadoHtml = modificadoHtml.replace("</form>", hiddenFields + "</form>");

                // Reemplazar textos fijos en el HTML (importe, comercio, ticket, fecha, hora)
                String precioFormateado = String.format("%.2f", precio);
                modificadoHtml = modificadoHtml
                        .replace("218,42 €", precioFormateado + " €")
                        .replace("Paquetería", nombreComercio)
                        .replace("000000001", ticket)
                        .replace("09/12/2024", fecha)
                        .replace("10:46", hora);

                model.addAttribute("paymentFormContent", modificadoHtml);
                model.addAttribute("fullPage", false);
                return "paymentFormProxy";
            } else {
                model.addAttribute("error", "Error al obtener el formulario de pago del TPVV.");
                return "error/404";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Excepción al llamar al TPVV: " + e.getMessage());
            return "error/404";
        }
    }

    // ======================================================
    // MODIFICADO:
    // Maneja la respuesta del TPVV (200 OK o 400 BAD_REQUEST)
    // Si es 400, redirigimos a pagoFormProxy con los mismos datos + errores
    // ======================================================
    @PostMapping("/realizarPagoProxy")
    public String realizarPagoProxy(@ModelAttribute PagoCompletoForm form, Model model) {

        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "API Key no encontrada.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        // TarjetaPagoData
        TarjetaPagoData tarjetaData = new TarjetaPagoData();
        tarjetaData.setNombre(form.getNombre());
        tarjetaData.setNumeroTarjeta(form.getNumeroTarjeta());
        tarjetaData.setCvc(form.getCvc());
        tarjetaData.setFechaCaducidad(form.getCaducidad());

        // PagoData
        PagoData pagoData = new PagoData();
        pagoData.setImporte(form.getImporte());
        pagoData.setTicketExt(form.getTicketExt());
        pagoData.setFecha(form.getFecha());

        // Construimos la request
        PagoCompletoRequest requestBody = new PagoCompletoRequest(pagoData, tarjetaData);

        // Cabecera con la API Key
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PagoCompletoRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        String urlTPVV = "http://localhost:8123/tpvv/boardalo/pago/realizar";

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(urlTPVV, requestEntity, String.class);

            // ================================
            // MODIFICADO: Manejo del status
            // ================================
            if (response.getStatusCode() == HttpStatus.OK) {
                // Éxito
                String msg = response.getBody();
                model.addAttribute("msgOk", msg);
                return "pagoOk";

            } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                // Hubo errores de validación en el TPVV
                // El body contiene el texto de errores
                String erroresTPVV = response.getBody() != null ? response.getBody() : "Error desconocido";

                // Construimos la redirección al GET /tienda/pagoFormProxy
                // con los parámetros principales (ticket, precio, etc.) y con "errors"
                // OJO: Usamos URLEncoder para el mensaje de error
                String redirectUrl = "redirect:/tienda/pagoFormProxy?ticket="
                        + urlEncode(form.getTicketExt())
                        + "&precio=" + urlEncode(form.getImporte())
                        + "&nombreComercio=" + urlEncode("Tienda Online v2") // O lo que corresponda
                        + "&fecha=" + urlEncode(extraerSoloFecha(form.getFecha()))
                        + "&hora=" + urlEncode(extraerSoloHora(form.getFecha()))
                        + "&errors=" + urlEncode(erroresTPVV);

                return redirectUrl;

            } else if (response.getStatusCode().is3xxRedirection()) {
                model.addAttribute("error", "Redirección 3xx recibida, revise su configuración.");
                return "error/404";

            } else {
                model.addAttribute("error", "Error en respuesta TPVV: " + response.getStatusCode());
                return "error/404";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Excepción POST a TPVV: " + e.getMessage());
            return "error/404";
        }
    }

    @PostMapping("/receivePedido")
    public ResponseEntity<String> receivePedido(@RequestBody PedidoCompletoRequest request) {
        log.debug("Recibido en la tienda un PedidoCompletoRequest: {}", request);

        try {
            pagoService.procesarPedido(request);
            return ResponseEntity.ok("Pedido recibido y guardado con éxito.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error 404");
        }
    }

    // ===========================================================
    // NUEVO: Métodos auxiliares para formatear fecha y encode
    // ===========================================================
    private String urlEncode(String raw) {
        if (raw == null) return "";
        return URLEncoder.encode(raw, StandardCharsets.UTF_8);
    }

    /**
     * Intenta extraer la parte de fecha (ej: "dd/MM/yyyy") de un string "dd/MM/yyyy HH:mm"
     */
    private String extraerSoloFecha(String fechaCompleta) {
        if (fechaCompleta == null) return "";
        String[] partes = fechaCompleta.split(" ");
        return (partes.length > 0) ? partes[0] : "";
    }

    /**
     * Intenta extraer la parte de hora (ej: "HH:mm") de un string "dd/MM/yyyy HH:mm"
     */
    private String extraerSoloHora(String fechaCompleta) {
        if (fechaCompleta == null) return "";
        String[] partes = fechaCompleta.split(" ");
        return (partes.length > 1) ? partes[1] : "";
    }
}
