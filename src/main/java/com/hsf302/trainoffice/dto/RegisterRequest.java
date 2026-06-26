package com.hsf302.trainoffice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Username không được để trống")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]{4,20}$",
            message = "Username chỉ chứa chữ, số, dấu _ và từ 4-20 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password_hash;

    @NotBlank(message = "Xác nhận mật khẩu")
    private String confirmPassword;
}
