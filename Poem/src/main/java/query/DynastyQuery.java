package query;

import data.model.DynastyData;
import data.po.Dynasty;

public class DynastyQuery implements Query {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAns(String openid) {
        Dynasty dynasty = DynastyData.query(name);
        if (dynasty == null) {
            return "没有收录朝代" + name;
        }
        return String.format("【%s】(%d年~%d年)\n%s",
                dynasty.getName(),
                dynasty.getBeg(),
                dynasty.getEnd(),
                dynasty.getDescription()
        );
    }
}
