package com.hsf302.trainoffice.dto;

import com.hsf302.trainoffice.common.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ProfileForm {
    private Long passengerId;

    @NotBlank(message = "Ho ten khong duoc de trong")
    @Size(max = 100, message = "Ho ten toi da 100 ky tu")
    private String fullName;

    @Size(max = 30, message = "So giay to toi da 30 ky tu")
    private String identityNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Gender gender;

    private String currentPassword;

    private String newPassword;

    private String confirmPassword;
}
