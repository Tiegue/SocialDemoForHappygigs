
@Configuration
@EnableWebSecurity
public class SecuirityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .build();
    }
}