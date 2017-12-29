package model;

import net.formio.validation.constraints.NotEmpty;

import java.util.Date;

public class Season {

    @NotEmpty
    private String name;
    @NotEmpty
    private Date startDate;
    @NotEmpty
    private Date endDate;

    public Season(String name, Date startDate, Date endDate)
    {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
