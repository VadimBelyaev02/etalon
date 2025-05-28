package com.andersenlab.etalon.userservice.dto.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserPatchRequestDto(
    @Valid
        @NotNull(message = "Employment data is required")
        @Schema(description = "Employment data of the user")
        EmploymentDataDto employmentData) {}
