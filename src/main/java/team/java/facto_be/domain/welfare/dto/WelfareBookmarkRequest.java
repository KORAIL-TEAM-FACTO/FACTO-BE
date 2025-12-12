package team.java.facto_be.domain.welfare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WelfareBookmarkRequest(
        @NotBlank
        @Size(max = 100)
        String servId,

        @NotBlank
        @Size(max = 255)
        String servNm
) {
}
