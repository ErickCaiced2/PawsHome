package com.example.exception;

public class MascotaNoEncontradaException extends RuntimeException {
    public MascotaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
