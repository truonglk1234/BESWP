package com.group1.project.swp_project.dto.req;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ExaminationResultRequest {
    private String result;
    private String advice;
}
