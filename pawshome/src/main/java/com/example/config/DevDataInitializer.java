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

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
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
}
