# cafe-service
![image](https://user-images.githubusercontent.com/117132858/209910638-ecf12a62-f5e0-49c1-af03-21c9c5069764.png)
> 고객이 어플을 통한 카페 메뉴를 주문하는 서비스입니다.
* * *
## 서비스 시나리오
* 기능적 요구사항

  고객이 메뉴를 선택하여 주문한다.

  시스템이 결제한다.

  결제가 완료되면 카페에 전달된다.

  제조 전에 고객은 주문을 취소 할 수 있다.

  제조 전에 카페에서 주문을 취소 할 수 있다.

  주문이 승인되면 제조를 시작한다.

  주문이 취소되면 결제를 취소한다.

  카페에서 주문이 승인되면 카페에서 제조를 시작한다.

  고객은 주문 상태를 조회할 수 있다.

  주문 상태를 카카오톡으로 고객에게 알림을 보낸다.

  고객은 결제, 주문 이력을 조회할 수 있다.

* 비기능적 요구사항

  - 트랜잭션
  
    결제가 되지 않은 주문건은 아예 거래가 성립되지 않아야 한다 Sync 호출
    
  - 장애격리
  
    알림 시스템에 장애가 발생해도 주문은 업무 시간 내 운영되어야 한다.
    
    결제 시스템 과부하 시 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다.
    
  - 성능	
  
    주문/메뉴 정보를 확인할 수 있어야 한다.
    
    주문 상태 알림
    
* * *
## Event Storming 결과

* 모델 완성
![image](https://user-images.githubusercontent.com/15317158/209916428-b615afe8-a3c0-4016-b934-a3f0b832d13d.png)

* * *
## 구현
   ### 1. Saga (Pub-Sub) / 3. Correlation
Cafe 프로젝트에서는 PolicyHandler에서 처리 시 어떤 건에 대한 처리인지를 구별하기 위한 Correlation-key 구현을 이벤트 클래스 안의 변수로 전달받아 서비스간 연관된 처리를 정확하게 구현하고 있습니다.

아래의 구현 예제를 보면

주문(Order)을 하면 동시에 연관된 결제(Payment) 서비스를 동기호출하면 외부 시스템인 PG(데모로 구현한)에서 전달받은 승인코드등 결제(Payment)서비스에서 상태가 적당하게 변경되고, 또 카페(Cafe) 서비스의 Aggregation에 pub/sub하여 주문 데이터가 적절하게 상호작용하고 있음을 알 수 있습니다. 이 때 Correlation-key인 orderId 속성을 연계되는 각 이벤트 클래스에 정의하여 예약취소나 커피생산시작 등의 명령에 유기적으로 상태를 변화시키고 있음을 알 수 있습니다.

- 고객이 주문 Post 후 상태

  ![image](https://user-images.githubusercontent.com/15317158/210028743-567663a5-a0c3-40ba-abcf-517798af8c45.png)


- 결재 동기 호출 후 상태

  ![image](https://user-images.githubusercontent.com/15317158/210028804-0d88468b-b444-4c79-87e4-feeefb24e49c.png)


- CafeOrder 상태
  ![image](https://user-images.githubusercontent.com/15317158/210028846-92360cdc-39c9-45b4-930f-f2edf6664529.png)

- Cafe서비스에서 주문 취소 후 상태

  ![image](https://user-images.githubusercontent.com/15317158/210028922-88000b03-fe87-4500-ba01-e96c7837f1e6.png)


- CafeOrder 상태
  ![image](https://user-images.githubusercontent.com/15317158/210028953-40414162-f8a5-45c7-9679-69812616f54d.png)


- Payment 상태
 
  ![image](https://user-images.githubusercontent.com/15317158/210028986-ab0a241f-ca3e-4720-bda2-d19194b5d7b5.png)


- OrderList 상태
  ![image](https://user-images.githubusercontent.com/15317158/210029022-3d67f984-6f83-4466-a947-c5717823f920.png)

- Kafka 메세지 로그
  ![image](https://user-images.githubusercontent.com/15317158/210029170-f719a4c6-2d7f-42f4-af50-2a7541ebe87e.png)

## 2. CQRS
- 주문(Order)의 Initial/OrderPlaced/Paid/PaymentCanceled/OrderApproved 등 총 9개 Status 를 포함한 주문 상세 정보를 고객(Customer)이 조회 할 수 있도록 OrderList를 CQRS 로 구현하였다.
- 주문(Order),결제(Payment),카페(Cafe) 서비스의 개별 Aggregate Status 를 통합 조회하여 성능 Issue 를 사전에 예방할 수 있다.
- 비동기식으로 처리되어 발행된 이벤트 기반 Kafka 를 통해 수신/처리 되어 별도 Table 에 관리한다

- Table 모델링 (OrderList)

![image](https://user-images.githubusercontent.com/15317158/210027667-446db092-b9bb-439e-a896-fefe31fdc114.png)

viewpage OrderListViewHandler.java 를 통해 구현 (OrderPlaced/OrderApproved/OrderCanceled/Paid/PaymentCanceled 등 주문 상태 변화에 따른 이벤트 발생 시, Pub/Sub 기반으로 별도 OrderList 테이블에 저장)
![image](https://user-images.githubusercontent.com/15317158/210028006-1e0bce8c-eade-4707-b216-73ac578303ac.png)
- 실제로 OrderList에서 각 주문의 상태를 확인할 수 있다.
![image](https://user-images.githubusercontent.com/15317158/210028164-709788a3-d762-49f5-ad49-e4dfff03ada9.png)

    
## 4. Request-Response
    카페 시스템은 주문(order) -> 결제(payment)간의 호출을 동기식으로 구현하여 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다.
    
- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현    
  ![image](https://user-images.githubusercontent.com/117251976/210033542-0a6547ab-a4c8-43c0-8217-5eccdf4d6192.png)
    
- 주문을 받은 직후(@PostPersist) 결제를 요청하도록 처리   
  ![image](https://user-images.githubusercontent.com/117251976/210033642-e18254f3-594f-4f5a-9905-8e7fe2b9e2a5.png)
  
- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인
  step1. payment 서비스를 종료시킨다
   ![image](https://user-images.githubusercontent.com/117251976/210033798-a861054a-3fb3-43a3-ab8a-223016566d16.png)
  step2. 주문처리를 실행한다. -> 실패
   ![image](https://user-images.githubusercontent.com/117251976/210033879-085a4726-bb23-41f8-ab60-851e37824029.png)
  step3. payment 서비스를 다시 시킨 후 주문시 정상 처리됨
   ![image](https://user-images.githubusercontent.com/117251976/210034212-7ad9029b-b488-4e57-bb97-c83ba0a28c94.png)
  - 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다. (서킷브레이커, 폴백 처리를 아래에서 설명)
   
   
## 5. Circuit Breaker
    Istio 를 사용 경우 (Timeout)

    스프링클라우드 - Hystrix
## 6. GateWay / Ingress
    JWT 인증

## 7. Deploy / Pipeline
- docker image 생성 및 push
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/209923107-34ac1d90-9169-40d6-bc3b-3756e257ba47.png">

- gateway 확인
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/210023626-ab2501b1-7e8e-4aba-a77d-1aaa1aeb164e.png">

- 주문 확인
<img width="1060" alt="image" src="https://user-images.githubusercontent.com/117134765/210024236-d1ebf6cc-b38d-4255-8dd4-73f3f2bd6ee4.png">
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/210024287-e25264cf-e78c-4c0c-990f-10cdde0e9ee3.png">


## 8. Autoscale (HPA)
- cpu 할당 : cafe: 200m,  order: 300m,  payment: 500m
![image](https://user-images.githubusercontent.com/117134765/210024507-31f25c77-94e0-4e9a-a38d-4b8ecc2fbed7.png)

- cafe/order/payment 각각 cpu 사용률 20/30/50% 초과 시, replica 2개까지 생성한다
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/209918735-0eeb9b81-4225-4b66-9806-6d6584ef9c47.png">

- 부하 테스트용 pod 생성 및 수행
<img width="600" alt="image" src="https://user-images.githubusercontent.com/117134765/210025051-4c69433a-190e-4bea-85cf-3d05aa6362e1.png">

- 해당 서비스 사용률 및 pod 증가 확인
<img width="1000" alt="image" src="https://user-images.githubusercontent.com/117134765/209922356-fb7c6cd6-f96b-4ecc-994a-4f39751facf1.png">


## 9. Zero-downtime deploy (Readiness probe)
- Zerodowntime deployment.yaml 설정

  <img width="400" alt="image" src="https://user-images.githubusercontent.com/117134765/210025329-82811e45-a4d8-48cb-afa9-88bede92e482.png">
  
- 부하 발생 후, 무정지 서비스 로그 확인
  <img width="800" alt="image" src="https://user-images.githubusercontent.com/117134765/210025426-71000908-d656-4320-971a-04e9be53e82d.png">
  
  
## 10. Persistence Volume
- EFS 생성
  
   ![image](https://user-images.githubusercontent.com/117131418/209910291-f4870d6f-f96a-485b-882f-5ae6a088ddf6.png)
  
- EFS 계정 생성 및 ROLE 바인딩 : efs-sa.yml, efs-rbac.yml
- EFSS Provisioner 배포 : efs-provisioner.yml
- 설치한 Provisioner를 storageclass에 등록 : efs-storageclass.yml
   
   ![image](https://user-images.githubusercontent.com/117131418/209910812-03ddc627-accf-4ba3-b88e-ba1ce918562f.png)
  
- PVC(PersistentVolumeClaim) 생성 : volume-pvc.yml
   
   ![image](https://user-images.githubusercontent.com/117131418/210026183-5f2ae8d3-6112-4cba-8892-ae3914a0a5d1.png)
  
 - order pod 적용
   
   ![image](https://user-images.githubusercontent.com/117131418/210025998-518ece50-302a-4c0e-99d8-b09fe549a3e0.png)
   ![image](https://user-images.githubusercontent.com/117131418/210026012-4509afa1-ece4-42aa-885f-3427eae96b52.png)


  - A pod에서 마운트된 경로에 파일을 생성하고 B pod에서 파일을 확인함
  
   A Pod에서 파일 생성
   
   ![image](https://user-images.githubusercontent.com/117131418/210026034-ff807ad5-fb22-437c-9973-f54ecbbdf8ef.png)   
   ![image](https://user-images.githubusercontent.com/117131418/210026067-238824fd-1767-49e2-a854-a47de436c6a8.png)
   
   B pod에서 파일 확인
   
   ![image](https://user-images.githubusercontent.com/117131418/210026087-9ae48f11-ea79-4f29-ab54-0f594fb9d1d1.png)
   ![image](https://user-images.githubusercontent.com/117131418/210026098-efddfac2-6724-4d96-9846-d15f4e6ed5ba.png)


 ## 11. Self-healing (liveness probe)
 - order deployment.yml 파일 수정
    컨테이너 실행 후 /tmp/healthy 파일을 만들고 
    90초 후 삭제
    livenessProbe에 'cat /tmp/healthy'으로 검증하도록 함
      
   ![image](https://user-images.githubusercontent.com/117131418/209911192-ddd4d65c-f80c-4217-9e05-a280359e9981.png)
     
  - kubectl describe pod order 실행으로 확인
    컨테이너 실행 후 90초 동인은 정상이나 이후 /tmp/healthy 파일이 삭제되어 livenessProbe에서 실패를 리턴하게 됨
    pod 정상 상태 일때 pod 진입하여 /tmp/healthy 파일 생성해주면 정상 상태 유지됨
    >> 배포 후 테스트해서 캡쳐(아래는 airbnb 예시)
      
   ![image](https://user-images.githubusercontent.com/117131418/209911538-9be624d4-4345-4a1d-96bd-148b8d8c0fe0.png)
      
 ## 12. Loggregation
  EFK Stack으로 배포된 마이크로 서비스에 대한 통합 로깅
  
  ![image](https://user-images.githubusercontent.com/117131418/209936133-9aebe28a-413a-4485-84a1-988812083b11.png)

  
  

