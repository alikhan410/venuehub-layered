package com.venuehub;

import com.venuehub.entity.Image;
import com.venuehub.entity.Role;
import com.venuehub.entity.User;
import com.venuehub.entity.Venue;
import com.venuehub.enums.VenueStatus;
import com.venuehub.enums.VenueType;
import com.venuehub.repository.RoleRepository;
import com.venuehub.repository.UserRepository;
import com.venuehub.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class VenuehubApplication {

    public static void main(String[] args) {
        SpringApplication.run(VenuehubApplication.class, args);
    }

    @Transactional
    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, VenueRepository venueRepository, PasswordEncoder encoder) {
        return args -> {

            Role userRole = new Role("USER");
            Role adminRole = new Role("ADMIN");
            Role vendorRole = new Role("VENDOR");

            if (roleRepository.findById(1).isEmpty()) {
                roleRepository.saveAll(List.of(userRole, adminRole, vendorRole));
            }


            if (userRepository.findByUsername("user").isEmpty()) {
                User user = User.builder()
                        .email("user@gmail.com")
                        .firstName("User")
                        .lastName("Khan")
                        .password(encoder.encode("pass"))
                        .username("user")
                        .authorities(new HashSet<>(List.of(userRole)))
                        .build();
                userRepository.save(user);
            }

            if (userRepository.findByUsername("vendor").isEmpty()) {
                User user = User.builder()
                        .email("vendor@gmail.com")
                        .firstName("Vendor")
                        .lastName("Khan")
                        .password(encoder.encode("pass"))
                        .username("vendor")
                        .authorities(new HashSet<>(List.of(userRole, vendorRole)))
                        .build();
                userRepository.save(user);
            }

            if (venueRepository.findById(1L).isEmpty()) {
                List<Image> imageSet1 = new ArrayList<>();
                List<Image> imageSet2 = new ArrayList<>();
                List<Image> imageSet3 = new ArrayList<>();
                List<Image> imageSet4 = new ArrayList<>();
                List<Image> imageSet5 = new ArrayList<>();

                imageSet1.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/blueskybanquet-1-JC0ZQKSzKR7oLyLrObunoEJdkiEzx9.jpg"));
                imageSet1.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/blueskybanquet-2-i9mmB1krYsKj2wga2cePGnjSLrG1Je.jpg"));
                imageSet1.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/blueskybanquet-3-00DDwxCn5spr3Avrdxu7krvEhHYF64.jpg"));
                imageSet2.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/emeraldbanquet-1-dY39c5tjd498rAm99eilaY7KAkkb21.jpg"));
                imageSet2.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/emeraldbanquet-2-dAzfCbeyGCOchxnCJ6XKacx0GCtJ7J.jpg"));
                imageSet2.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/emeraldbanquet-3-EVNo6EbKMMNasUAQtSkh7wbpMww13D.jpg"));
                imageSet3.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/grandbanquet-1-ZTL68KHMxgsc7QXmuwyou0QLsFWxye.jpg"));
                imageSet3.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/grandbanquet-2-1UeXR6z7xOREV8gUbVQ6HHam6tv8bU.jpg"));
                imageSet3.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/grandbanquet-3-AQQRJjnyHa9Q5TEozSspQKabWGSgRR.jpg"));
                imageSet4.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/greenfieldhall-1-OIm85aXEXor2shdaGkGvImNBu0E1Ux.jpg"));
                imageSet4.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/greenfieldhall-2-mns1dY144wobYBKexjc5697g3T1CvM.jpg"));
                imageSet4.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/greenfieldhall-3-cykW2wRQ58og9ozCVv6aSrt4fRhyM7.jpg"));
                imageSet5.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/royalhall-1-6gZwAJytPGb8erD0Iauiwvjn24XkVq.jpg"));
                imageSet5.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/royalhall-2-568f1ei3KMQEieNKRws4pgJrI47jnq.jpg"));
                imageSet5.add(new Image("https://s600n6ockzwcrvcg.public.blob.vercel-storage.com/royalhall-3-KIGbD4bDcJV1bI50kdkCWMugEe2nWm.jpg"));



                Venue venue1 = Venue.builder()
                        .status(VenueStatus.ACTIVE)
                        .username("vendor")
                        .name("Royal Banquet")
                        .description("An elegant banquet hall for weddings and special occasions.")
                        .location("456 Elm Street, Los Angeles, CA")
                        .images(imageSet1)
                        .phone("+1 987-654-3210")
                        .type(VenueType.BANQUET)
                        .capacity(300)
                        .bookingPrice(1200)
                        .build();

                Venue venue2 = Venue.builder()
                        .status(VenueStatus.DRAFT)
                        .username("vendor")
                        .name("Sunset Hall")
                        .description("A spacious hall with stunning sunset views.")
                        .location("789 Oak Avenue, Miami, FL")
                        .phone("+1 555-234-5678")
                        .images(imageSet2)
                        .type(VenueType.HALL)
                        .capacity(200)
                        .bookingPrice(1500)
                        .build();

                Venue venue3 = Venue.builder()
                        .status(VenueStatus.ACTIVE)
                        .username("vendor")
                        .name("Golden Banquet")
                        .description("A premium banquet venue for corporate events and celebrations.")
                        .location("321 River Road, Austin, TX")
                        .phone("+1 333-777-9999")
                        .images(imageSet3)
                        .type(VenueType.BANQUET)
                        .capacity(400)
                        .bookingPrice(1800)
                        .build();

                Venue venue4 = Venue.builder()
                        .status(VenueStatus.DRAFT)
                        .username("vendor")
                        .name("Grand City Hall")
                        .description("A modern hall in the heart of the city for all kinds of events.")
                        .location("567 Business Plaza, Chicago, IL")
                        .phone("+1 666-888-0000")
                        .images(imageSet4)
                        .type(VenueType.HALL)
                        .capacity(100)
                        .bookingPrice(800)
                        .build();
                Venue venue5 = Venue.builder()
                        .status(VenueStatus.ACTIVE)
                        .username("vendor")
                        .name("Grand Event Hall")
                        .description("A spacious venue for all kinds of events.")
                        .location("123 Main Street, New York, NY")
                        .phone("+1 123-456-7890")
                        .images(imageSet5)
                        .type(VenueType.BANQUET)
                        .capacity(500)
                        .bookingPrice(1000)
                        .build();

                venueRepository.saveAll(List.of(venue1, venue2, venue3, venue4, venue5));
            }
        };
    }

}
