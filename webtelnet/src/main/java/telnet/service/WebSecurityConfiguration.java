package telnet.service;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsUtils;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

//    @Autowired
//    UserDetailsService userDetailsService;
//    @Autowired
//    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .authorizeRequests()
//                .requestMatchers(CorsUtils::isCorsRequest).permitAll()
//                .anyRequest().authenticated()
//                .and().httpBasic()
//                .and().addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class);
                .addFilterBefore(new WebSecurityCorsFilter(), ChannelProcessingFilter.class);
        //http.headers().frameOptions().disable();
        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
    }
}