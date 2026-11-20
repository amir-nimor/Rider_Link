package ir.maktabsharif.command;

import ir.maktabsharif.dto.Driver.DriverRequestDto;
import ir.maktabsharif.dto.Driver.DriverResponseDto;
import ir.maktabsharif.model.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverCommand {

    Driver dtoToDao(DriverRequestDto driverRequestDto);

    DriverResponseDto daoToDto(Driver driver);
}
