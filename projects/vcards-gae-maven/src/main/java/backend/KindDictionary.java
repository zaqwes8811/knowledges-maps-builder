package backend;

import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import net.jcip.annotations.NotThreadSafe;

import java.util.List;

/**
 * Created by zaqwes on 04/09/16.
 */
@NotThreadSafe
@Entity
public class KindDictionary {
    public KindDictionary() { }
    public @Id
    Long id;

    @Index
    public String name;

    @Serialize
    public List<GoogleTranslatorRecord> records;

    public void persist()
    {
        final KindDictionary self = this;
        OfyService.ofy().transactNew(OfyService.COUNT_REPEATS, new VoidWork() {
            public void vrun() {
                OfyService.ofy().save().entity(self).now();
            }
        });
    }
}
