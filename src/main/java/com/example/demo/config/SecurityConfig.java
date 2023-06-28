package com.example.demo.config;

import com.example.demo.exceptionHandler.AccessDeniedHandlerImp;
import com.example.demo.security.filter.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JWTFilter jwtAuthFilter;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();//設定加密類型 作為帳密驗證器
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()//關閉CSRF (等於關閉預設登入頁)
                //預設 是 從 Session獲取 SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//關閉SESSION
                .and()
                .authorizeRequests()
                .antMatchers("/users/login").permitAll()
                .antMatchers(HttpMethod.DELETE,"/users/*").hasAnyAuthority("Manager") // 需要 ADMIN 角色的 URL
                .antMatchers(HttpMethod.POST,"/users").permitAll()
                .anyRequest().authenticated()//除permitAll 以外的api 都必需認證
                .and()
                .formLogin()
                .and()
                .logout()
                .permitAll();

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);//設定FILTER 即出現在哪個CLASS 之前

        //配置異常處理
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)//配置認證失敗
                .accessDeniedHandler(accessDeniedHandler);//配置授權失敗

    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
