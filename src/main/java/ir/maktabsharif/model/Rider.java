package ir.maktabsharif.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("RIDER")
@SuperBuilder
public class Rider extends User{


    private String homeAddress;
}
