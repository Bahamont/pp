package com.example.demo.product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
@Entity @Table(name="products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Column(nullable=false,length=120) private String name;
 @PositiveOrZero @Column(nullable=false) private double price;
 protected Product() {}
 public Product(String name,double price){this.name=name;this.price=price;}
 public Long getId(){return id;} public String getName(){return name;} public double getPrice(){return price;}
 public void setName(String name){this.name=name;} public void setPrice(double price){this.price=price;}
}