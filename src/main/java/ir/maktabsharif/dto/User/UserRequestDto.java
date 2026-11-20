package ir.maktabsharif.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDto (
        @NotBlank @Size(min=5, max=20) @Pattern(regexp="[a-zA-Z0-9_]+")
        String username,

        @NotBlank @Size(min=8, max=72) @Pattern(regexp = "[\\x20-\\x7E]+",
                message = "Password must contain printable ASCII characters")
        String password,

        @NotBlank @Size(max=100)
        String fullName,

        @NotBlank @Size(min=7, max=15)
        String phoneNumber
){
}
