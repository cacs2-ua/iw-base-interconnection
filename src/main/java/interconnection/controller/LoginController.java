package interconnection.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import interconnection.authentication.ManagerUserSession;
import interconnection.dto.LoginData;
import interconnection.dto.RegistroData;
import interconnection.dto.UsuarioData;
import interconnection.service.UsuarioService;
import interconnection.service.exception.UsuarioServiceException;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    private Long getUsuarioLogeadoId() {
        return managerUserSession.usuarioLogeado();
    }

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (getUsuarioLogeadoId() != null) {
            return "redirect:/api/general/bienvenida";
        }

        model.addAttribute("loginData", new LoginData());
        return "formLogin"; // Plantilla adaptada con thymeleaf
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model) {
        UsuarioService.LoginStatus loginStatus = usuarioService.login(
                loginData.getEmail(),
                loginData.getContrasenya()
        );

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.getEmail());
            managerUserSession.logearUsuario(usuario.getId());

            // Redirigir a la página de bienvenida
            return "redirect:/api/general/bienvenida";

        } else {
            model.addAttribute("error", "Ha habido algún error al iniciar sesión");
            return "formLogin";
        }

    }

    // Nueva ruta para la página de bienvenida
    @GetMapping("/api/general/bienvenida")
    public String bienvenida(Model model) {
        // Aquí podrías pasar datos adicionales al modelo si lo deseas
        return "debug/bienvenida";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        RegistroData registroData = new RegistroData();
        model.addAttribute("registroData", registroData);

        // Si quisieras cargar dinámicamente los tipos desde la DB, harías algo así:
        // model.addAttribute("tiposUsuario", tipoUsuarioRepository.findAll());

        return "formRegistro";
    }

    @PostMapping("/registro")
    public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe");
            return "formRegistro";
        }

        try {
            UsuarioData nuevoUsuario = usuarioService.registrar(registroData);
            return "redirect:/login";
        } catch (UsuarioServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "formRegistro";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        managerUserSession.logout();
        return "redirect:/login";
    }
}
