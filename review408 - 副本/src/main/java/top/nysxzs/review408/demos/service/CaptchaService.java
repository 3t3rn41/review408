package top.nysxzs.review408.demos.service;

import java.io.IOException;

public interface CaptchaService {
    byte[] generateCaptcha(String username) throws IOException;
    String verifyCode(String username,String verifyCode);

}
