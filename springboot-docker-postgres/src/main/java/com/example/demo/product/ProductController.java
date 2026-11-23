package com.example.demo.product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/products")
public class ProductController {
 private final ProductRepository repository;
 public ProductController(ProductRepository repository){this.repository=repository;}
 @GetMapping public List<Product> all(){return repository.findAll();}
 @GetMapping("/{id}") public Product one(@PathVariable Long id){return repository.findById(id).orElseThrow(()->new ProductNotFoundException(id));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Product create(@Valid @RequestBody Product product){return repository.save(product);}
 @PutMapping("/{id}") public Product update(@PathVariable Long id,@Valid @RequestBody Product input){
  Product current=repository.findById(id).orElseThrow(()->new ProductNotFoundException(id));
  current.setName(input.getName());current.setPrice(input.getPrice());return repository.save(current);
 }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){
  if(!repository.existsById(id))throw new ProductNotFoundException(id);repository.deleteById(id);
 }
}