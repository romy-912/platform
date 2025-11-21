package com.romy.platform.main.common.controller;

import static com.romy.platform.main.common.dto.FavoriteDto.*;
import com.romy.platform.main.common.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "즐겨찾기 관련")
@RequestMapping("/common/favorite")
public class FavorieteController {

    private final FavoriteService service;

    @DeleteMapping("/{favoriteCd}")
    @Operation(summary = "즐겨찾기 삭제")
    public int removeFavorite(@PathVariable @Parameter(description = "즐겨찾기 코드") String favoriteCd) {
        return this.service.removeFavorite(favoriteCd);
    }

    @PostMapping
    @Operation(summary = "즐겨찾기 등록")
    public String createFavorite(@Valid @RequestBody CreateReq dto) {
        return this.service.createFavorite(dto.div(), dto.keyCd());
    }

}
