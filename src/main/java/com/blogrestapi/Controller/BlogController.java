
package com.blogrestapi.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


import com.blogrestapi.Dao.UserDao;
import com.blogrestapi.Entity.User;
import com.blogrestapi.Exception.ResourceNotFoundException;
import com.blogrestapi.Security.UserDetailService;
import com.blogrestapi.ServiceImpl.FileServiceImpl;
import com.blogrestapi.ValidationGroup.UpdateUserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.blogrestapi.DTO.UserDTO;
import com.blogrestapi.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000")
public class BlogController {
    @Autowired
    private UserService userService;
    @Autowired
    private FileServiceImpl fileService;
    @Value("${project.users.image}")
    private String imagePath;
    @Autowired
    private UserDao userDao;
    // this handler get all the user data from the database
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUser() {
        List<UserDTO> getAllUser = this.userService.getUsers();
        if (getAllUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(getAllUser);
    }
    @GetMapping(value = "/users/getImage/{image}",produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getUserImage(@PathVariable("image")String image)throws  IOException{
        try {
            InputStream is = this.fileService.getFile(imagePath, image);
            byte[] b = is.readAllBytes();
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(b);
        }catch (FileNotFoundException file){
            throw new FileNotFoundException("Image not found with the name: "+image);
        }

    }


    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") int id) {
        UserDTO getUserById = this.userService.getUserById(id);
        return ResponseEntity.ok(getUserById);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") int id) {
        Map<String, Object> response = new HashMap<>();
        UserDTO getUserById = this.userService.getUserById(id);
        this.userService.deleteUserById(id);
        response.put("message", "User deleted successfully");
        response.put("status", "OK(200)");
        response.put("data", getUserById);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> putUserById(@PathVariable("id") int id,@Validated(UpdateUserGroup.class) @RequestPart("user") UserDTO user,
                                         BindingResult result,
                                         @RequestPart(value="image",required = false) MultipartFile imageFile) {


        Map<String,Object> response=new HashMap<>();
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String currentUser= null;
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
            UserDetails userDetails =(UserDetails) authentication.getPrincipal();
            currentUser =userDetails.getUsername();
        }
        User getCurrentUserDetails = this.userDao.findByUsername(currentUser).orElseThrow(
                ()-> new ResourceNotFoundException("User not found")
        );
        if(getCurrentUserDetails == null || getCurrentUserDetails.getId() != id){
            response.put("status", "FORBIDDEN(403)");
            response.put("message", "You are not authorized to update this user.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        if (result.hasErrors()) {
            Map<String,Object> fieldError=new HashMap<>();
            result.getFieldErrors().forEach(err->fieldError.put(err.getField(), err.getDefaultMessage()));
            response.put("status", "BAD_REQUEST(400)");
            response.put("message", fieldError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        String image= null;
        if(imageFile != null && !imageFile.isEmpty()) {
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
        if(imageFile ==  null || imageFile.isEmpty()){
            image = "";
        }
        user.setImage(image);
        UserDTO updatedUser = this.userService.updateUserById(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    @PostMapping(path = "/users/{id}/uploadImage")
    public ResponseEntity<?> postImage(@PathVariable("id")int id, @RequestPart("userImage") MultipartFile imageFile){
       UserDTO userDTO= this.userService.getUserById(id);
       String fileName=null;
       try {
            fileName=this.fileService.uploadFile(imagePath,imageFile);

       }catch(IOException io){
           System.out.println(io.getMessage());
           return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(io.getLocalizedMessage());
       }catch (Exception e){
           System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
       userDTO.setImage(fileName);
       UserDTO user =this.userService.updateUserById(id,userDTO);
       return ResponseEntity.status(HttpStatus.OK).body(user) ;
    }

}
