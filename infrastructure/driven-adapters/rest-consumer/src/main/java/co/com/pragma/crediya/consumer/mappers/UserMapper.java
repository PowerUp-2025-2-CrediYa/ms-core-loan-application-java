package co.com.pragma.crediya.consumer.mappers;

import co.com.pragma.crediya.consumer.model.UserResponse;
import co.com.pragma.crediya.model.loanapplication.User;

public class UserMapper {

    UserMapper(){}
    public static User toDomain(UserResponse dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .documentId(dto.getDocumentId())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .baseSalary(dto.getBaseSalary())
                .build();
    }
}
