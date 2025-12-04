package com.example.msBackend.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class EmailVerificationUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 验证码有效期（5分钟）
    private static final long VERIFICATION_CODE_EXPIRE = 5 * 60;

    // Redis中存储验证码的key前缀
    private static final String VERIFICATION_CODE_PREFIX = "verification_code:";

    /**
     * 生成6位随机验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送验证码邮件
     */
    public boolean sendVerificationCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("验证码");
            message.setText("您的验证码是：" + code + "\n验证码有效期为5分钟，请及时使用。");

            mailSender.send(message);

            // 将验证码存储到Redis中，设置过期时间
            String key = VERIFICATION_CODE_PREFIX + toEmail;
            stringRedisTemplate.opsForValue().set(key, code, VERIFICATION_CODE_EXPIRE, TimeUnit.SECONDS);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证验证码是否正确
     */
    public boolean verifyCode(String email, String code) {
        String key = VERIFICATION_CODE_PREFIX + email;
        String storedCode = stringRedisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功后删除验证码
            stringRedisTemplate.delete(key);
            return true;
        }

        return false;
    }
}