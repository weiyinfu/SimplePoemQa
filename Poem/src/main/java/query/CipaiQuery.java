package query;

import data.model.CipaiData;
import data.po.Cipai;

public class CipaiQuery implements Query {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAns(String openid) {
        Cipai cipai= CipaiData.query(name);
        if(cipai==null){
            return "没有收入词牌<"+name+">";
        }
        return cipai.getDescription();
    }
}
