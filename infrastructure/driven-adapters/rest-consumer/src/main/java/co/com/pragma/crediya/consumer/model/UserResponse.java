package co.com.pragma.crediya.consumer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta al crear un usuario")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserResponse {

    private String firstName;

    private String lastName;

    private String email;

    private String documentId;

    private LocalDate birthDate;

    private String address;

    private String phoneNumber;

    private Double baseSalary;
}

