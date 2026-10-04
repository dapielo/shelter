package com.david.shelter.config;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.david.shelter.service.JwtService;
import com.david.shelter.model.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
// Este es un filtro de Spring Security que se va a añadir a la cadena de filtros, para que haga las comprobaciones pertientes en el token
// para las peticiones que requieren de autenticacion, antes de que la WebRequest llegue siquiera al controlador.
// extiende de OncePerRequestFilter, que como su nombre indica es un filtro que aplica una vez por peticion
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);

        // Si tenemos token valido
        if (token != null && jwtService.verifyToken(token)) {

            // Sacamos el usuario y el rol del token (no se puede falsear, esta firmado en su creacion y no se puede modificar sin saber la clave de verificacion)
            String user = jwtService.getUserFromToken(token);
            Role rol = jwtService.getRoleFromToken(token);
            // Tenemos que convertirlo en una lista aunque sea de un elemento, de objetos uqe implementen GrantedAuthority, para que los admita el UsernamePasswordAutnenticationToken
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol.toString()));

            // Creamos un objeto que implemente Authentication, con esta informacion sobre la peticion con token, para meterlo en el SecurityContextHolder
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, authorities);

            // Inyectamos las credenciales en el SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // Ahora llamamos a doFilter para que el filterChain pase al siguiente filtro o envie la peticion al endpoint si no hay mas y pasa el filtro
        filterChain.doFilter(request, response);
    }

    public String getTokenFromRequest(HttpServletRequest request) {

        // Sacamos el token de la cabecera "Authorization"
        String tokenBearer = request.getHeader("Authorization");

        // Si existe la cabecera y el contenido empieza por Bearer (es como empieza la cabecera del token) devolvemos el token sin el Bearer
        if (tokenBearer !=null && tokenBearer.startsWith("Bearer ")) {
            return tokenBearer.substring(7);
        }
        // De lo contrario devolvemos null
        return null;
    }

}
