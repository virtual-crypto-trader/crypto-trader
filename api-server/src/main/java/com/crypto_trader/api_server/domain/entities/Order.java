package com.crypto_trader.api_server.domain.entities;

import com.crypto_trader.api_server.domain.OrderSide;
import jakarta.persistence.*;

@Entity(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String market;
    @Enumerated(value = EnumType.STRING)
    private OrderSide side;
    private Number volume;
    private Number price;
    @Enumerated(value = EnumType.STRING)
    private OrderState state;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public Order() {}

    public Order(String market, OrderSide side, Number volume, Number price) {
        this.market = market;
        this.side = side;
        this.volume = volume;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getMarket() {
        return market;
    }

    public OrderSide getSide() {
        return side;
    }

    public Number getVolume() {
        return volume;
    }

    public Number getPrice() {
        return price;
    }

    public OrderState getState() {
        return state;
    }

    public UserEntity getUser() {
        return user;
    }

    public Number totalPrice() {
        return price.doubleValue() * volume.doubleValue();
    }

    /**
     * 매수(bid) 시: user balance > order totalprice
     * 매도(ask) 시: 보유 수량 > order volume
     */
    public void validationWith(UserEntity user) {
        if (side == OrderSide.BID) {
            double balance = user.getAccount().getBalance().doubleValue();
            double totalPrice = totalPrice().doubleValue();
            if (balance < totalPrice)
                throw new RuntimeException();
        } else { // BID
            CryptoAsset asset = user.findCryptoAssetByMarket(market);
            if (asset == null)
                throw new RuntimeException();

            double amount = asset.getAmount().doubleValue();
            if (amount < volume.doubleValue())
                throw new RuntimeException();
        }
    }

    public void cancel(String market) {
        this.state = OrderState.CANCELED;

        if (side == OrderSide.BID) {
            user.getAccount().unlock(totalPrice());
        } else {
            user.findCryptoAssetByMarket(market).unlock(volume);
        }
    }

    // 주문 타입에는
    // - limit: 지정가 주문 (현재는 이것만, 나머지는 TODO)
    // - price: 시장가 주문(매수)
    // - market: 시장가 주문(매도)
    // - best: 최유리 주문
}
