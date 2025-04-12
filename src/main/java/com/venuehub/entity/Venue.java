package com.venuehub.entity;

import com.venuehub.enums.VenueStatus;
import com.venuehub.enums.VenueType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Table(name = "venue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private VenueStatus status;
    private String username;
    private String name;
    private String description;
    private String location;
    private String phone;
    @Enumerated(EnumType.STRING)
    private VenueType type;
    private int capacity;
    private int bookingPrice;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue", cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_id") // This creates the foreign key column in the Image table
    private List<Image> images = new ArrayList<>();

}
