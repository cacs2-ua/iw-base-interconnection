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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    /**
     * Descarga el fragmento HTML del formulario de pago desde el TPVV y lo inyecta
     * con los datos reales (ticket, importe, etc.).
     */
    @GetMapping("/pagoFormProxy")
    public String pagoFormProxy(@RequestParam("ticket") String ticket,
                                @RequestParam("precio") double precio,
                                @RequestParam("nombreComercio") String nombreComercio,
                                @RequestParam("fecha") String fecha,
                                @RequestParam("hora") String hora,
                                Model model) {

        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "Error: API Key no encontrada.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        // Llamada GET al TPVV
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
                // 1) Nombre
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input\" placeholder=\"Nombre Completo\">",
                        "<input type=\"text\" class=\"card-input\" name=\"nombre\" placeholder=\"Nombre Completo\">"
                );

                // 2) Nº Tarjeta
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input\" placeholder=\"0000 0000 0000 0000\">",
                        "<input type=\"text\" class=\"card-input\" name=\"numeroTarjeta\" placeholder=\"0000 0000 0000 0000\">"
                );

                // 3) Caducidad
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input expiry-date\" placeholder=\"mm/aa\">",
                        "<input type=\"text\" class=\"card-input expiry-date\" name=\"caducidad\" placeholder=\"mm/aa\">"
                );

                // 4) CVC
                modificadoHtml = modificadoHtml.replace(
                        "<input type=\"text\" class=\"card-input security-code\" placeholder=\"***\">",
                        "<input type=\"text\" class=\"card-input security-code\" name=\"cvc\" placeholder=\"***\">"
                );

                // Añadir campos ocultos para Importe, Ticket, Fecha (unida a hora), etc.
                String hiddenFields = ""
                        + "<input type=\"hidden\" name=\"importe\" value=\"" + precio + "\"/>"
                        + "<input type=\"hidden\" name=\"ticketExt\" value=\"" + ticket + "\"/>"
                        + "<input type=\"hidden\" name=\"fecha\" value=\"" + fecha + " " + hora + "\"/>";

                modificadoHtml = modificadoHtml.replace("</form>", hiddenFields + "</form>");

                // Reemplazar textos fijos en el HTML
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

    /**
     * Maneja el POST del formulario, construye el JSON (PagoCompletoRequest)
     * y lo envía al mismo endpoint /pago/realizar del TPVV (sin crear uno nuevo).
     */
    @PostMapping("/realizarPagoProxy")
    public String realizarPagoProxy(@ModelAttribute PagoCompletoForm form,
                                    Model model) {

        Optional<String> apiKeyOpt = parametroComercioService.getValorParametro("apiKey");
        if (apiKeyOpt.isEmpty()) {
            model.addAttribute("error", "API Key no encontrada.");
            return "error/404";
        }
        String apiKey = apiKeyOpt.get();

        // Creamos TarjetaPagoData
        TarjetaPagoData tarjetaData = new TarjetaPagoData();
        tarjetaData.setNombre(form.getNombre());
        tarjetaData.setNumeroTarjeta(form.getNumeroTarjeta());
        tarjetaData.setCvc(Integer.parseInt(form.getCvc()));

        // Parse de caducidad "mm/aa" a Date
        Date fechaCaducidad = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
            fechaCaducidad = sdf.parse(form.getCaducidad());
        } catch (ParseException e) {
            // Si falla el parseo, dejamos fechaCaducidad como 'new Date()' (ejemplo)
        }
        tarjetaData.setFechaCaducidad(fechaCaducidad);

        // Creamos PagoData
        PagoData pagoData = new PagoData();
        pagoData.setImporte(form.getImporte() != null ? form.getImporte() : 0.0);
        pagoData.setTicketExt(form.getTicketExt());

        // Si form.getFecha() es null, establecemos 'new Date()' como fallback
        if (form.getFecha() == null) {
            pagoData.setFecha(new Date());
        } else {
            pagoData.setFecha(form.getFecha());
        }

        // Armamos la request
        PagoCompletoRequest requestBody = new PagoCompletoRequest(pagoData, tarjetaData);

        // Cabecera con la API Key
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PagoCompletoRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        // Llamada al MISMO endpoint de TPVV: /pago/realizar
        String urlTPVV = "http://localhost:8123/tpvv/boardalo/pago/realizar";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(urlTPVV, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String msg = response.getBody();
                model.addAttribute("msgOk", msg);
                return "pagoOk";
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
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error 404");
        }

    }
}
