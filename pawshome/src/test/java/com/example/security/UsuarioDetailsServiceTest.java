package com.example.security;

import com.example.model.RolUsuario;
import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void loadUserByUsername_usuarioActivoAdminRefugio_retornaUserDetailsConRol() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("admin@paws.com");
        usuario.setPasswordHash("$2a$10$hashed");
        usuario.setRol(RolUsuario.ADMINISTRADOR_REFUGIO);
        usuario.setActivo(true);

        when(usuarioRepository.findByCorreo("admin@paws.com")).thenReturn(Optional.of(usuario));

        UserDetails result = usuarioDetailsService.loadUserByUsername("admin@paws.com");

        assertEquals("admin@paws.com", result.getUsername());
        assertEquals("$2a$10$hashed", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_REFUGIO")));
    }

    @Test
    void loadUserByUsername_usuarioInactivo_lanzaUsernameNotFoundException() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("inactivo@paws.com");
        usuario.setPasswordHash("hash");
        usuario.setRol(RolUsuario.USUARIO);
        usuario.setActivo(false);

        when(usuarioRepository.findByCorreo("inactivo@paws.com")).thenReturn(Optional.of(usuario));

        assertThrows(UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername("inactivo@paws.com"));
    }

    @Test
    void loadUserByUsername_correoNoExiste_lanzaUsernameNotFoundException() {
        when(usuarioRepository.findByCorreo("noexiste@paws.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername("noexiste@paws.com"));
    }

    @Test
    void loadUserByUsername_rolUsuario_retornaAuthorityRoleUsuario() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("user@paws.com");
        usuario.setPasswordHash("$2a$10$hash2");
        usuario.setRol(RolUsuario.USUARIO);
        usuario.setActivo(true);

        when(usuarioRepository.findByCorreo("user@paws.com")).thenReturn(Optional.of(usuario));

        UserDetails result = usuarioDetailsService.loadUserByUsername("user@paws.com");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO")));
    }
}
