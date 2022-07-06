package com.t2010a.baovemuaxuan.service;

import com.t2010a.baovemuaxuan.entity.Account;
import com.t2010a.baovemuaxuan.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

/**
 * Có nhiệm vụ tìm user theo username
 * và trả về đối tượng của lớp UserDetails.
 */
@Service
@Transactional
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm account theo user name trong bảng accounts.
        Account account = accountRepository.findAccountByUsername(username);
        /*
            Tạo danh sách quyền. (một người dùng có thể có nhiều quyền).
            Có thể tạo ra bảng quyền riêng mapping n - n với accounts
         */
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (account.getRole() == 1) {
            // thêm quyền theo trường role trong account
            authorities.add(new SimpleGrantedAuthority("user"));
        } else if (account.getRole() == 2) {
            authorities.add(new SimpleGrantedAuthority("admin"));
        } else {
            authorities.add(new SimpleGrantedAuthority("guest"));
        }
        /*
            Tạo đối tượng user detail theo thông tin username, password và quyền được lấy ra ở trên.
            Trong đó password là pass được mã hoá
         */
        return new User(account.getUsername(), account.getPasswordHash(), authorities);
    }
}
