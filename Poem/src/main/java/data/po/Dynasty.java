package data.po;

public class Dynasty {
    String description;
    String name;
    String _id;
    Integer beg;
    Integer end;

    public Integer getBeg() {
        return beg;
    }

    public void setBeg(Integer beg) {
        this.beg = beg;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
