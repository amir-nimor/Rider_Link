package ir.maktabsharif.command;

import ir.maktabsharif.dto.User.UserRequestDto;
import ir.maktabsharif.dto.User.UserResponseDto;
import ir.maktabsharif.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserCommand {
    User dtoToDao(UserRequestDto userRequestDto);

    UserResponseDto daoToDto (User user);
}
