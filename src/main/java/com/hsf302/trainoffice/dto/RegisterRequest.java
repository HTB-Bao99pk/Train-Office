package com.hsf302.trainoffice.dto;

import com.hsf302.trainoffice.common.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@Setter
public class RegisterRequest {
    private String  username;
    private String password;
    private String confirmPassword;
    private String fullname;
    private String indentityNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private Gender gender;


}
