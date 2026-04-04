    package com.management.inventory.user.controller;

    import com.management.inventory.user.dto.UserRequestDTO;
    import com.management.inventory.user.dto.UserResponseDTO;
    import com.management.inventory.user.service.UserServices;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/user")
    @CrossOrigin(origins = "http://localhost:5173")
    public class UserController {

        @Autowired
        private UserServices services;

        @PostMapping
        public UserResponseDTO createUser(@RequestBody UserRequestDTO user){
            return services.create(user);
        }

        @GetMapping
        public List<UserResponseDTO> listUsers(){
            return services.listUsers();
        }

        @PutMapping(value = "/{id}")
        public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO user){
            return services.updateUser(id, user);
        }

        @DeleteMapping(value = "/{id}")
        public ResponseEntity<?> delete(@PathVariable Long id){
            services.deleteUser(id);
            return ResponseEntity.ok().build();
        }

    }
