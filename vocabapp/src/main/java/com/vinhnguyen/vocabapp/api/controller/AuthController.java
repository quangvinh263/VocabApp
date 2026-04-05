package com.vinhnguyen.vocabapp.api.controller;

import com.vinhnguyen.vocabapp.application.dto.AuthResponse;
import com.vinhnguyen.vocabapp.application.dto.LoginRequest;
import com.vinhnguyen.vocabapp.domain.entity.User;
import com.vinhnguyen.vocabapp.infrastructure.repository.UserRepository;
import com.vinhnguyen.vocabapp.infrastructure.security.CustomUserDetailsService;
import com.vinhnguyen.vocabapp.infrastructure.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Các API ở đây sẽ bắt đầu bằng /api/auth
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. API ĐĂNG KÝ (Tạo thẻ thành viên)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest request) {
        // Kiểm tra xem tên đã có ai dùng chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Username đã tồn tại!"));
        }

        // Tạo User mới và BĂM mật khẩu bằng BCrypt
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getUsername() + "@gmail.com"); // Sinh email tự động
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Mật khẩu bị băm nát tại đây
        userRepository.save(user);

        return ResponseEntity.ok(new AuthResponse(null, "Đăng ký thành công! Hãy đăng nhập."));
    }

    // 2. API ĐĂNG NHẬP (Phát vòng tay JWT)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Trình CCCD cho máy xác thực kiểm tra (So sánh mật khẩu thô và mã băm trong DB)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Lấy hồ sơ ra
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // In vòng tay (Token)
        String jwt = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt, "Đăng nhập thành công!"));
    }
}