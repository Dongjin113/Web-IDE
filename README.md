![image](https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/00b55253-aeef-4f34-a789-6b2349613e59)


# [OGJG IDE - 배포 링크]()
여러 사람이 실시간으로 협업하여 코드를 작성할 수 있는 웹 기반 통합 개발 환경(Web IDE) 입니다.  
  
기성 Web IDE를 사용하여 온라인 협업 시 음성 채팅 기능이 없어  
디스코드, Zoom 등 다른 플랫폼을 추가적으로 사용해야만 한다는 불편함을 해결하고자  
음성 채팅 기술을 도입하였습니다.

## 기대효과
- 생산성 향상  
동시에 여러 사용자가 작업을 할 수 있으므로 작업 속도가 빨라진다.  
코드 리뷰나 수정을 실시간으로 할 수 있어서 피드백 추가 주기가 단축된다.  

- 협업의 용이성  
팀원 간의 협업이 원활해지며, 실시간으로 코드를 공유하고 토의할 수 있다.  

- 교육 및 멘토링 효과  
신입 개발자나 학습자에게 실시간으로 가이드를 제공할 수 있어서 교육 효과가 증대된다.  
문제 상황을 실시간으로 공유하며 해결 방법을 찾을 수 있다.

## 📆 기간
2023.09.06 ~ 2023.10.11

## 🏃 팀 구성
### Frontend
- [김준서](https://github.com/narcoker)  
- [한승재](https://github.com/stat1202)  
- [조재균](https://github.com/stat1202)  
- [한세라](https://github.com/hansera)  

### Backend
- [안병규](https://github.com/bstaran)  
- [이정준](https://github.com/dunowljj)  
- [이동진](https://github.com/Dongjin113)

## 개발 프로세스
![image](https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/03fa11ce-6f6b-4663-a933-b5470cc34f3a)

## 아키텍쳐
![image](https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/2e916f9f-e9c0-429c-b6d8-99f2bd9ed37a)

## 🔎 UI 및 기능
### 1. 회원가입 UI
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/1034d809-5a2d-4bb0-a8a0-e126aa4dd71f

#### 시퀀스 다이아그램
![image](https://github.com/Dongjin113/Web-IDE/assets/104759062/235f4bab-78ef-4238-9d4b-da73cf4b6d13)

1. 클라이언트에서 이메일을 입력한 후 인증 요청 후 이메일 발송 중이라는 메시지 출력
2. 서버에서 SSE 연결 후 이메일로 인증토큰값이 담긴 요청 URL이 발송
3. 서버에서 클라이언트로 이메일 발송이 완료되었다는 응답을 반환
4. 사용자가 이메일인증 완료시 서버에서 인증 토큰을 확인
5. 토큰 인증이 완료되면 인증이 완료되었다는 응답값을 클라이언트로 반환

#### SSE(Server-Sent Events) 선택 이유
이메일 인증요청을 보냈을때 인증이 완료되면 자동적으로 인증이 완료되었다는 메시지를 출력되기를 원했다.  
보통 Http요청은 한번의 요청에 한번의 응답만을 보내기 때문에 인증 메시지가 발송되었다는 값만을 보낼 수 있어  
어떻게 하면 한번의 요청에 여러번의 메시지를 보낼 수 있을까?

##### 1. WebSocket
SSE와 마지막까지 고민했지만 기간내에 구현하기에 SSE보다 구현과 설정이 복잡하고,  
양방향통신까지 필요한 것이 아닌 서버에서 클라이언트로 보내는 단방향 통신만 필요로 하기에 과하다고 생각했다  

##### 2. Pulling
주기적으로 클라이언트에서 요청을 보내서 매번 확인하는 방법으로 불필요한 요청들이 많이 올 것이라고 생각해서 탈락  

##### 3. Long Polling
요청을 보내고 응답을 받으면 바로 새로운 요청을 보내고 응답을 받을때까지 연결을 유지하는 방식  
SSE보다 리소스 사용량이 비효율적이라고 생각되어서 탈락  

##### 4. SSE(Server-Sent-Events)
WebSocket보다 간단한 구현, 단일 연결로 여러 메시지 전송 가능,  
요청이 없을 때는 연결이 유지되지 않아 서버 리소스를 효과적으로 사용할 수 있었기 때문에 선택하게 됐다.  
클라이언트와의 양방향 통신이 아닌 서버에서 클라이언트쪽으로의 여러번의 단방향 통신이  
요구사항과 딱 들어맞아서 단방향 통신이라는 에로사항은 별로 문제 되지 않았다  

### 트러블 슈팅
PostMan을 통해서는 정상 작동하던 SSE가 프론트와 연결해서 요청을 처리할때에는
실시간으로 메시지가 전송되지 않고 연결시간이 종료되어 연결이 끊어질 때 메시지가 한번에 전송되는 버그가 발생

#### - 해결방법
SSE는 HTTP GET 메서드를 통해서만 클라이언트와의 서버 간의 통신을 지원하기 때문에 Post를 통해 받는 요청을 Get 요청으로 변경 

### 2. 로그인 UI
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/0f7d74d4-c6d6-42db-b779-b37957fc71cf

#### 시퀀스 다이아그램
![image](https://github.com/Dongjin113/Web-IDE/assets/104759062/0c2f0b15-2915-4b67-95b9-71603884ef1e)

1. 로그인 페이지 아이디 비밀번호 입력 후 로그인 요청
2. 아이디 비밀번호 확인 후 AccessToken(Header)과 RefreshToken(Cookie)을 발급
3. 클라이언트에서 AccessToken을 Local Storage에 저장
4. 로그인이 필요한 요청을 보낼 시 AccessToken을 검증
5. AccessToken이 만료되었을시 클라이언트에서 RefreshToken을 통한 AccessToken 재발급 요청
6. RefreshToken을 검증 후  AccessToken을 재발급


## 🪙토큰 
AccessToken의 유효기간은 1분 RefreshToken의 유효기간은 2주  
AccessToken은 Local Storage에 저장하여 사용하기 때문에 보안을 강화하기 위해 유효기간을 짧게 설정했습니다.    
긴 기간을 사용하기 때문에 비교적 안전한 Cookie에 저장했고, httpOnly 속성을 사용하여 JavaScript로의 직접적인 접근을 방지했습니다.

###검증
#### AccessToken의 검증순서
1. Filter에서 검증이 필요한 요청인지 확인 검증이 필요하지 않은 요청이라면 filter가 실행되지 않는다
2. 검증이 필요하다면 검증을 위한 AbstractAuthenticationToken을 상속받은 객체에 AccesToken을 담은 후
3. AuthenticationManager를 통해 어떠한 Provider를 통해 검증을 진행할 것인지 찾은 후
4. AuthenticationProvider를 상속받아 구현한 Provider에서 토큰의 유효성을 검토한 후
5. 검증이 완료되었다면 정상실행이되고 인증에 실패했다면 인증에 실패했다는 예외를 발생시켜준다

#### RefreshToken의 검증 순서
1. Accesstoken의 기간이 만료되면 클라이언트에서 token으로 요청을 보낸다
2. 요청이 들어오면 RefreshTokenAuthenticationFilter -> AuthenticationManager -> RefreshAuthenticationProvider -> RefreshTokenAuthenticationFilter 순으로 검증 후
3. RefreshToken이 유효하다면 AccessToken을 재발급해 클라이언트에 보내준다



