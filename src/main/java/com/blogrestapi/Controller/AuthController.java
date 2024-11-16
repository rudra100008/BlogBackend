package com.blogrestapi.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.blogrestapi.Dao.UserDao;
import com.blogrestapi.Entity.User;
import com.blogrestapi.Exception.ResourceNotFoundException;
import com.blogrestapi.ServiceImpl.FileServiceImpl;
import com.blogrestapi.ValidationGroup.CreateUserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.blogrestapi.DTO.JwtRequest;
import com.blogrestapi.DTO.JwtResponse;
import com.blogrestapi.DTO.UserDTO;
import com.blogrestapi.Security.JWTTokenHelper;
import com.blogrestapi.Security.UserDetailService;
import com.blogrestapi.Service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private JWTTokenHelper jwtTokenHelper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Value("${project.users.image}")
    private String imagePath;
    @Autowired
    private FileServiceImpl fileService;

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody JwtRequest request) {
        this.authenticate(request.getUsername(), request.getPassword());
        UserDetails userDetails=this.userDetailService.loadUserByUsername(request.getUsername());
        String token= this.jwtTokenHelper.generateToken(userDetails);
        Boolean isTokenExpired=this.jwtTokenHelper.isTokenExpired(token);
        User user=   this.userDao.findByUsername(request.getUsername()).orElseThrow(()->
                new ResourceNotFoundException("User not found"));
        JwtResponse response=new JwtResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setIsTokenExpired(isTokenExpired);
        return ResponseEntity.ok(response);
    }
    public void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        //UsernamePassword holds the username and password of user before authentication after thr authentication
        //it will store the other data like role and permission
        this.authenticationManager.authenticate(token);// authenticationManger checks whether the username and password
        // matches with database username and password

    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated(CreateUserGroup.class) @RequestPart("user") UserDTO userDTO,
                                      BindingResult result,
                                      @RequestPart(value = "image",required = false)MultipartFile imageFile)
    {
        Map<String,Object> response=new HashMap<>();
        if (result.hasErrors()) {
            Map<String,Object> error=new HashMap<>();
            result.getFieldErrors().forEach(f->error.put(f.getField(), f.getDefaultMessage()));
            response.put("status","BAD_REQUEST(400)");
            response.put("message",error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        String image = null;
        if(imageFile !=null && imageFile.isEmpty()) {
            try {
                image = this.fileService.uploadFile(imagePath, imageFile);
            } catch (IOException e) {
                // Log the exception
                response.put("status", "INTERNAL_SERVER_ERROR(500)");
                response.put("message", "Image upload failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } catch (Exception e) {
                // Log unexpected exceptions
                response.put("status", "INTERNAL_SERVER_ERROR(500)");
                response.put("message", "An unexpected error occurred: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
        if(imageFile == null){
            image = "";
        }
        userDTO.setImage(image);
        UserDTO saveUser = this.userService.registerNewUser(userDTO);
        response.put("message", "User inserted successfully");
        response.put("status", "CREATED(201)");
        response.put("data", saveUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
