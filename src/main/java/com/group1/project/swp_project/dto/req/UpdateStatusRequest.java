package com.group1.project.swp_project.dto.req;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateStatusRequest {
    private String newStatus;
    private Long staffId;
}
