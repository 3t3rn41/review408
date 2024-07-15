package top.nysxzs.review408.demos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class CaptchaServiceImpl implements CaptchaService{
    @Autowired

    private StringRedisTemplate stringRedisTemplate;

    private static final String CHAR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int WIDTH = 100;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;

    public byte[] generateCaptcha(String username) throws IOException {
        Random random = new Random();
        String captchaText = "";
        for (int i = 0; i < CODE_LENGTH; i++) {
            captchaText += CHAR_STRING.charAt(random.nextInt(CHAR_STRING.length()));
        }

        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // 设置背景色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(Color.BLUE);

        // 绘制验证码
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int x = (WIDTH - fontMetrics.stringWidth(captchaText)) / 2;
        int y = ((HEIGHT - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
        g2d.drawString(captchaText, x, y);

        // 释放资源
        g2d.dispose();

        // 将图片输出为字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        saveString(username,captchaText);
        return outputStream.toByteArray();
    }
    public String verifyCode(String username,String verifyCode)
    {
        if(verifyCode==null||verifyCode.equals("")||username==null||username.equals(""))
            return "验证码不能为空";
        String rightCode=(String) getString(username);
        if(verifyCode.equalsIgnoreCase(rightCode)) {
            deleteKey(username);
            return "验证码正确";
        }
        else{
          deleteKey(username);
          return "验证码错误";
        }
    }
    public void saveString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    public Boolean deleteKey(String key) {

        return stringRedisTemplate.delete(key);

    }
}