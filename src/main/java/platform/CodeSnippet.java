package platform;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class CodeSnippet {
    @Id
    private String id;
    private String code;
    private String date;
    private long time;
    private long views;


    public CodeSnippet() {
        this.id = RandomStringUUID.getRandomUUIDString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }
}
