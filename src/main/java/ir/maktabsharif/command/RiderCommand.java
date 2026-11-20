package ir.maktabsharif.command;

import ir.maktabsharif.dto.Rider.RiderRequestDto;
import ir.maktabsharif.dto.Rider.RiderResponseDto;
import ir.maktabsharif.model.Rider;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface RiderCommand {

    Rider dtoToDao(RiderRequestDto riderRequestDto);

    RiderResponseDto daoToDto(Rider rider);
}
