package tpvv.interceptor;

import tpvv.authentication.ManagerUserSession;
import tpvv.controller.exception.NotFoundException;
import tpvv.dto.UsuarioData;
import tpvv.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor to ensure that only admin users can access certain URLs.
 */
@Component
public class ComercioInterceptor implements HandlerInterceptor {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Long userId = managerUserSession.usuarioLogeado();
        if (userId == null) {
            // User not logged in
            throw new NotFoundException("Página no encontrada.");
        }

        UsuarioData usuario = usuarioService.findById(userId);
        if (usuario == null || usuario.getTipoId() != 3) {
            // User is not admin
            throw new NotFoundException("Página no encontrada.");
        }

        // User is admin, allow access
        return true;
    }

}

