package com.tresorshautebretagne.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String to, String name, String token) {
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        String html = """
                <html><body style="font-family:sans-serif;max-width:600px;margin:auto">
                <h2 style="color:#2d6a4f">Bienvenue sur Les Trésors de Haute Bretagne, %s !</h2>
                <p>Cliquez sur le bouton ci-dessous pour vérifier votre adresse email et commencer l'aventure :</p>
                <p style="text-align:center;margin:32px 0">
                  <a href="%s" style="background:#2d6a4f;color:white;padding:14px 28px;
                     text-decoration:none;border-radius:6px;font-size:16px">
                    Vérifier mon email
                  </a>
                </p>
                <p style="color:#666;font-size:13px">Ce lien expire dans 24 heures.<br>
                Si vous n'avez pas créé de compte, ignorez cet email.</p>
                </body></html>
                """.formatted(name, verificationLink);

        sendHtml(to, "Vérifiez votre adresse email — Les Trésors de Haute Bretagne", html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Impossible d'envoyer l'email de vérification : " + e.getMessage());
        }
    }
}
