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
  ### 1. Saga (Pub-Sub) / 3. Correlation
    Cafe 프로젝트에서는 PolicyHandler에서 처리 시 어떤 건에 대한 처리인지를 구별하기 위한 Correlation-key 구현을 이벤트 클래스 안의 변수로 전달받아 서비스간 연관된 처리를 정확하게 구현하고 있습니다.

아래의 구현 예제를 보면

주문(Order)을 하면 동시에 연관된 결제(Payment) 서비스를 동기호출하면 외부 시스템인 PG(데모로 구현한)에서 전달받은 승인코드등 결제(Payment)서비스에서 상태가 적당하게 변경되고, 또 카페(Cafe) 서비스의 Aggregation에 pub/sub하여 주문 데이터가 적절하게 상호작용하고 있음을 알 수 있습니다. 이 때 Correlation-key인 orderId 속성을 연계되는 각 이벤트 클래스에 정의하여 예약취소나 커피생산시작 등의 명령에 유기적으로 상태를 변화시키고 있음을 알 수 있습니다.



**예제 삽입 필요**


    Loosely coupled architecture
  ### 2. CQRS
- 주문(Order)의 Initial/OrderPlaced/Paid/PaymentCanceled/OrderApproved 등 총 9개 Status 를 포함한 주문 상세 정보를 고객(Customer)이 조회 할 수 있도록 OrderList를 CQRS 로 구현하였다.
- 주문(Order),결제(Payment),카페(Cafe) 서비스의 개별 Aggregate Status 를 통합 조회하여 성능 Issue 를 사전에 예방할 수 있다.
- 비동기식으로 처리되어 발행된 이벤트 기반 Kafka 를 통해 수신/처리 되어 별도 Table 에 관리한다

    
  ### 4. Request-Response
    값을 참고(GET)

    order -> 주문이력을 조회
  ### 5. Circuit Breaker
    Istio 를 사용 경우 (Timeout)

    스프링클라우드 - Hystrix
  ### 6. GateWay / Ingress
    JWT 인증

  ### 7. Deploy / Pipeline
1. docker image 생성 및 push
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/209923107-34ac1d90-9169-40d6-bc3b-3756e257ba47.png">

2. gateway 확인
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/210023626-ab2501b1-7e8e-4aba-a77d-1aaa1aeb164e.png">

3. 주문 확인
<img width="1060" alt="image" src="https://user-images.githubusercontent.com/117134765/210024236-d1ebf6cc-b38d-4255-8dd4-73f3f2bd6ee4.png">
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/210024287-e25264cf-e78c-4c0c-990f-10cdde0e9ee3.png">


  ### 8. Autoscale (HPA)
1. cpu 할당 : cafe:200m, order:300m, payment:500m
![image](https://user-images.githubusercontent.com/117134765/210024507-31f25c77-94e0-4e9a-a38d-4b8ecc2fbed7.png)

2. cafe/order/payment 각각 cpu 사용률 20/30/50% 초과 시, replica 2개까지 생성한다
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/209918735-0eeb9b81-4225-4b66-9806-6d6584ef9c47.png">

3. 부하 테스트용 pod 생성 및 수행
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/209921993-7bba448b-caf8-4a70-b107-871afc89aa77.png">

4. 해당 서비스 사용률 및 pod 증가 확인
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/209922356-fb7c6cd6-f96b-4ecc-994a-4f39751facf1.png">

  ### 9. Zero-downtime deploy (Readiness probe)
  미구현

  ### 10. Persistence Volume
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
      
  ### 12. Loggregation
  EFK Stack으로 배포된 마이크로 서비스에 대한 통합 로깅
  
  ![image](https://user-images.githubusercontent.com/117131418/209936133-9aebe28a-413a-4485-84a1-988812083b11.png)

  
  

