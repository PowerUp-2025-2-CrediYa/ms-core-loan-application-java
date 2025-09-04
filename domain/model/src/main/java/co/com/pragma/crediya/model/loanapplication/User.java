package co.com.pragma.crediya.model.loanapplication;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String documentId;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
    private Double baseSalary;
}
