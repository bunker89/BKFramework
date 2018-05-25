# BKFramework
BKFramework는 비즈니스 로직과 네트워크 로직을 완전히 분리 시킨 네트워크 프레임워크이다.
PeerBase, Packet, PacketFactory, Writer, PeerLife, Secure 가 주된 클래스이며 각각의 기능들이 추상화 되어있어 교체가 용이하다.
특정 데이터타입이 아닌 제너럴로 타입이 선언되어 있어 데이터 타입에 종속적이지 않다.

PeerBase는 패킷을 종합하여 하위로 전달하고 하위에 Writer를 제공한다. 
생명 주기가 네트워크 방식에 따라 달라질 수 있으므로 생명주기를 설정할 수 있다.

Packet은 읽어드린 데이터가 패킷으로 변경된 인터페이스 이다.

PacketFactory는 데이터를 Packet으로 변경시키는 인터페이스 이다.

Writer는 네트워크 방식에 맞춰 데이터를 전송할 수 있는 인터페이스 이다.

PeerLife는 관리될 수 있는 생명주기를 가지기 위한 인터페이스 이다.

BKFramework는 BK Server API와 BK Client API에서 쉽게 사용 가능하도록 제공된다.
BKFramework는 네트워크 로직만을 위한 프로젝트 이고 실 사용은 BK Server API나 BK Client API를 사용한다.
