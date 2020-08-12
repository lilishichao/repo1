package com.hopeshop.auth.test;

import com.hopeshop.auth.entity.UserInfo;
import com.hopeshop.auth.utils.JwtUtils;
import com.hopeshop.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by silence on 2020/1/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JwtTest.class)
public class JwtTest {

    private static final String pubKeyPath = "D:\\img\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "D:\\img\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "hopeshop");
    }
    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU5MzQxNjQwMX0.WamMFOeKAAceTuOkMrpI5yQg-sC6ktfY2IxyZy7AOms1lnn6T5yAtB0WkJZ076cQwi1yPuBc9bE5BJuLuvJ2xNz7xPMhKuKyx6dKxM2hL0UP9AM0_iGK0s13MWkUXvo95U6U2yRW88etFnvmmPpg0HF6zVOYcObGFb0cZxCMAbA";
        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
