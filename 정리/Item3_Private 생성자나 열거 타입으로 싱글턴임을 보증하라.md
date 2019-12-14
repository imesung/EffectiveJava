## Private 생성자나 열거 타입으로 싱글턴임을 보증하라

### 싱글턴이란,

- 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.

- 싱글턴을 만드는 방식 두 가지

  - 두 가지 모두 생성자는 private으로 감춰두고, 유일한 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 하나 마련해둔다.

- 첫번째 방식

  ```JAVA
  //public static final 필드 방식의 싱글턴
  public class Elvis {
      public static final Elvis INSTANCE = new Elvis();
      private Elvis(){...};
  }
  ```

  - private 생성자는 public static final 필드인 Elvis.INSTANCE를 초기화할 때 딱 한번만 호출된다.
  - public 이나 protected(같은 패키지 혹은 상속받을 시 접근 가능) 생성자가 없으므로 Elvis 클래스가 초기화될 때 만들어진 인스턴스가 전체 시스템에서 하나뿐임을 보장한다. 이는, 클라이언트가 손 쓸 방법이 없다는 얘기다.
  - but, 예외가 있는데.. 이는 권한이 있는 클라이언트는 리플렉션 API(아이템 65)인 AccessibleObject.setAccessible을 사용해 private생성자를 호출할 수 있다.
  - 예외를 방어하려면, 생성자를 수정하여 두 번째 객체가 생성되려할 때 예외를 던지게 하면 된다.
  - 장점
    - 해당 클래스가 싱글턴임이 API에 명백히 드러나 있다.
    - 간결함이다.

- 두번째 방식

  ```java
  //정적 팩토리 방식의 싱글턴
  public class Elvis {
      private static final Elvis INSTANCE = new Elvis();
      private Elvis(){...}
      public static Elvis getInstance() {
          return INSTANCE;
      }
  }
  ```

  - Elvis.getInstacne는 항상 같은 객체의 참조를 반환하므로 제2의 Elvis 인스턴스란 결코 만들어지지 않다(이 또한, 리플렉션을 통한 예외는 똑같이 적용됨)
  - 장점
    - API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 있다.
    - 원한다면 정적 팩토리를 제네릭 싱글턴 팩토리로 만들 수 있다.(아이템 30)
      - 싱글턴 부분을 팩토리로 만들어 팩토리 접근시에만 객체 생성할 수 있도록 하겠다.
    - 정적 팩토리의 메서드 참조를 공급자(supplier)로 사용할 수 있다.
      - Elvis::getInstance를 Supplier<Elvis>로 사용하는 식이다(아이템 43, 44)

- 세번째 방법

  - private이 아닌 enum 타입 방식의 싱글턴을 말한다. 애가 가장 바람직한 방법이다.

  - ```java
    public enum Elvis {
        INSTANCE;
    }
    
    //사용
    Single single = Elvis.INSTANCE;
    ```

- 대부분 상황에서는 **원소가 하나뿐인 열거 타입**이 싱글턴을 만드는 가장 좋은 방법이다.

- 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 이 방법은 사용할 수 없다.(열거 타입이 다른 인터페이스를 구현하도록 선언 할 수는 있다.)