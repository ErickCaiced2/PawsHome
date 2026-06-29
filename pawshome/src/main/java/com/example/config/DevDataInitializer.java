package com.example.config;

import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private static final String ADMIN_CORREO = "admin@pawshome.local";
    private static final String USUARIO_CORREO = "usuario@pawshome.com";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearAdministradorSiNoExiste();
        crearUsuarioSiNoExiste();
    }

    private void crearAdministradorSiNoExiste() {
        if (usuarioRepository.findByCorreo(ADMIN_CORREO).isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("Administrador Refugio");
        admin.setCorreo(ADMIN_CORREO);
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);
        admin.setActivo(true);

        usuarioRepository.save(admin);
    }

    private void crearUsuarioSiNoExiste() {
        if (usuarioRepository.findByCorreo(USUARIO_CORREO).isPresent()) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario Adoptante");
        usuario.setCorreo(USUARIO_CORREO);
        usuario.setPasswordHash(passwordEncoder.encode("usuario123"));
        usuario.setRol(RolUsuario.USUARIO);
        usuario.setActivo(true);

        usuarioRepository.save(usuario);
    }
}
