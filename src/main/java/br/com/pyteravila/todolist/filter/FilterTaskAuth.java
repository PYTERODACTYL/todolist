package br.com.pyteravila.todolist.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.pyteravila.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
        throws ServletException, IOException {
            var servletPath = request.getServletPath();

            if(!servletPath.startsWith("/tasks/")) {
                filterChain.doFilter(request, response);
                return;
            }

            var authorization = request.getHeader("Authorization");
            System.out.println("FilterTaskAuth: Authorization header: " + authorization);

            byte[] decodedBytes = java.util.Base64.getDecoder().decode(authorization.substring("Basic ".length()).trim());

            var authString = new String(decodedBytes);
            System.out.println("FilterTaskAuth: Decoded authorization: " + authString);

            String[] authParts = authString.split(":", 2);

            var username = authParts[0];
            var password = authParts[1];

            var user = this.userRepository.findByUsername(username);

            if (user == null ) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            } else {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());

                if(passwordVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid password");
                }
            }
    }
    
}
