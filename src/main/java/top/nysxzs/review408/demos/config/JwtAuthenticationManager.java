package top.nysxzs.review408.demos.config;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import top.nysxzs.review408.demos.pojo.query.Return;

@Component
public class JwtAuthenticationManager {
    public Return verifyJWT(String jwt) {
        Return ret=new Return();
        if (jwt==null)
        {
            ret.setCode(-1);
            ret.setMessage("请先登录");
            return ret;
        }
        jwt=jwt.split(" ")[1];
        // 创建JwtParser对象，用于解析JWT令牌
        JwtParser parser = Jwts.parser();
        // 解析JWT令牌并验证签名
        Jws<Claims> claimsJws = null;
        try {
            String key="8&u0fAO/6868CY&wl69p/9uwN70k{Y%o";
            claimsJws = parser
                    .setSigningKey(key) // 设置验证签名所使用的密钥（与生成JWT时使用的密钥相同）
                    .parseClaimsJws(jwt); // 解析JWT令牌
            ret.setCode(0);
            ret.setMessage(claimsJws.getBody().get("username").toString());
            return ret; // 输出解析后的JWT令牌的载荷内容
        } catch (ExpiredJwtException expiredJwtException) {
            // 验证失败，抛出异常
            ret.setCode(1);
            ret.setMessage("登录过期");
            return ret;
        }
        catch (Exception e){
            ret.setCode(2);
            ret.setMessage("用户名或密码错误");
            return ret;
        }
    }

}