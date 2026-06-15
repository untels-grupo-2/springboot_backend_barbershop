package com.diamondbarbershop.apibarbershop.emailPassword.service;

import com.diamondbarbershop.apibarbershop.emailPassword.dto.EmailDto;
import com.diamondbarbershop.apibarbershop.exceptions.EmailNoEnviadoException;
import com.diamondbarbershop.apibarbershop.exceptions.UsuarioExistenteException;
import com.diamondbarbershop.apibarbershop.models.Usuario;
import com.diamondbarbershop.apibarbershop.repositories.IUsuariosRepository;
import com.diamondbarbershop.apibarbershop.util.MensajeError;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final IUsuariosRepository usuariosRepository;

    @Value("${mail.urlFront}")
    private String urlFront;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private final static String SUBJECT = "Cambio de contraseña";

    public void enviarCorreo(EmailDto emailDto){
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("userName", emailDto.getNombre());
            model.put("url", urlFront + "?token=" + emailDto.getTokenPassword());

            context.setVariables(model);
            String htmlText =  templateEngine.process("email-template",context);
            helper.setFrom(emailDto.getMailFrom());
            helper.setTo(emailDto.getMailTo());
            helper.setSubject(emailDto.getSubject());
            helper.setText(htmlText,true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailNoEnviadoException(MensajeError.CORREO_NO_ENVIADO);
        }
    }

    /**
     * Envío genérico de correo HTML usando una plantilla Thymeleaf cualquiera.
     *
     * A diferencia de enviarCorreo(EmailDto), este método no asume nada del
     * contenido (tokenPassword, URL de reset, etc.). El emisor pasa el nombre
     * de la plantilla y un map de variables que el template puede consumir
     * con th:text="${variable}".
     *
     * Pensado para reutilización por cualquier listener que necesite enviar
     * notificaciones por correo — PB-39 lo usa desde NotificacionEmailReservaListener.
     *
     * @param mailTo       dirección del destinatario
     * @param subject      asunto del correo
     * @param templateName nombre del archivo sin extensión (ej. "reserva-confirmada")
     * @param variables    variables a interpolar en la plantilla
     * @throws EmailNoEnviadoException si falla el envío SMTP
     */
    public void enviarConTemplate(String mailTo, String subject,
                                  String templateName, Map<String, Object> variables) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            context.setVariables(variables);
            String htmlText = templateEngine.process(templateName, context);

            helper.setFrom(mailFrom);
            helper.setTo(mailTo);
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailNoEnviadoException(MensajeError.CORREO_NO_ENVIADO);
        }
    }

    public void procesarEnvioCorreo(EmailDto emailDto){
        //validar si el usuario existe
        Usuario usuario =usuariosRepository.findByUsername(emailDto.getUsername())
                .orElseThrow(() -> new UsuarioExistenteException(MensajeError.USUARIO_NO_EXISTENTE));

        if (!usuario.getEmail().equals(emailDto.getMailTo()))
            throw new EmailNoEnviadoException(MensajeError.CORREO_NO_COINCIDE);

        // Verificar si ya se generó un token recientemente
        if (usuario.getLastTokenRequest() != null && usuario.getLastTokenRequest().isAfter(LocalDateTime.now().minusMinutes(5))) {
            throw new EmailNoEnviadoException(MensajeError.CORREO_RECIENTE);
        }

        //Generamos el token de restablecimiento y actualizamos el campo
        String tokenPassword = UUID.randomUUID().toString();
        usuario.setTokenPassword(tokenPassword);
        usuario.setLastTokenRequest(LocalDateTime.now());
        usuariosRepository.save(usuario);

        //Configuramos el dto para el correo
        emailDto.setMailFrom(mailFrom);
        emailDto.setMailTo(usuario.getEmail());
        emailDto.setSubject(SUBJECT);
        emailDto.setUsername(usuario.getUsername());
        emailDto.setNombre(usuario.getNombre());
        emailDto.setTokenPassword(tokenPassword);

        //enviamos el correo
        enviarCorreo(emailDto);
    }
}
