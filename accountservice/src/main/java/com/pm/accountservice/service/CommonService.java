package com.pm.accountservice.service;

import com.pm.accountservice.config.CustomAuthenticationDetails;
import com.pm.accountservice.exceptions.tenants.TenantNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class CommonService {

    private final TenantService tenantService;

    public CommonService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public Mono<Long> getCurrentTenantId() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    var auth = securityContext.getAuthentication();
                    if (auth != null && auth.getDetails() instanceof CustomAuthenticationDetails) {
                        var tenantId = ((CustomAuthenticationDetails) auth.getDetails()).tenantId();

                        return tenantService.existsById(tenantId)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.just(tenantId);
                                    } else {
                                        return Mono.error(new TenantNotFoundException("Tenant not found"));
                                    }
                                });
                    }
                    return Mono.error(new IllegalStateException("TenantId not available in security context"));
                });
    }

    // comentando esto para dejar referencia a sink, pero es mejor usar reactivo de punta a punta
//    public Mono<Long> getCurrentTenantId() {
//        return ReactiveSecurityContextHolder.getContext()
//                .publishOn(Schedulers.boundedElastic())
//                // handle es un operador de sink el cual te permite controlar flujos reactivos mas complejos y que
//                // puedan devolver distintos resultados, dinamicos
//                // es una forma de tener manejo de errores y manejo de logica en un solo bloque de codigo
//                // cuando se necesiten multiples condicionales o el resultado es algo dinamico, sink es utilizado
//
//                // Se usa sink cuando **son útiles cuando hay lógica compleja, múltiples condiciones, o cuando el flujo reactivo debe ser dinámico,
//                // sobre todo en casos donde necesitas emitir valores o manejar errores manualmente.
//                // sink en resumen se usa para flujos DINAMICOS (ejemplo es programacion imperativa impredecible) y no se puede dividir en pasos declarativos
//                // El flujo normal con map filter flatMap switchIfEmpty se usa cuando la logica es sencilla y lo que retorna siempre es lo que uno espera
//                // es se usa para flujos LINEALES y se pueden dividir en pasos declarativos (programacion funcional predecible y pura)
//                .handle((securityContext, sink) -> {
//                    var auth = securityContext.getAuthentication();
//                    // sink aca es util porque podemos manejar logica que puede ser dinamica y compleja
//                    // LA IA dice
//                    // Ejemplo: Si necesitas hacer muchos `if/else` con condiciones diferentes y emitir diferentes valores o errores basados en esas condiciones.
//                    //  Ejemplo: Emitir múltiples valores, completar el flujo manualmente, o lanzar errores en medio de un procesamiento complejo.
//                    if (auth != null && auth.getDetails() instanceof CustomAuthenticationDetails) {
//                        var tenantId = ((CustomAuthenticationDetails) auth.getDetails()).tenantId();
//
//                        tenantService.existsById(tenantId)
//                                .subscribe(exists -> {
//                                    if (exists) {
//                                        sink.next(tenantId);
//                                    } else {
//                                        sink.error(new IllegalStateException("Tenant not found"));
//                                    }
//                                }, error -> sink.error(new RuntimeException("Error while checking tenant existence" + error.getMessage())));
//
//                        return;
//                    }
//
//                    sink.error(new IllegalStateException("TenantId not available in security context"));
//                });
//    }
}
