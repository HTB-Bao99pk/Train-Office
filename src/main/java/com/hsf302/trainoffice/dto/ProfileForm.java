package com.hsf302.trainoffice.dto;

import com.hsf302.trainoffice.common.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ProfileForm {

    private Long passengerId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    @Pattern(
            regexp = "^[\\p{L}\\s]+$",
            message = "Họ tên chỉ được chứa chữ cái và khoảng trắng"
    )
    private String fullName;

    @Pattern(
            regexp = "^$|^\\d{9,12}$",
            message = "CCCD/CMND phải gồm 9-12 chữ số"
    )
    private String identityNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    private Gender gender;
}