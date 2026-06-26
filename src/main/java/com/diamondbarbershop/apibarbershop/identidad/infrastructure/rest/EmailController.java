package com.diamondbarbershop.apibarbershop.identidad.infrastructure.rest;

import com.diamondbarbershop.apibarbershop.shared.infrastructure.rest.ApiResponse;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.email.EmailDto;
import com.diamondbarbershop.apibarbershop.shared.infrastructure.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@CrossOrigin
public class EmailController {

    private final EmailService emailService;

    @PostMapping("password")
    public ResponseEntity<ApiResponse<Object>> sendEmail(@RequestBody EmailDto emaildto){
        emailService.procesarEnvioCorreo(emaildto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.succes("Correo enviado con éxito a su email asociado, recuerde revisar su carpeta de spam.",null)
        );

    }


}
