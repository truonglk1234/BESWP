package com.group1.project.swp_project.dto.req;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Data
public class MenstrualCycleRequest {
    private String cycleStartDate;
    private Integer cycleLength;
    private Integer periodLength;
    private Boolean remindOvulation;
    private Boolean remindHighFertile;
    private Boolean remindPill;
    private String pillTime;
    private String note;
}
