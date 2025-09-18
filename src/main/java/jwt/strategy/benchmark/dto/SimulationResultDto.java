package jwt.strategy.benchmark.dto;

import lombok.*;

import java.util.List;

@Builder
public record SimulationResultDto (
        List<Integer> whitelistCounts,
        List<Integer> blacklistCounts,
        List<Integer> timestamps,
        Metrics metrics
) {
    public record Metrics(
            int whitelistTokens, int whitelistMemoryKB, double whitelistTime,
            int blacklistTokens, int blacklistMemoryKB, double blacklistTime
    ) {}
}