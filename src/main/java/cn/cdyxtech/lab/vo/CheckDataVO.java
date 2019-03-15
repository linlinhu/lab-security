package cn.cdyxtech.lab.vo;

import cn.cdyxtech.lab.util.TestRandomUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class CheckDataVO extends AbstractVO implements Comparable<CheckDataVO>{


    private static final long serialVersionUID = 7403729636544747134L;

    private Integer level;//层级 1 ，2 ，3
    private Boolean hasChild; //是否含有下级
    private Integer grade;// 等级
    private String name;// 名称
    private String keyPoint; // 检查要点
    private String seq; // 序列： 1， 1.1 ， 1.1.1 等
    private String text;
    private List<CheckDataVO> children;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    @JsonProperty("text")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyPoint() {
        return keyPoint;
    }

    public void setKeyPoint(String keyPoint) {
        this.keyPoint = keyPoint;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
    @JsonProperty("nodes")
    public List<CheckDataVO> getChildren() {
        if(this.children!=null){
            Collections.sort(children);
        }

        return children;
    }


    public void setChildren(List<CheckDataVO> children) {

        this.children = children;

    }

    @Override
    public boolean valid() {
        return true;
    }
    @Override
    public int compareTo(CheckDataVO o) {
        return TestRandomUtil.compareVersion(this.seq,o.getSeq());
    }
}
