package model;

public class GetUserCountCreationPerCompanyPerYear {
    private Integer companyId;
    private String year;
    private Integer count;

    public GetUserCountCreationPerCompanyPerYear() {
    }

    public GetUserCountCreationPerCompanyPerYear(Integer companyId, String year, Integer count) {
        this.companyId = companyId;
        this.year = year;
        this.count = count;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
