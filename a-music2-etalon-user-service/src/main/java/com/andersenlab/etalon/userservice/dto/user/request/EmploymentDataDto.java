package com.andersenlab.etalon.userservice.dto.user.request;

import com.andersenlab.etalon.userservice.util.validation.ValidWorkField;
import com.andersenlab.etalon.userservice.util.validation.groups.SizeCheck;
import com.andersenlab.etalon.userservice.util.validation.groups.ValidWorkCheck;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@GroupSequence({SizeCheck.class, ValidWorkCheck.class, EmploymentDataDto.class})
public record EmploymentDataDto(
    @Schema(example = "Assembler-dev")
        @ValidWorkField(fieldName = "Position", groups = ValidWorkCheck.class)
        @Size(
            groups = SizeCheck.class,
            min = 2,
            max = 50,
            message = "Size must be between 2 and 50")
        String position,
    @Schema(example = "Andersenlab")
        @ValidWorkField(fieldName = "Place of work", groups = ValidWorkCheck.class)
        @Size(
            min = 2,
            max = 100,
            message = "Size must be between 2 and 100",
            groups = SizeCheck.class)
        String placeOfWork) {}
