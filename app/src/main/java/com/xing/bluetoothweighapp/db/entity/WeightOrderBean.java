package com.xing.bluetoothweighapp.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class WeightOrderBean {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String orderId;
    private String time;
    private String weigth;
    private String customer;
    private int status;
    private String type;
    @Generated(hash = 886857606)
    public WeightOrderBean(Long id, String orderId, String time, String weigth,
            String customer, int status, String type) {
        this.id = id;
        this.orderId = orderId;
        this.time = time;
        this.weigth = weigth;
        this.customer = customer;
        this.status = status;
        this.type = type;
    }
    @Generated(hash = 59104762)
    public WeightOrderBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOrderId() {
        return this.orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getWeigth() {
        return this.weigth;
    }
    public void setWeigth(String weigth) {
        this.weigth = weigth;
    }
    public String getCustomer() {
        return this.customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }


}
