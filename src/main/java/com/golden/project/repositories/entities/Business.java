package com.golden.project.repositories.entities;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "businesses")
public class Business implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne(targetEntity = Neighborhood.class)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private int neighborhoodId;

    @ManyToOne(targetEntity = Neighborhood.class)
    @JoinColumn(name = "name", insertable = false, updatable = false)
    private String neighborhoodName;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "location_start")
    private String locationStart;

    @Column(name = "location_end")
    private String locationEnd;

    @Column(name = "coordinates")
    private Point coordinates;
}
