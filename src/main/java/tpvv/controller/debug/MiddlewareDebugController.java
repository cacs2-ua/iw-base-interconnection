package tpvv.controller.debug;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tpvv.authentication.ManagerUserSession;
import tpvv.dto.LoginData;
import tpvv.dto.RegistroData;
import tpvv.dto.UsuarioData;
import tpvv.service.UsuarioService;
import tpvv.service.exception.UsuarioServiceException;

@Controller
public class MiddlewareDebugController {

    @GetMapping("/api/admin/check")
    public String adminCheck(Model model) {
        return "debug/adminCheck";
    }

    @GetMapping("/api/tecnico/check")
    public String tecnicoCheck(Model model) {
        return "debug/tecnicoCheck";
    }

    @GetMapping("/api/comercio/check")
    public String comercioCheck(Model model) {
        return "debug/comercioCheck";
    }

    @GetMapping("/api/tecnico-or-admin/check")
    public String tecnicoOrAdminCheck(Model model) {
        return "debug/tecnicoOrAdminCheck";
    }

    @GetMapping("/api/general/check")
    public String generalCheck(Model model) {
        return "debug/generalCheck";
    }
}

