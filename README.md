# 🔐 JWT Strategy Benchmark Simulator

JWT 인증에서 **화이트리스트(Whitelist)** vs **블랙리스트(Blacklist)** 전략의 효율성을 비교하기 위한 시뮬레이션 도구입니다.  
사용자 수, 토큰 수명, 세션 수, 로그인/로그아웃 비율 등 다양한 조건을 설정하여 **토큰 관리 방식의 성능 차이**를 시각적으로 분석할 수 있습니다.
- 이 시뮬레이터는 Claude 대화형 아티팩트를 기반으로 구현되었습니다.

---

## ✨ 주요 기능

- **시뮬레이션 실행**
    - 사용자 수, 토큰 수명, 세션 수, 로그인/로그아웃 비율을 입력하여 환경 설정
    - 실제 서비스 트래픽과 유사한 시나리오로 토큰 발급/만료/무효화 과정을 시뮬레이션

- **화이트리스트 vs 블랙리스트 비교**
    - 각 전략별 토큰 수, 메모리 사용량(KB), 검증 시간(ms) 측정
    - 시간 경과에 따른 토큰 수 변화 그래프 시각화

---

## 📂 프로젝트 구조

jwt-strategy-benchmark  
┣ 📂 controller  
┃ ┗ JwtSimulationController.java # 시뮬레이션 API 엔드포인트  
┣ 📂 simulator  
┃ ┗ JwtSimulator.java # 핵심 시뮬레이션 로직  
┣ 📂 dto  
┃ ┣ SimulationRequestDto.java # 요청 DTO  
┃ ┗ SimulationResultDto.java # 응답 DTO  
┣ 📄 resources/static/index.html # 시뮬레이션 UI  
┗ ...


---
## 📊 시뮬레이션 알고리즘
### 시뮬레이션 과정

1. 초기 설정: 사용자 수와 세션 수에 따른 초기 토큰 생성
2. 24시간 시뮬레이션: 매분마다 다음 이벤트들을 확률적으로 실행
   - 토큰 자연 만료 처리
   - 새로운 로그인 (토큰 생성)
   - 로그아웃 (토큰 무효화)
   - 토큰 갱신 (기존 토큰 무효화 + 새 토큰 생성)
3. 결과 계산: 메모리 사용량, 검증 시간, 효율성 점수 산출

### 핵심 로직
```javascript
// 화이트리스트: 현재 활성 토큰만 저장
activeTokens = Set(현재_사용중인_토큰들)

// 블랙리스트: 무효화되었지만 아직 만료되지 않은 토큰들 누적
blacklistedTokens = Set(무효화된_토큰들)
```
---
## 🚀 실행 방법

### 1) 프로젝트 클론
```bash
git clone https://github.com/your-repo/jwt-strategy-benchmark.git
cd jwt-strategy-benchmark
```
2) 서버 실행
```
./gradlew bootRun
```
3) 웹 UI 접속

브라우저에서 http://localhost:8080 접속

시뮬레이션 환경을 설정 후 🚀 시뮬레이션 실행 버튼 클릭


---

## 테스트 진행 흐름

### 1. 사용자 입력 단계
### 2. 시뮬레이션 실행 버튼 클릭
runSimulation() 함수 호출
-> JwtSimulator 객체 생성
-> simulate(72) 메서드 실행
### 3. 초기 상태 설정 (시뮬레이션 시작)
```
// 1000명 * 2세션 = 2000개 초기 토큰 생성
for (let i = 0; i < 2000; i++) {
    activeTokens.add({
        token: "jwt_abc123_1234567890",
        createdAt: 0,
        expiresAt: 3600
    });
}
```
### 4. 24시간 시뮬레이션 루프
```
// totalSteps 동안 매분마다 실행
for (int step = 0; step < totalSteps; step++) {
    time = step * 60;   // 초 단위
    
    // 단계별 처리:
    // 1) 만료된 토큰 자동 제거
    activeTokens = activeTokens.filter(t => t.expiresAt > currentTime);
    blacklistedTokens = blacklistedTokens.filter(t => t.expiresAt > currentTime);
    
    // 2) 로그인 시뮬레이션 (새 토큰 생성)
    if (Math.random() < 0.1) {  // 10% 확률
        // 새 토큰들 activeTokens에 추가
    }
    
    // 3) 로그아웃 시뮬레이션 (토큰 무효화)
    if (Math.random() < 0.05) {  // 5% 확률
        // activeTokens에서 제거 → blacklistedTokens에 추가
    }
    
    // 4) 토큰 갱신 시뮬레이션
    // 5) 매시간 결과 기록
}
```
### 5. 결과 수집 및 계산
```
// 24시간 후 최종 상태:
activeTokens.size = 1500개 (예시)
blacklistedTokens.size = 800개 (예시)
// 메트릭 계산:
whitelistMemory = 1500 × 256바이트 = 384KB
blacklistMemory = 800 × 256바이트 = 204.8KB
```
### 6. 결과 화면 표시
```
// DOM 업데이트
document.getElementById('whitelistTokens').textContent = "1,500";
document.getElementById('whitelistMemory').textContent = "384";
document.getElementById('blacklistTokens').textContent = "800";
document.getElementById('blacklistMemory').textContent = "205";
```
### 7. 차트 그리기
Canvas에 24시간 동안의 토큰 수 변화 그래프 그리기  
파란선: 화이트리스트 토큰 수  
빨간선: 블랙리스트 토큰 수