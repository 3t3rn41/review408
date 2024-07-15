package top.nysxzs.review408.demos.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Date;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class JwtConfig {
    @EnableWebSecurity
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll();
            http.csrf().disable();
        }
    }
    // 用于生成JWT的辅助方法
    public String generateJwt(Integer id,String username) {
        JwtBuilder builder = Jwts.builder();
        String key="8&u0fAO/6868CY&wl69p/9uwN70k{Y%o";
        String token = builder
                // 头部
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "PS512")
                // 载荷数据/过期时间
                .claim("id", id)
                .claim("username", username)
                // 设置过期时间，这里设置为当前时间加上一小时
                //.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60*60*24*7))
                // 设置JWT的唯一标识（ID），这里使用UUID生成唯一ID
                .setId(UUID.randomUUID().toString())
                // 签名，使用HS256算法和指定的密钥（key）进行签名
                .signWith(SignatureAlgorithm.HS256, key)
                // 构造JWT令牌
                .compact();
        return ("Bearer "+token);
    }
}