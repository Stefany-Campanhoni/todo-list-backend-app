package br.com.stefanycampanhoni.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.stefanycampanhoni.todolist.user.UserModel;
import br.com.stefanycampanhoni.todolist.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().startsWith("/tasks")) {
            String authorization = request.getHeader("Authorization");
            String encodedAuth = authorization.substring("Basic".length()).trim();

            byte[] decodedAuth = Base64.getDecoder().decode(encodedAuth);

            // [0] username, [1] password
            String[] credentials = new String(decodedAuth).split(":");

            UserModel user = userRepository.findByUsername(credentials[0]);

            if (user == null) {
                response.sendError(401);
            } else {
                var verifiedPassword = BCrypt.verifyer().verify(credentials[1].toCharArray(), user.getPassword());

                if (verifiedPassword.verified) {
                    request.setAttribute("userId", user.getId());

                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

}
