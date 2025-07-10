package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "licenses")
public class License extends RepresentationModel<License> {

    @Id
    @Column
    private String licenseId;

    @Column
    private String description;

    @Column
    private String organizationId;

    @Column
    private String productName;

    @Column
    private String licenseType;

    @Column
    private String comment;

    public License withComment(String comment){
        this.setComment(comment);
        return this;
    }
}