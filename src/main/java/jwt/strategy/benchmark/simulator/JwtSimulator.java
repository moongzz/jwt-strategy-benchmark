package jwt.strategy.benchmark.simulator;

import jwt.strategy.benchmark.dto.SimulationRequestDto;
import jwt.strategy.benchmark.dto.SimulationResultDto;

import java.util.*;

public class JwtSimulator {
    private final SimulationRequestDto request;
    private final Set<Token> activeTokens = new HashSet<>();
    private final Set<Token> blacklistedTokens = new HashSet<>();
    private int time = 0;

    public JwtSimulator(SimulationRequestDto request) {
        this.request = request;
    }

    public record Token(String value, int createdAt, int expiresAt) {}

    private String generateTokenValue() {
        return "jwt_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
    }

    private Token newToken(int createdAt) {
        return new Token(generateTokenValue(), createdAt, createdAt + request.tokenLifetime());
    }

    public SimulationResultDto simulate(int hours) {
        List<Integer> whitelistCounts = new ArrayList<>();
        List<Integer> blacklistCounts = new ArrayList<>();
        List<Integer> timestamps = new ArrayList<>();

        for (int i = 0; i < request.userCount() * request.sessionsPerUser(); i++) {
            activeTokens.add(newToken(time));
        }

        int totalSteps = hours * 60;
        Random random = new Random();

        for (int step = 0; step < totalSteps; step++) {
            time = step * 60;   // 초 단위

            // 만료된 토큰 제거
            activeTokens.removeIf(t -> t.expiresAt() <= time);
            blacklistedTokens.removeIf(t -> t.expiresAt() <= time);

            int activeUsers = (int) (request.userCount() * (0.7 + random.nextDouble() * 0.3));

            // 로그인
            if (random.nextDouble() < request.loginRate()) {
                int newTokens = (int) (activeUsers * request.loginRate() * request.sessionsPerUser());
                for (int i = 0; i < newTokens; i++) {
                    activeTokens.add(newToken(time));
                }
            }

            // 로그아웃
            if (random.nextDouble() < request.logoutRate()) {
                int tokensToInvalidate = (int) (activeTokens.size() * request.logoutRate());
                List<Token> tokenList = new ArrayList<>(activeTokens);
                Collections.shuffle(tokenList);
                for (int i = 0; i < tokensToInvalidate && i < tokenList.size(); i++) {
                    Token token = tokenList.get(i);
                    activeTokens.remove(token);
                    blacklistedTokens.add(token);
                }
            }

            // 매시간 기록
            if (step % 60 == 0) {
                whitelistCounts.add(activeTokens.size());
                blacklistCounts.add(blacklistedTokens.size());
                timestamps.add(step / 60);
            }
        }

        return SimulationResultDto.builder()
                .blacklistCounts(blacklistCounts)
                .whitelistCounts(whitelistCounts)
                .timestamps(timestamps)
                .metrics(calculateMetrics())
                .build();
    }

    public SimulationResultDto.Metrics calculateMetrics() {
        int tokenSize = 256; // 토큰 1개의 메모리 크기 (bytes)

        // 메모리 계산 (KB)
        int whitelistMemoryKB = activeTokens.size() * tokenSize / 1024;
        int blacklistMemoryKB = blacklistedTokens.size() * tokenSize / 1024;

        // 검증 시간 계산 (ms)
        double whitelistTimeMs = Math.log1p(activeTokens.size()) * 10; // 로그 기반으로 안정적 계산
        double blacklistTimeMs = Math.log1p(blacklistedTokens.size()) * 10;

        return new SimulationResultDto.Metrics(
                activeTokens.size(), whitelistMemoryKB, whitelistTimeMs,
                blacklistedTokens.size(), blacklistMemoryKB, blacklistTimeMs
        );
    }


}
