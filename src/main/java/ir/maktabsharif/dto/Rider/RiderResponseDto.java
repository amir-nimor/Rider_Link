package ir.maktabsharif.dto.Rider;

import lombok.Builder;

@Builder
public record RiderResponseDto(
        Long id,
        String username,
        String fullName,
        String phoneNumber,
        String homeAddress
) {

}
