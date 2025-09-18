package jwt.strategy.benchmark.dto;

public record SimulationRequestDto (
    int userCount,
    int tokenLifetime,
    int sessionsPerUser,
    double loginRate,
    double logoutRate
) {}
