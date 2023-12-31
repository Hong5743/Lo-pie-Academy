package com.project.lpuniv.dayoung.user.login.controller;


import com.project.lpuniv.dayoung.user.login.dao.LoginDao;
import com.project.lpuniv.dayoung.user.login.dto.AuthInfo;
import com.project.lpuniv.dayoung.user.login.dto.UserDto;
import com.project.lpuniv.dayoung.user.login.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller
public class LoginController {

    @Autowired
    private LoginDao loginDao;

    @Autowired
    private AuthService authService;

    @GetMapping(value = "/")
    public String getLogin() {
        return "dayoung/loginForm";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String PostLogin(@RequestParam("user_loginId") String user_loginId, @RequestParam("user_passwd") String user_passwd, HttpSession session) {
        AuthInfo authInfo = authService.authenticate(user_loginId, user_passwd);
        session.setAttribute("authInfo", authInfo);

        String id = user_loginId;

         UserDto userDto= loginDao.loginById(id);
         String userId = userDto.getUser_loginId();
       ;


        if(userId != null ) {
            String hashedPasswd = hashPassword(user_passwd);

            userDto = loginDao.loginByPw(id);
            String dbPassword = userDto.getUser_passwd();


            if(hashedPasswd.equals(dbPassword)){

              int type =authInfo.getUser_tp();

                if(authInfo !=null) {
                    int user_tp =authInfo.getUser_tp();

                    if (user_tp == 1) {
                        return "/dayoung/stuMain";
                    } else if (user_tp == 2) {
                        return "/dayoung/teaMain";

                    } else if (user_tp == 3) {
                        return "/dayoung/adminMain";

                    }
                }
          }
        }
        return "redirect:/";
    }



    private static String hashPassword(@RequestParam("user_passwd") String user_passwd) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(user_passwd.getBytes());

            // 바이트를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}
