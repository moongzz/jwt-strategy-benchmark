package jwt.strategy.benchmark.controller;

import jwt.strategy.benchmark.dto.SimulationRequestDto;
import jwt.strategy.benchmark.dto.SimulationResultDto;
import jwt.strategy.benchmark.simulator.JwtSimulator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulation")
public class JwtSimulationController {

    @PostMapping
    public SimulationResultDto simulate(
            @RequestBody SimulationRequestDto request
    ) {
        JwtSimulator simulator = new JwtSimulator(request);
        return simulator.simulate(72);
    }
}
