package com.noorschool.auth.mapper;

import com.noorschool.auth.model.dto.RefreshTokenRecordDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface RefreshTokenMapper {
    RefreshTokenRecordDTO selectActiveByToken(@Param("refreshToken") String refreshToken);

    int revokeAllByMemberId(@Param("memberId") Long memberId);

    int revokeByToken(@Param("refreshToken") String refreshToken);

    int insertRefreshToken(
            @Param("memberId") Long memberId,
            @Param("refreshToken") String refreshToken,
            @Param("expiresAt") LocalDateTime expiresAt
    );
}
