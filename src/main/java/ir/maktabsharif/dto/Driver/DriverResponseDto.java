package ir.maktabsharif.dto.Driver;

import lombok.Builder;

@Builder
public record DriverResponseDto (
        Long id,
        String username,
        String fullName,
        String phoneNumber,
        String vehicleLicenseNumber
){
}
