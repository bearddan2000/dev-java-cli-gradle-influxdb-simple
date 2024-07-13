package example.model;

import lombok.*;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Data
@AllArgsConstructor
@Measurement(name = "animal.dog")
public class Dog{

    @Column(name = "time")
    private Instant time;

    @Column(name = "id")
    private Long id;

    @Column(name = "breed")
    private String breed;

    @Column(name = "color")
    private String color;

    public Dog(Long a, String b, String c){
        time = null;
        id = a;
        breed = b;
        color = c;
    }
}