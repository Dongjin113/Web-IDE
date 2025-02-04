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

## 👀 ERD
![image](https://github.com/Dongjin113/Web-IDE/assets/104759062/b74b7132-cc93-4d37-a72d-69d09f54292c)

## ❌ Exception Response 전략
### ApiResponse
Exception과 Response 응답모두 같은 ApiResponse 추상 클래스를 반환
   - Status와 T 타입의 필드를 선언
     ```
      @Getter
      public class ApiResponse<T> {
      
          private Status status;
      
          private T data;
      
          public ApiResponse(ErrorType errorCode) {
              this.status = new Status(errorCode);
          }
      
          public ApiResponse(ErrorType errorCode, T data) {
              this.status = new Status(errorCode);
              this.data = data;
          }
      }
     ```
      - Status
        1. 정상 응답시 200의 상태코드와 Success라는 성공메시지를 반환
        2. Error 발생 시 프론트와 약속한 Error 코드와 에러 메시지 내용을 반환
           
      - T 타입
        1. 타입을 유연하게 받아오기위해 Generic T 타입으로 선언 ( 정상 응답시 요청한 데이터를 담아서 반환 )
        2. Error 발생 시 에러에 대한 추가정보가 필요할 시 담아서 반환  

  ### Exception 전략

  #### 1. 타입을 유연하게 쓰기위한 부모 인터페이스의 ErrorType 
  ```
    public interface ErrorType {
        HttpStatus getStatusCode();    
        String getCode();
        String getMessage();
    }
  ```
  #### 2. CustomException의 부모 추상 클래스
  총 세개의 타입을 정의해 두었다
  1. 정의해둔 ErrorCode Type만 받는 생성자  
  2. ErrorCode Type과 변경할 Message를 받는 생성자  
  3. ErrorCode Type과 에러에대한 추가정보를 받을 ErrorData
  ```
  @Getter
  public abstract class CustomException extends RuntimeException {
  
      private final ErrorType errorCode;
      private ErrorData errorData;
  
      public CustomException(ErrorCode errorCode) {
          super(errorCode.getMessage());
          this.errorCode = errorCode;
      }
  
      public CustomException(ErrorCode errorCode, String message) {
          super(errorCode.changeMessage(message).getMessage());
          this.errorCode = errorCode.changeMessage(message);
      }
  
      public CustomException(ErrorCode errorCode, ErrorData errorData) {
          super(errorCode.getMessage());
          this.errorCode = errorCode;
          this.errorData = errorData;
      }
  }
  ```
  ##### 상속받아 custom한 Exception 예시
  ```
  public class EmailAuthFailure extends CustomException {
    public EmailAuthFailure() {
        super(ErrorCode.EMAIL_AUTH_FAIL);
    }
    public EmailAuthFailure(String message) {
        super(ErrorCode.EMAIL_AUTH_FAIL, message);
    }
    public EmailAuthFailure(ErrorData errorData) {
        super(ErrorCode.EMAIL_AUTH_FAIL, errorData);
    }
  }
  ```

  #### 3. ErrorCode 에러에 대해 정의해두는 EnumClass 
  ```
  @Getter
  public enum ErrorCode implements ErrorType {
  
      SUCCESS(HttpStatus.OK, "200", "OK"),
      LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "401", "아이디와 비밀번호를 확인 해주세요"),
  
      @JsonIgnore
      private final HttpStatus statusCode;
      private final String code;
      private String message;
  
      ErrorCode(HttpStatus statusCode, String code, String message) {
          this.statusCode = statusCode;
          this.code = code;
          this.message = message;
      }
  
      public ErrorResult changeMessage( String message ) {
          return new ErrorResult(this.statusCode, this.getCode(), message);
      }
  }
  ```

  
   ### 응답 예시
   #### 정상 응답
   ```
   @PostMapping("")
    public ApiResponse<ContainerResponse> create(
            @Valid @RequestBody ContainerCreateRequest request,
            @AuthenticationPrincipal JwtUserDetails user
    ) {
        return new ApiResponse<>(
                ErrorCode.SUCCESS,
                containerService.createContainer(user.getEmail(), request)
        );
    }
   ```

   #### ErrorHandler
   ```
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ApiResponse<?> customExceptionHandler(
            HttpServletResponse response, CustomException e
    ) {
        response.setStatus(e.getErrorCode().getStatusCode().value());
        return new ErrorResponse(e.getErrorCode(), e.getErrorData());
    }
   ```




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

##### 2. Polling
주기적으로 클라이언트에서 요청을 보내서 매번 확인하는 방법으로 불필요한 요청들이 많이 올 것이라고 생각해서 탈락  

##### 3. Long Polling
요청을 보내고 응답을 받으면 바로 새로운 요청을 보내고 응답을 받을때까지 연결을 유지하는 방식  
SSE보다 리소스 사용량이 비효율적이라고 생각되어서 탈락  

##### 4. SSE(Server-Sent-Events)
WebSocket보다 간단한 구현, 단일 연결로 여러 메시지 전송 가능,  
요청이 없을 때는 연결이 유지되지 않아 서버 리소스를 효과적으로 사용할 수 있었기 때문에 선택하게 됐다.  
클라이언트와의 양방향 통신이 아닌 서버에서 클라이언트쪽으로의 여러번의 단방향 통신이  
요구사항과 딱 들어맞아서 단방향 통신이라는 에로사항은 별로 문제 되지 않았다  

#### 시퀀스 다이아그램
![image](https://github.com/Dongjin113/Web-IDE/assets/104759062/0c2f0b15-2915-4b67-95b9-71603884ef1e)

1. 로그인 페이지 아이디 비밀번호 입력 후 로그인 요청
2. 아이디 비밀번호 확인 후 AccessToken(Header)과 RefreshToken(Cookie)을 발급
3. 클라이언트에서 AccessToken을 Local Storage에 저장
4. 로그인이 필요한 요청을 보낼 시 AccessToken을 검증
5. AccessToken이 만료되었을시 클라이언트에서 RefreshToken을 통한 AccessToken 재발급 요청
6. RefreshToken을 검증 후  AccessToken을 재발급

### 2. 비밀번호 찾기
https://github.com/Dongjin113/Web-IDE/assets/104759062/37b2fd02-a5c4-4224-aae8-742a80c3cd07



## 🪙토큰 
AccessToken의 유효기간은 1분 RefreshToken의 유효기간은 2주  
AccessToken은 Local Storage에 저장하여 사용하기 때문에 보안을 강화하기 위해 유효기간을 짧게 설정했습니다.    
긴 기간을 사용하기 때문에 비교적 안전한 Cookie에 저장했고, httpOnly 속성을 사용하여 JavaScript로의 직접적인 접근을 방지했습니다.

### 검증
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

#### 이메일 인증 검증 순서
1. 클라이언트에서 이메일 인증 요청을 보낼때 UUID값을 생성해서 같이 보낸다.
2. 서버에 요청이 들어오면 UUID값을 통해 SSE를 연결하고 요청이 들어온 이메일로 인증 URL과 인증을위한 토큰을 발급해준다.
3. 사용자가 이메일에서 이메일인증 요청을 보내면 인증이 완료 되었다는 템플릿과 기존의 회원가입을 진행하라고 응답해준다.
4. 서버에서 이메일인증이 완료되면 원래 사용자가 이메일 인증을 진행하던 페이지에 인증이 완료되었다는 응답을 보내준다.


## ‼️ 트러블 슈팅
#### 1. 빈의 복잡한 순환참조 발생
@Bean으로 등록할 설정파일이 많아질것을 고려하여 @Configuration을 여러 class로 분리할려고 했으나  
분리한 설정파일 내에서 순환참조가 발생  

##### 해결방안
설정파일을 분리할만큼의 내용이 많지 않아 SecurityConfig 한 파일에서 관리하기로 결정 

#### 2. 이메일인증을 진행하던중 stackoveflow 발생
filter에서 manager로 들어가 인증을 하던중 provider까지 도착하지못하고 도중에 overflow가 발생

=> 예상오류 USerDetails를 두곳에서 custom해서 사용했기때문에 authenticationmanager에서 어떤 UserDetails를 어디서 정의한 놈을 쓸줄 모르겠어!! 하면서 죽기직전까지 서로를 호출하면서 무한반복하기때문에 stackOverFlow가 발생한다

authenticationmanager에게 어떤 UserDetails를 사용할지 bean으로 등록해준다

    @Bean
    public AuthenticationManager emailAuthenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(emailAuthenticationProvider()));
    }

    @Bean
    public AuthenticationManager jwtAuthenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(jwtAuthenticationProvider()));
    }
    
이렇게 두개만 등록해줬더니 primary로 누굴쓸지 모르겠어!!! 라고 에러를 방출해서 

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
커스텀한 manager를 primary로 사용해주기에는 매번 다른 provider를 호출해줘야하기 때문에 기본 AuthenticationManager를 생성해서 @Primary로 등록해줬다.

그리고 filter안의 authenticationManager를 어떤 bean의 manager를 사용할것인지 @Qualifier로 어떤 manager를 쓸것인지 명시해준다

    private final AuthenticationManager authenticationManager;
    private final EmailAuthService emailAuthService;

    public EmailAuthenticationFilter(
            @Qualifier("emailAuthenticationManager") AuthenticationManager authenticationManager,
            EmailAuthService emailAuthService
    ) {
        this.authenticationManager = authenticationManager;
        this.emailAuthService = emailAuthService;
    }

@RequiredArgsConstructor를 사용한다면 @Qualifier 를 사용할수없기때문에 constructor에 직접 주입해준다.


##### 최종 변경안
Filter에 어떤 Provider쓸지 지정해준 Manager를 직접 주입해준다면 어떤 Provider를 쓸 Manager인지 Bean으로 등록할 필요가 없어
@Primary와 불필요한 @Bean 등록을 줄일 수 있었다.

    @Bean
    public EmailAuthenticationFilter emailAuthenticationFilter() throws Exception {
        return new EmailAuthenticationFilter(
                new ProviderManager(Collections
                        .singletonList(emailAuthenticationProvider()))
                , emailAuthService
                , authenticationEntryPoint
        );
    }

    @Bean
    public AccessAuthenticationFilter accessAuthenticationFilter() throws Exception {
        return new AccessAuthenticationFilter(
                new ProviderManager(Collections.singletonList(accessAuthenticationProvider()))
                , authenticationEntryPoint
                , userService
                , permitUrlList);
    }

    @Bean
    public RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter() throws Exception {
        return new RefreshTokenAuthenticationFilter(
                new ProviderManager(Collections.singletonList(refreshAuthenticationProvider()))
                , userService
                , authenticationEntryPoint
                , jwtUtils);
    }

#### 3. PostMan을 통해서는 정상 작동하던 SSE가 프론트와 연결해서 요청을 처리할때에는 실시간으로 메시지가 전송되지 않고 연결시간이 종료되어 연결이 끊어질 때 메시지가 한번에 전송되는 버그가 발생

##### 해결방안
SSE는 HTTP GET 메서드를 통해서만 클라이언트와의 서버 간의 통신을 지원하기 때문에 Post를 통해 받는 요청을 Get 요청으로 변경 

#### 4. Custom해서 사용하던 Exception을 그대로 사용했으나 Filter에서는 Custom한 Exception 로직을 타지 않음

##### 해결방안
filter에서의 에러를 핸들링 할 수 있는 AuthenticationEntryPoint를 커스텀하고 AuthenticationException을 커스텀해서 어떠한 에러가 발생했는지 직접 커스텀해서 사용했다. 

## ❓❔ 고민사항
#### 1. filter에서 사용하는 login과 controller에서 하는 login
- 구현을 하다보면 filter에서 처리하는 login과 Controller에서 처리하는 login의 유의미한 차이에 대해 고민

#### 2. refreshtoken을통해 재발급하는과정 accessToken확인후 확인실패시 refreshToken 확인 후 실패시 재로그인
- 프로젝트내에서 accessToken이 만료되면 프론트에서 요청을 보내서 refreshToken을 통해 accessToken을 다시 재발급하는데
  이때 요청을 보내지않고 accessToken이 만료된 요청이 들어온다면 refreshToken을 확인하고 자동적으로 accessToken을 발급한 후 들어온 요청을 처리하고
  새로 발급된 accessToken도 같이 발급해준다면 불필요한 요청을 줄일 수 있지 않을까?

#### 3. refreshToken을 저장하는 DB 테이블을 설계했으나 현재의 프로젝트에서는 refreshToken을 저장할 필요가 없는 것 같다
- DB 테이블 삭제

#### 4. Enum에 정의해둔 에러의 Message만 변경할려고 했으나 영구적으로 값이 변경되었다
- ErrorResult라는 class를 정의하고 메시지를 변경하면 새로운 객체를 만들어서 반환하도록 변경


## 👀 전체 기능 시연영상

### 3. 메인 UI
#### 3-1. 컨테이너 사이드바 UI
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/3fb37e9e-efb3-4edf-832d-36f9576f0caf
#### 3-2. 컨테이너 검색 기능
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/9b1e9900-3d00-4d2f-b5e0-c2d11a5b4a39
#### 3-3. 컨테이너 삭제
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/0a08d837-ecfc-4f41-a54d-c12faaa2a4cc
#### 3-4. 컨테이너 참여 유저 정보 UI
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/5f36e3b8-24a6-41cb-84f0-eae3981ec45d
#### 3-5. 컨테이너 시작
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/7fc30149-6ade-4661-8173-bfbf8b9fc738
#### 3-6. 컨테이너 고정, 링크 복사, 공개 비공개 전환
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/86ee0819-dea0-4315-8e04-840e287d3167
#### 3-7. 컨테이너 소개 정보 수정
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/37e00797-e5e1-4e0e-8686-e8ddcd389a3c
#### 3-8. 컨테이너 정렬 기능
https://github.com/Goorm-OGJG/Web-IDE/assets/93318615/093efa9d-4957-4197-aab2-9f8f33230c69

### 4. 컨테이너 생성 UI
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/a818fe47-df59-462c-8b66-e69ddd6a4c36

### 5. 코드 편집 UI
#### 5-1. 동시 편집
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/1f217c7e-af87-47e6-a619-de247b4afb24

#### 5-2. 탭 기능
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/9be2d61c-442e-4f0b-abeb-0e694ae3e357

#### 5-3. 파일 및 폴더 생성
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/0235b0ce-1abd-4c11-9059-e0c8f27bafd5

#### 5-4. 파일 및 폴더 삭제
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/11cf9bc2-7f62-4881-8382-b602269e48d5

#### 5-5. 파일 및 폴더 이름 변경
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/11f91211-c958-4d7b-bf17-276adacd20f2

#### 5.6. 채팅
https://github.com/Goorm-OGJG/Web-IDE/assets/79975172/25a6a2f2-7449-484b-aaea-36f462224037

## 원본 레포 => https://github.com/Goorm-OGJG/Web-IDE
