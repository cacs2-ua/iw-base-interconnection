package tpvv.service;


import tpvv.dto.RegistroData;
import tpvv.dto.UsuarioData;
import tpvv.model.Comercio;
import tpvv.model.TipoUsuario;
import tpvv.model.Usuario;
import tpvv.repository.ComercioRepository;
import tpvv.repository.TipoUsuarioRepository;
import tpvv.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tpvv.service.exception.UsuarioServiceException;

import java.util.Optional;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Repositorio para tipos de usuario
    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private ComercioRepository comercioRepository;

    // Repositorio para comercio, si quieres buscar uno existente por ID
    // @Autowired
    // private ComercioRepository comercioRepository;

    @Autowired
    private ModelMapper modelMapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!encoder.matches(password, usuario.get().getContrasenya())) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    @Transactional
    public UsuarioData registrar(RegistroData registroData) {
        // Validaciones básicas
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(registroData.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + registroData.getEmail() + " ya está registrado");
        else if (registroData.getEmail() == null)
            throw new UsuarioServiceException("El usuario no tiene email");
        else if (registroData.getContrasenya() == null)
            throw new UsuarioServiceException("El usuario no tiene password");

        // Encriptar contraseña
        String contraEnClaro = registroData.getContrasenya();
        registroData.setContrasenya(encoder.encode(contraEnClaro));

        // Convertir DTO -> Entidad
        Usuario usuarioNuevo = modelMapper.map(registroData, Usuario.class);

        // Asignar TipoUsuario
        if (registroData.getTipoId() == null) {
            throw new UsuarioServiceException("No se especificó el tipo de usuario");
        }
        TipoUsuario tipo = tipoUsuarioRepository.findById(registroData.getTipoId())
                .orElseThrow(() -> new UsuarioServiceException("Tipo de usuario inválido"));
        usuarioNuevo.setTipo(tipo);

        // Asignar un comercio por defecto (ID=1), asumiendo que ya existe en DB
        // Comercio comercioDefecto = comercioRepository.findById(1L)
        //     .orElseThrow(() -> new UsuarioServiceException("No existe comercio con ID=1"));
        // usuarioNuevo.setComercio(comercioDefecto);

        // Si no tenemos un repositorio de comercio, podemos crear uno "dummy" (no recomendado):
        // usuarioNuevo.setComercio(new Comercio("defaultNIF")); // Genera problemas si la DB exige ID existente

        Comercio comercioDefecto = comercioRepository.findById(1L)
                .orElseThrow(() -> new UsuarioServiceException("No existe comercio con ID=1 en la base de datos"));

        // Asignarlo al usuario para no violar la constraint
        usuarioNuevo.setComercio(comercioDefecto);

        usuarioNuevo = usuarioRepository.save(usuarioNuevo);
        return modelMapper.map(usuarioNuevo, UsuarioData.class);
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }


}

