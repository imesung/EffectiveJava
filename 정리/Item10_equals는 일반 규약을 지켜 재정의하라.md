## equals는 일반 규약을 지켜 재정의하라

Equals 메소드는 재정의하기 쉬워 보이지만 곳곳에 함정이 도사리고 있어 자칫하면 끔찍한 결과를 초래할 수 있다.

이런 이유로 인해 아예 재정의 안하는 것이 옳은 선택일 수 있다. 



**다음 상황에 맞닥드릴 시에는 재정의를 하지 않는 것이 좋다.**

- **각 인스턴스가 본질적으로 고유하다.** 

  - 값을 표현하는 게 아니라 동작하는 개체를 표현하는 클래스를 말한다.
  - Thread에서 Object의 equals 메소드는 이러한 클래스에 딱 맞게 구현되어있다.

- **인스턴스의 논리적 동치성을 검사할 일이 없다.**

  - 인스턴스의 동치성을 검사할 일이 없다면 Object의 기본 equals만으로 해결이 된다.

- **상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.**

  - Set구현체는 AbstractSet이 구현한 equals를 상속받앙 사용하고, List 구현체들은 AbstractList로 부터, Map 구현체는 AbstractMap으로 부터 그대로 상속받아 사용한다.

- **클래스가 private이거나 package-private(protected)이고, equals 메소드를 호출할 일이 없다.**

  - 만약 위 같은 상황에서 혹시라도 equals가 호출되는 것을 막고 싶다면 다음처럼 구현해라

    ~~~java
    @Override
    public boolean equals(Object o) {
      throw new AssertionError();	//호출 금지
    }
    ~~~

    

**그렇다면 우리는 언제 equals를 재정의해야 하는 것일까?**

- 객체 식별성(두 객체가 물리적으로 같은지 == 주소가 같은지)이 아니라 논리적 동치성(값이 같은지)을 확인해봐야 하는데, **상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의가 되지 않을 때다.**
  - 주로 String, Integer 같은 Wrapper Class(값 클래스)가 여기에 해당한다. 
  - Wrapper Class를 equals()로 사용시 주로 값이 같은지를 확인할 것이다. 그러므로, 이 때는 **재정의가 필요하며, 이로 인해 Map키와 Set원소로 사용할 수 있게 된다.**



- 하지만 만약, **값 클래스더라도 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 인스턴스(Ex. 리터럴 String)라면 equals를 재정의 하지 않아도 된다.**
  - 이 경우에서는 같은 인스턴스가 2개 이상 만들어지는 것이 아니기 때문에 **논리적 동치성과 객체 식별성이 사실상 똑같은 의미가 된다. 즉, Object의 equals가 논리적 동치성까지 확인해준다고 볼수 있다.**



**equals() 재정의 시 따라야할 일반 규약**

- **반사성(reflexivity)** : null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true이다.
- **대칭성(symmetry)** : null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true이면 y.equals(x)도 true이다.
- **추이성(transitivity)** : null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이면 y.equals(z)도 true면, x.equals(z)도 true이다.
- **일관성(consistency)** : null이 아닌 모든 참조 값 x,y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
- **null - 아님** : null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 **false**이다.

~~~java
//Integer equals
public boolean equals(Object obj) {
  if (obj instanceof Integer) {
    return this.value == (Integer)obj;
  } else {
    return false;
  }
}

//String equals
public boolean equals(Object anObject) {
  if (this == anObject) {
    return true;
  } else {
    if (anObject instanceof String) {
      String aString = (String)anObject;
      if (this.coder() == aString.coder()) {
        return this.isLatin1() ? StringLatin1.equals(this.value, aString.value) : StringUTF16.equals(this.value, aString.value);
      }
    }

    return false;
  }
}
~~~

**equals()를 재정의 할 때는 위의 규약을 반드시 따라야 하며, 이 규 약을 어길 시 프로그램이 이상하게 동작하거나 종료될 것이고, 원인이 되는 코드를 찾기도 굉장히 어려울 것이다.**

equals()는 true 혹은 false만 return 한다.



### **Object에서 말하는 동치관계란?**

Object에서 말하는 동치관계는 쉽게 말해, **서로 같은 원소들로 이뤄진 부분집합으로 나누는 연산**을 말한다. 이 부분집합을 **동치 클래스**라 한다. equals()가 쓸모 있으려면 모든 원소가 같은 동치류에 속하여 어떤 원소와도 서로 교환할 수 있어야 한다.



#### 동치 관계를 만족시키기 위한 요건

1. **반사성**은 단순히 말해 객체는 자기 자신과 같아야 한다는 뜻이다.

   - 이 요건을 어긴 클래스를 확인하려면, 해당 클래스의 인스턴스를 컬렉션에 넣고 contains()를 호출하면 인스턴스가 없다고 답할 것이다.

2. **대칭성**은 두 객체는 서로에 대한 동치 여부에 똑같이 답해야 한다는 뜻이다.

   - 아래 클래스에서 toString 메소드는 원본 문자열의 대소문자를 그대로 돌려주지만, equals에서는 대소문자를 무시하여 확인하다.

   ~~~java
   public final class CaseInsensitiveString {
       private final String s;
   
       public CaseInsensitiveString(String s) {
          this.s = Objects.requireNonNull(s);
       }
   
     	//대소문자 무시후 비교
       public boolean equals(Object o) {
           if(o instanceof CaseInsensitiveString) {
               return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
           }
           
           if(o instanceof String) {
               return s.equalsIgnoreCase((String) o);
           }
           return false;
       }
   }
   
   //String equalsIgnoreCase
   public boolean equalsIgnoreCase(String anotherString) {
     return this == anotherString ? true : anotherString != null &&
       anotherString.length() == this.length() && 
       this.regionMatches(true, 0, anotherString, 0, this.length());
   }
   
   //String 대소문자 무시 로직
   public static boolean regionMatchesCI(byte[] value, int toffset, byte[] other, int ooffset, int len) {
     int last = toffset + len;
   
     while(toffset < last) {
       char c1 = (char)(value[toffset++] & 255);	//char형으롭 변경 
       char c2 = (char)(other[ooffset++] & 255);
       if (c1 != c2) {
         char u1 = Character.toUpperCase(c1);	//대문자로 변경
         char u2 = Character.toUpperCase(c2);
         if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
           return false;
         }
       }
     }
   
     return true;
   }
   ~~~

   

   - CaseInsensitiveString의 equals는 순진하게 일반 문자열과도 비교를 시도한다. 

     ~~~java
     CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
     String s = "polish";
     ~~~

     - 위를 참고로 cis.equals(s)를 실행해봤을 때 결과는 true를 반환한다.
     - 문제는 CaseInsensitiveString의 equals는 일반 String을 알고 있지만, String의 equals는 CaseInsensitiveString의 존재여부를 모른다. **따라서 s.equals(cis)는 false라는 결과를 나타낸다.**
     - 즉, **명백한 대칭성 위반이다.**

     

   - 이번에는 CaseInsensitiveString을 컬렉션에 넣어보자

     ~~~java
     List<CaseInsensitiveString> List = new ArrayList<>();
     list.add(cis);
     ~~~

     - list.contains(s);를 호출하면 어떤 결과가 나올까?
       - JDK 버전에 따라 다르므로 true 혹은 false를 반환하거나 런타임 예외가 발생할 수도 있다.

   

   - 여기에서 요점은 **equals 규약을 어기면 그 객체를 사용하는 다른 객체들이 어떻게 반응할지 모른다는 것이다.**

   - 위의 문제를 해결하려면 **CaseInsensitiveString의 equals를 String과도 연동하겠다는 꿈을 버려야 한다.**

     ~~~java
     @Override
     public boolean equals(Object o) {
       return o instanceof CaseInsensitiveString && ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
     }
     ~~~

     

   

   - **equalsIgnoreCase에 속한 regionMatchesCI의 로직흐름을 직접 테스트 해봤다.**

   ~~~java
   public static void main(String [] args) {
   
     String str2 = new String("hHllo");
   
     //1. 받아온 String을 byte배열에 담는다.
     byte [] bt = str2.getBytes();
     System.out.println("비트 값 : " + bt[0] + ", " + bt[1]);
   
     //2. 255로 비트연산을 진행 후 char형으로 변환한다.
     char a = (char) (bt[0] & 255);
     char b = (char) (bt[1] & 255);
     System.out.println("char 변환 값 : " + a +","+ b);
   
     //3. Character의 toUpperCase()를 활용하여 모두 대문자로 변경한다.
     char u1 = Character.toUpperCase(a);
     char u2 = Character.toUpperCase(b);
     System.out.println("대문자 변환 값 : " + u1 +","+ u2);
   }
   ~~~

   - **실행 결과**

   ![image](https://user-images.githubusercontent.com/40616436/74012433-954a0180-49cd-11ea-8f04-c689bbfe33d1.png)

   

3. **추이성**은 첫번째 객체와 두번째 객체가 같고, 두번째 객체와 세번째 객체가 같다면, 첫번째 객체와 세번째 객체도 같아야 한다는 뜻이다.

   - 만약, 상위 클래스에는 없는 필드를 하위 클래스에 추가하는 상황을 생각해보자(equals 비교에 영향을 주는 정보를 추가한 것이다.)

     ~~~java
     
     ~~~

     

   