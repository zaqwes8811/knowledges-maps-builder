package chew.chew_third_party.gae;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Car {
    @Id Long id;
    @Index String license;
    int color;
}