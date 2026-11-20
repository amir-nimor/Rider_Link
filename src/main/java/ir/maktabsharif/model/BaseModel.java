package ir.maktabsharif.model;

import jakarta.persistence.*;


@MappedSuperclass
public class BaseModel<ID extends Number>{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;

}
