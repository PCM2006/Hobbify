package com.app.hobbify.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.app.hobbify.model.Cuenta;
import com.app.hobbify.model.TipoCuenta;
import com.app.hobbify.repository.CuentaRepository;

@Service
public class CuentaService implements UserDetailsService {

    private final CuentaRepository cuentas;
    private final PasswordEncoder passwordEncoder;

    CuentaService(CuentaRepository cuentas, PasswordEncoder passwordEncoder) {
        this.cuentas = cuentas;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Cuenta registrar(String nombre, String apellido, String correo, String contrasena,
                            TipoCuenta tipo, String nombreComercial) {
        if (tipo == TipoCuenta.EMPRESA && (nombreComercial == null || nombreComercial.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre comercial es obligatorio");
        }

        long idUsuario = cuentas.crearUsuario(nombre.strip(), apellido == null ? null : apellido.strip(),
                        correo, passwordEncoder.encode(contrasena))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado"));

        if (tipo == TipoCuenta.EMPRESA) {
            cuentas.crearEmpresa(idUsuario, nombreComercial.strip());
        }

        return cuentaActiva(correo);
    }

    public Cuenta cuentaActiva(String correo) {
        return cuentas.buscarPorCorreo(correo)
                .filter(Cuenta::activa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cuenta no disponible"));
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Cuenta cuenta = cuentas.buscarPorCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Cuenta no disponible"));
        return User.withUsername(cuenta.correo())
                .password(cuenta.hash())
                .roles(cuenta.tipo().name())
                .disabled(!cuenta.activa())
                .build();
    }
}
