package kinds;
/**
 * Created by zaqwes on 04/09/16.
 */

import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import gae_store_space.GAEStoreAccessManager;
import net.jcip.annotations.NotThreadSafe;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import static gae_store_space.OfyService.ofy;

@NotThreadSafe
@Entity
@CsvDataType()
public class GoogleTranslatorKind {
    @Id
//    private
    public String id;

    public GoogleTranslatorKind() {}

    @CsvField(pos = 1)
    public String from;

    @CsvField(pos = 2)
    public String to;

    @CsvField(pos = 3)
    public String what;

    @CsvField(pos = 4)
    public String translate;

    // must be final
    public void persist(final GoogleTranslatorKind kind)
    {
        // execution on dal - можно транслировать ошибку нижнего слоя
        ofy().transactNew(GAEStoreAccessManager.COUNT_REPEATS, new VoidWork() {
            public void vrun() {
                ofy().save().entity(kind).now();
            }
        });
    }
}