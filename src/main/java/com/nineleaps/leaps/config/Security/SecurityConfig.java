package com.nineleaps.leaps.config.Security;

import com.nineleaps.leaps.config.Filter.CustomAuthenticationFilter;
import com.nineleaps.leaps.config.Filter.CustomAuthorizationFilter;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.implementation.RefreshTokenServiceImpl;
import com.nineleaps.leaps.service.implementation.UserServiceImpl;
import com.nineleaps.leaps.utils.SecurityUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final UserServiceImpl userServiceImpl;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtility securityUtility;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), securityUtility, refreshTokenRepository);

        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");

        http.csrf().disable();

//        http.authorizeRequests().anyRequest().permitAll();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/v1/login/**","/api/v1/phoneNo","/api/v1/otp","/api/v1/user/signup").permitAll();


        http.authorizeRequests().antMatchers("/api/v1/category/list","/api/v1/file/view/**","/api/v1/subcategory/list","/api/v1/product/search", "/api/v1/product/list", "/api/v1/product/listByPriceRange").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/v1/users").hasAuthority("admin");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/v1/address/add").hasAnyAuthority("OWNER","BORROWER");

        http.authorizeRequests().antMatchers("/api/v1/address/update/**","/api/v1/address/listaddress","/api/v1/address/delete/**").hasAnyAuthority("OWNER","BORROWER");

        http.authorizeRequests().antMatchers("/api/v1/cart/add","/api/v1/cart/list","/api/v1/cart/update","/api/v1/cart/delete/**").hasAnyAuthority("OWNER","BORROWER");

        http.authorizeRequests().antMatchers("/api/v1/category/create","/api/v1/category/update/**").permitAll();

        http.authorizeRequests().antMatchers("/api/v1/order/create-checkout-session","/api/v1/order/add","/api/v1/order/list","/api/v1/order/getOrderById/**").hasAnyAuthority("BORROWER","OWNER");

        http.authorizeRequests().antMatchers("/api/v1/product/update/**","/api/v1/product/listByProductId/**", "/api/v1/product/listInDesc", "/api/v1/product/listOWNERProducts","/api/v1/subcategory/listbyid/**").hasAnyAuthority("OWNER","BORROWER");

        http.authorizeRequests().antMatchers("/api/v1/product/listBySubcategoryId/**", "/api/v1/product/listByCategoryId/**", "/api/v1/product/listByProductId/**", "/api/v1/product/listByPriceRange").hasAnyAuthority("OWNER","BORROWER");


        http.authorizeRequests().antMatchers("/api/v1/file/upload","/api/v1/file/download/**","/api/v1/file/delete/**","/api/v1/file/uploadbannerimage", "/api/v1/product/add").hasAuthority("OWNER");

        http.authorizeRequests().antMatchers("/api/v1/order/dashboardSubCategoriesAnalytics","/api/v1/order/dashboard","/api/v1/order/onClickDashboard","/api/v1/order/dashboardOrderItems","/api/v1/order/dashboardSubCategoriesAnalytics").hasAuthority("OWNER");

        http.authorizeRequests().antMatchers("/api/v1/subcategory/create","/api/v1/subcategory/update/**").permitAll();

        http.authorizeRequests().antMatchers("/api/v1/user/switch","/api/v1/user/update","api/v1/user/getCurrentUsername","api/v1/user/getUser").hasAnyAuthority("OWNER","BORROWER");

        http.authorizeRequests().antMatchers("/api/v1/wishlist/add","/api/v1/wishlist/addtowishlist","/api/v1/wishlist/findallwishlist","/api/v1/wishlist/remove","/api/v1/wishlist/removebyid").hasAnyAuthority("OWNER","BORROWER");
        http.authorizeRequests().antMatchers("/api/v1/file/uploadProfileImage").hasAnyAuthority("OWNER","BORROWER");


        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(userServiceImpl, refreshTokenRepository, securityUtility, customAuthenticationFilter), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}














