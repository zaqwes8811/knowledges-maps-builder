package backend;
/**
 * Created by zaqwes on 04/09/16.
 */

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import java.io.Serializable;

@CsvDataType()
public class GoogleTranslatorRecord implements Serializable {
    public GoogleTranslatorRecord() {}

    @CsvField(pos = 1)
    public String from;

    @CsvField(pos = 2)
    public String to;

    @CsvField(pos = 3)
    public String what;

    @CsvField(pos = 4)
    public String translate;
}