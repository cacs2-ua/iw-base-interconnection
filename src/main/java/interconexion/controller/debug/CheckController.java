package interconexion.controller.debug;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class CheckController {

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/bienvenida";
    }

    @GetMapping("/bienvenida")
    public String checkView(Model model) {
        return "bienvenida"; // Plantilla adaptada con thymeleaf
    }
}
