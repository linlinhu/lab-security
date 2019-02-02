package cn.cdyxtech.lab.vo;

public class FlockVO extends AbstractVO {

    private Long id;

    private String name;

    @Override
    public boolean valid() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
