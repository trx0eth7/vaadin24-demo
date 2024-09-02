package ru.trx.vaadindemo.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import ru.trx.vaadindemo.view.login.LoginView;

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    UserDetailsService users() {
        var alice = User.builder()
                .username("Alina P")
                .password("{noop}password")
                .roles(Roles.USER)
                .build();
        var bob = User.builder()
                .username("Ilya K")
                .password("{noop}password")
                .roles(Roles.USER)
                .build();
        var admin = User.builder()
                .username("Alexander V")
                .password("{noop}admin")
                .roles(Roles.ADMIN, Roles.USER)
                .build();
        return new InMemoryUserDetailsManager(alice, bob, admin);
    }
}
