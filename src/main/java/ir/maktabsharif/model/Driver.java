package ir.maktabsharif.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("DRIVER")
@SuperBuilder
public class Driver extends User{

    private String vehicleLicenseNumber;
}
