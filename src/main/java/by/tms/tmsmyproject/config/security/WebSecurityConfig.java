package by.tms.tmsmyproject.config.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/library/**", "/error", "/images/**", "/css/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/").loginProcessingUrl("/login").defaultSuccessUrl("/library/login", true).failureUrl("/?error")
                .and()
                .logout().logoutSuccessUrl("/").permitAll()
                .and()
                .build();
    }
}
