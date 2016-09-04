package bleeding_edge;

/**
 * Created by zaqwes on 04/09/16.
 */
import com.googlecode.jcsv.annotations.MapToColumn;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

@CsvDataType()
class GoogleTranslatorRecord {
    public GoogleTranslatorRecord() {}
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @CsvField(pos = 1)
    @MapToColumn(column=0)
    public String from;

    @CsvField(pos = 2)
    @MapToColumn(column=1)
    public String to;

    @CsvField(pos = 3)
    @MapToColumn(column=2)
    public String what;

    @CsvField(pos = 4)
    @MapToColumn(column=3)
    public String translate;

    // getter, equals, toString, ...
}