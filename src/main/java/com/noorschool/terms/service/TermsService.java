package com.noorschool.terms.service;

import com.noorschool.auth.model.dto.AuthTokenResponseDTO;
import com.noorschool.auth.model.dto.OAuth2UserProfileDTO;
import com.noorschool.auth.security.cookie.AuthCookieService;
import com.noorschool.auth.service.JwtService;
import com.noorschool.auth.service.RefreshTokenService;
import com.noorschool.common.exception.BusinessException;
import com.noorschool.common.model.vo.ResultCode;
import com.noorschool.member.model.dto.MemberDTO;
import com.noorschool.member.model.dto.MemberUpsertDTO;
import com.noorschool.member.model.vo.MemberRoleType;
import com.noorschool.member.service.MemberService;
import com.noorschool.terms.mapper.TermsMapper;
import com.noorschool.terms.model.dto.TermsAgreementRequestDTO;
import com.noorschool.terms.model.dto.TermsDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsMapper termsMapper;
    private final MemberService memberService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthCookieService authCookieService;

    public List<TermsDTO> getActiveTerms() {
        return termsMapper.selectActiveTerms();
    }

    /**
     * 신규 회원 약관 동의 완료 처리.
     * TB_MEMBER → TB_TERMS_AGREEMENT → TB_REFRESH_TOKEN 을 단일 트랜잭션으로 처리하며,
     * 하나라도 실패하면 전체 롤백된다.
     */
    @Transactional
    public AuthTokenResponseDTO completeSignup(TermsAgreementRequestDTO request, HttpServletResponse httpResponse) {
        // 1. signupToken 검증 및 OAuth 프로필 추출
        OAuth2UserProfileDTO profile = jwtService.extractSignupProfile(request.getSignupToken());

        // 2. 이미 가입된 계정인지 재확인 (중복 요청 방어)
        if (memberService.findByGoogleSub(profile.getGoogleSub()) != null) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "이미 가입된 계정입니다. 다시 로그인해 주세요.");
        }

        // 3. 필수 약관 전체 동의 여부 검증
        List<TermsDTO> activeTerms = termsMapper.selectActiveTerms();
        List<Long> requiredTermsIds = activeTerms.stream()
                .filter(t -> "Y".equals(t.getIsRequired()))
                .map(TermsDTO::getTermsId)
                .toList();

        boolean allRequiredAgreed = requiredTermsIds.stream().allMatch(reqId ->
                request.getAgreements() != null &&
                request.getAgreements().stream()
                        .anyMatch(a -> reqId.equals(a.getTermsId()) && a.isAgreed())
        );

        if (!allRequiredAgreed) {
            throw new BusinessException(ResultCode.INVALID_REQUEST, "필수 약관에 모두 동의해야 합니다.");
        }

        // 4. TB_MEMBER INSERT
        memberService.upsert(MemberUpsertDTO.builder()
                .googleSub(profile.getGoogleSub())
                .email(profile.getEmail())
                .name(profile.getName())
                .nickname(profile.getName())
                .profileImage(profile.getProfileImage())
                .role(MemberRoleType.LEARNER.name())
                .status("ACTIVE")
                .build());

        MemberDTO member = memberService.findByGoogleSub(profile.getGoogleSub());
        if (member == null) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "회원 생성에 실패했습니다.");
        }

        // 5. TB_TERMS_AGREEMENT INSERT (동의한 것만)
        if (request.getAgreements() != null) {
            for (TermsAgreementRequestDTO.AgreementItem item : request.getAgreements()) {
                termsMapper.insertAgreement(member.getMemberId(), item.getTermsId(), item.isAgreed() ? "Y" : "N");
            }
        }

        // 6. TB_REFRESH_TOKEN INSERT
        String accessToken = jwtService.createAccessToken(member.getMemberId(), member.getEmail(), member.getRole());
        String refreshToken = jwtService.createRefreshToken(member.getMemberId());
        refreshTokenService.rotateRefreshToken(
                member.getMemberId(), refreshToken, jwtService.getRefreshTokenExpirationSeconds());

        // 7. 리프레시 쿠키 세팅
        httpResponse.addHeader(HttpHeaders.SET_COOKIE,
                authCookieService.createRefreshCookie(refreshToken, jwtService.getRefreshTokenExpirationSeconds()).toString());

        return AuthTokenResponseDTO.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpirationSeconds())
                .refreshTokenExpiresIn(jwtService.getRefreshTokenExpirationSeconds())
                .isNewMember(false)
                .build();
    }
}
