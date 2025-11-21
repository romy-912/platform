package com.romy.platform.main.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class FavoriteDto {

    @Schema(name = "FavoriteDto.CreateReq", description = "즐겨찾기 등록")
    public record CreateReq(
            @NotBlank
            @Size(max = 20)
            @Schema(description = "구분")
            String div,
            @NotBlank
            @Size(max = 20)
            @Schema(description = "참조키코드")
            String keyCd
    ) {}

}
