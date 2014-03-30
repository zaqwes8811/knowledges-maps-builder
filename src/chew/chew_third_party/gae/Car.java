package chew.chew_third_party.gae;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Car {
    @Id Long id;
    @Index String license;
    int color;
    @Load List<Ref<Engine>> owners = new ArrayList<Ref<Engine>>();
}