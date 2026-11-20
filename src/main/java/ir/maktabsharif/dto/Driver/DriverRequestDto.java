package ir.maktabsharif.dto.Driver;


import jakarta.validation.constraints.*;
import lombok.Builder;


@Builder
public record DriverRequestDto(

        @NotBlank @Size(min = 5, max = 20) @Pattern(regexp = "[a-zA-Z0-9_]+")
        String username,

        @Pattern(regexp = "[\\x20-\\x7E]+",
                message = "Password must contain printable ASCII characters")
        String password,

        @NotBlank @Size(max = 100)
        String fullName,

        @NotBlank @Size(min = 7, max = 15)
        String phoneNumber,

        @NotBlank @Size(max = 200)
        String vehicleLicenseNumber
) {

}
