# cafe-service
![image](https://user-images.githubusercontent.com/117132858/209910638-ecf12a62-f5e0-49c1-af03-21c9c5069764.png)
> 고객이 어플을 통한 카페 메뉴를 주문하는 서비스입니다.
* * *
## 서비스 시나리오

* 기능적 요구사항

* 비기능적 요구사항

* * *
## Event Storming 결과

> 모델 완성
![image](https://user-images.githubusercontent.com/15317158/209916428-b615afe8-a3c0-4016-b934-a3f0b832d13d.png)

* * *
## 구현
  ### 1. Saga (Pub-Sub)
    장애간섭최소화

    Loosely coupled architecture
  ### 2. CQRS
    읽기 데이터 분리

    두개 이상의 다른 마이크로서비스의 데이터를 Projection(Join)하는 대시보드
  ### 3. Compensation / Correlation
    복구/취소

    해당 취소 건에 대한 필터링
  ### 4. Request-Response
    값을 참고(GET)

    order -> 주문이력을 조회
  ### 5. Circuit Breaker
    Istio 를 사용 경우 (Timeout)

    스프링클라우드 - Hystrix
  ### 6. GateWay / Ingress
    JWT 인증

  ### 7. Deploy / Pipeline
    Public 주소로 접속

    CI/CD 로 적용
  ### 8. Autoscale (HPA)
1. cpu 할당 : cafe:200m, order:300m, payment:500m
<img width="357" alt="image" src="https://user-images.githubusercontent.com/117134765/209917249-664ca4c9-51fa-4784-8d06-bec82aaae579.png">

2. cafe/order/payment 각각 cpu 사용률 20/30/50% 초과 시, replica 2개까지 생성한다
<img width="504" alt="image" src="https://user-images.githubusercontent.com/117134765/209918735-0eeb9b81-4225-4b66-9806-6d6584ef9c47.png">

  ### 9. Zero-downtime deploy (Readiness probe)

  ### 10. Persistence Volume/ConfigMap/Secret
  1. EFS 생성
  
   ![image](https://user-images.githubusercontent.com/117131418/209910291-f4870d6f-f96a-485b-882f-5ae6a088ddf6.png)
  
  2. EFS 계정 생성 및 ROLE 바인딩 : efs-sa.yml, efs-rbac.yml
  3. EFSS Provisioner 배포 : efs-provisioner.yml
  4. 설치한 Provisioner를 storageclass에 등록 : efs-storageclass.yml
   
   ![image](https://user-images.githubusercontent.com/117131418/209910812-03ddc627-accf-4ba3-b88e-ba1ce918562f.png)
  
  5. PVC(PersistentVolumeClaim) 생성 : volume-pvc.yml  -> 여기까지 진행, PVC 상태 Pending 지속
   
   ![image](https://user-images.githubusercontent.com/117131418/209910889-a4f54560-8f3e-4ae4-bbe9-16467f871a0f.png)
  
  6. order pod 적용
  7. A pod에서 마운트된 경로에 파일을 생성하고 B pod에서 파일을 확인함(airbnb 예시)

   ![image](https://user-images.githubusercontent.com/117131418/209912112-d9aa6816-e4f8-4cdc-b02a-b5e12558b093.png)
   
   ![image](https://user-images.githubusercontent.com/117131418/209912139-65c32c7f-9065-487a-a125-41466cbee58b.png)




  ### 11. Self-healing (liveness probe)
  1. order deployment.yml 파일 수정
    컨테이너 실행 후 /tmp/healthy 파일을 만들고 
    90초 후 삭제
    livenessProbe에 'cat /tmp/healthy'으로 검증하도록 함
      
   ![image](https://user-images.githubusercontent.com/117131418/209911192-ddd4d65c-f80c-4217-9e05-a280359e9981.png)
     
   2. kubectl describe pod order 실행으로 확인
    컨테이너 실행 후 90초 동인은 정상이나 이후 /tmp/healthy 파일이 삭제되어 livenessProbe에서 실패를 리턴하게 됨
    pod 정상 상태 일때 pod 진입하여 /tmp/healthy 파일 생성해주면 정상 상태 유지됨
    >> 배포 후 테스트해서 캡쳐(아래는 airbnb 예시)
      
   ![image](https://user-images.githubusercontent.com/117131418/209911538-9be624d4-4345-4a1d-96bd-148b8d8c0fe0.png)
      
  ### 12. Loggregation / Monitoring
  
  

