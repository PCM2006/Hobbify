package com.app.hobbify.auth;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.hobbify.auth.CuentaRepository.Cuenta;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final CuentaService cuentas;
    private final AuthenticationManager autenticador;
    private final SecurityContextRepository contextos;
    private final SessionAuthenticationStrategy estrategiaSesion;

    AuthController(CuentaService cuentas, AuthenticationManager autenticador,
                   SecurityContextRepository contextos, SessionAuthenticationStrategy estrategiaSesion) {
        this.cuentas = cuentas;
        this.autenticador = autenticador;
        this.contextos = contextos;
        this.estrategiaSesion = estrategiaSesion;
    }

    @GetMapping("/csrf")
    TokenCsrf csrf(CsrfToken token) {
        return new TokenCsrf(token.getHeaderName(), token.getToken());
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    CuentaRespuesta registrar(@Valid @RequestBody Registro registro) {
        validarLongitud(registro.contrasena());
        return CuentaRespuesta.desde(cuentas.registrar(registro.nombre(), registro.apellido(), registro.correo(),
                registro.contrasena(), registro.tipo(), registro.nombreComercial()));
    }

    @PostMapping("/login")
    CuentaRespuesta login(@Valid @RequestBody Login login, HttpServletRequest request, HttpServletResponse response) {
        validarLongitud(login.contrasena());
        Authentication autenticacion;
        try {
            autenticacion = autenticador.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(login.correo(), login.contrasena()));
        } catch (BadCredentialsException | AccountStatusException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        CuentaRespuesta cuenta = CuentaRespuesta.desde(cuentas.cuentaActiva(autenticacion.getName()));
        estrategiaSesion.onAuthentication(autenticacion, request, response);
        var contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacion);
        SecurityContextHolder.setContext(contexto);
        contextos.saveContext(contexto, request, response);
        return cuenta;
    }

    @GetMapping("/me")
    CuentaRespuesta consultaCuenta(Principal principal) {
        return CuentaRespuesta.desde(cuentas.cuentaActiva(principal.getName()));
    }

    private static void validarLongitud(String contrasena) {
        if (contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña admite hasta 72 bytes en UTF-8");
        }
    }

    record Registro(@NotBlank @Size(max = 120) String nombre, @Size(max = 120) String apellido,
                    @NotBlank @Email @Size(max = 254) String correo,
                    @NotBlank @Size(min = 8, max = 72) String contrasena,
                    @NotNull TipoCuenta tipo, @Size(max = 140) String nombreComercial) {

        Registro {
            correo = correo == null ? null : correo.strip();
        }
    }

    record Login(@NotBlank @Email @Size(max = 254) String correo, @NotBlank @Size(max = 72) String contrasena) {

        Login {
            correo = correo == null ? null : correo.strip();
        }
    }

    record TokenCsrf(String headerName, String token) {}

    record CuentaRespuesta(long id, String nombre, String apellido, String correo, TipoCuenta tipo, String nombreComercial) {

        static CuentaRespuesta desde(Cuenta cuenta) {
            return new CuentaRespuesta(cuenta.id(), cuenta.nombre(), cuenta.apellido(),
                    cuenta.correo(), cuenta.tipo(), cuenta.nombreComercial());
        }
    }
}
