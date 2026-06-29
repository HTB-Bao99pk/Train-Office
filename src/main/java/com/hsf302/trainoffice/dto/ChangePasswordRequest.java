package com.hsf302.trainoffice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mat khau toi thieu 6 ky tu")
    private String currentPassword;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mat khau toi thieu 6 ky tu")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu")
    private String confirmNewPassword;
}
