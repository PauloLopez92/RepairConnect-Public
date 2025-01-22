package com.singlesoft.repaircon.models;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Service implements Serializable {
    private String id;
    private String model;
    private String tag;
    private String description;
    private BigDecimal laborTax;
    private BigDecimal partCost;
    private BigDecimal finalPrice;
    private BigDecimal payed;
    private BigDecimal discount;
    private Customer customer;
    private User user;
    private int status;
    private int payway;

    private Timestamp previsionTime;

    private List<String> imgName = new ArrayList<>();
    /* // Constructor
    public Service(String model, String description,double laborTax, double partCost,
                         Customerid customer, Techid tech, int status) {
        this.description = description;
        this.model = model;
        this.laborTax = laborTax;
        this.partCost = partCost;
        this.customer = customer;
        this.tech = tech;
        this.status = status;
    }
     */
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getModel() {
        return model;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getLaborTax() {
        return laborTax;
    }
    public void setLaborTax(BigDecimal laborTax) {
        this.laborTax = laborTax;
    }
    public BigDecimal getPartCost() {
        return partCost;
    }
    public void setPartCost(BigDecimal partCost) {
        this.partCost = partCost;
    }
    /*
    public void setFinalPrice(){
        this.finalPrice = partCost.add(laborTax).multiply(discount.divide(BigDecimal.valueOf(100)));
    }
    */
    public BigDecimal getFinalPrice(){
        return finalPrice;
    }
    public BigDecimal getPayed() {
        return payed;
    }
    public void setPayed(BigDecimal payed) {
        this.payed = payed;
    }
    public BigDecimal getDiscount() {
        return discount;
    }
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public long getCustomerID(){
        return customer.getId();
    }
    public String getCustomerName(){
        return customer.getName();
    }
    public int getPayway() {
        return payway;
    }
    public void setPayway(int payway) {
        this.payway = payway;
    }
    // ImgName Getter and Setter
    public List<String> getImgName() {
        return imgName;
    }
    public void setImgName(String imgName) {
        this.imgName.add(imgName);
    }

    public Timestamp getPrevisionTime() {
        return previsionTime;
    }

    public void setPrevisionTime(Timestamp previsionTime) {
        this.previsionTime = previsionTime;
    }
}
/*
public class ServiceTable {
    private String id;

    private String model;

    private String description;

    private Double partCost;

    private Double laborTax;

    private Double finalPrice;

    private Customer customer;

    private Tech tech;

    private String status;

    private Timestamp PrevisionTime;

    private Timestamp CreationTime;

    //Id getter and setter
    public String getId() {
        return id;

    }
    public void setId( String id) {
        this.id = id;
    }

    // Model getter and setter
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    // Description Getter and Setter
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // Part cost getter and setter
    public Double getPartCost() {
        return partCost;
    }
    public void setPartCost(Double partCost) {
        this.partCost = partCost;
    }

    // LaborTax getter and setter
    public Double getLaborTax() {
        return laborTax;
    }
    public void setLaborTax(Double laborTax) {
        this.laborTax = laborTax;
    }

    // Final Cost getter and setter
    public Double getFinalPrice() {
        return finalPrice;
    }
    public void setFinalPrice() {
        this.finalPrice = this.laborTax+this.partCost;
    }

    // Customer getter and setter
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Technician getter and setter
    public Tech getTech() {
        return tech;
    }
    public void setTech(Tech tech) {
        this.tech = tech;
    }

    // Status getter and setter
    public Integer getStatus() {
        //return this.status;
        String[] statusOp = {"Budget","Authorized","NoFix","Finished"};
        for (int i = 0; i < statusOp.length; i++) {
            if (this.status.equals(statusOp[i])) {
                return i;
            }
        }
        return -1;
    }
    public void setStatus(Integer statusNum) {
        //this.status = statusNum;
        if(statusNum<=3) {
            String[] statusOp = {"Budget","Authorized","NoFix","Finished"};
            this.status = statusOp[statusNum];
        }
    }

    // forecast Timestamp getter and setter
    public Timestamp getPrevisionTime() {
        return PrevisionTime;
    }
    public void setPrevisionTime(Timestamp previsionTime) {
        PrevisionTime = previsionTime;
    }

    // Creation Timestamp getter and setter
    public Timestamp getCreationTime() {
        return CreationTime;
    }
    public void setTimestamp(Timestamp Time) {
        // Set the current Timestramp
        //this.CreationTime = new Timestamp(System.currentTimeMillis());
        this.CreationTime = Time;
    }
}
 */