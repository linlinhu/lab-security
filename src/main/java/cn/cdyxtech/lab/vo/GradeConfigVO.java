package cn.cdyxtech.lab.vo;

import java.util.List;

public class GradeConfigVO extends AbstractVO{

    private Integer limit;//最高问题等级
    private List<GradeColor> gradeColors;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<GradeColor> getGradeColors() {
        return gradeColors;
    }

    public void setGradeColors(List<GradeColor> gradeColors) {
        this.gradeColors = gradeColors;
    }

    @Override
    public boolean valid() {
        return true;
    }

    public static class GradeColor{
        private Integer grade;
        private String color;

        public Integer getGrade() {
            return grade;
        }

        public void setGrade(Integer grade) {
            this.grade = grade;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getGradeStr(){
            return this.grade.toString();
        }
    }
}
