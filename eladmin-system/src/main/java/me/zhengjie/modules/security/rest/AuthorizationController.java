package me.zhengjie.modules.security.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
//import me.zhengjie.domain.VerificationCode;
//import me.zhengjie.utils.ElAdminConstant;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.exception.BadRequestException;
//import me.zhengjie.modules.system.service.UserService;
//import me.zhengjie.service.EmailService;
//import com.yzd.service.VerificationCodeService;
import me.zhengjie.config.RsaProperties;
import me.zhengjie.modules.security.config.SecurityProperties;
import me.zhengjie.modules.security.security.TokenProvider;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.utils.RsaUtils;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "系统：系统授权接口")
public class AuthorizationController {

    @Value("${loginCode.expiration}")
    private Long expiration;
    @Value("${single.login}")
    private Boolean singleLogin;
    @Value("${rsa.private_key}")
    private String privateKey;
    private final SecurityProperties properties;
    private final RedisUtils redisUtils;
    private final OnlineUserService onlineUserService;
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    //对于 Controller 与 Service 中的构造方法将使用 @RequiredArgsConstructor 注解替代

    @Log("用户登录")
    @ApiOperation("登录授权")
    @AnonymousAccess
    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        // 密码解密

        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
        String password = authUser.getPassword();
        RSA rsa = new RSA(privateKey, null);
        if ("zyh".equals(authUser.getUsername())) {
            // 注：这里这样做只是给postman测试用的，跳过验证码验证，生产阶段要删了
            authUser.setUsername("admin");
        } else {
            // String password
            password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
            // 查询验证码
            String code = (String) redisUtils.get(authUser.getUuid());
            // 清除验证码
            redisUtils.del(authUser.getUuid());
            if (StringUtils.isBlank(code)) {
                throw new BadRequestException("验证码不存在或已过期");
            }
            if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
                throw new BadRequestException("验证码错误");
            }
        }
        //这里的authentication是待认证，其principal为账号，credentials为密码
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌
        // 令牌中只包含了用户名（subject），权限字符串（claim：authorities）这些用户信息
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回 token 与 用户信息
        Map<String,Object> authInfo = new HashMap<String,Object>(2){{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if(singleLogin){
            // 踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(authUser.getUsername(),token);
        }
        return ResponseEntity.ok(authInfo);
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public ResponseEntity<Object> getUserInfo(){
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
//        JwtUserDto jwtUserDto = (JwtUserDto)userDetailsService.loadUserByUsername(SecurityUtils.getCurrentUsername());
//        return ResponseEntity.ok(jwtUserDto);
    }

    @AnonymousAccess
    @ApiOperation("获取验证码")
    @GetMapping(value = "/code")
    public ResponseEntity<Object> getCode(){
        // 算术类型 https://gitee.com/whvse/EasyCaptcha
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(111, 36);
        // 几位数运算，默认是两位
        captcha.setLen(2);
        // 获取运算的结果
        String result = captcha.text();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        // 保存
        redisUtils.set(uuid, result, expiration, TimeUnit.MINUTES);
        // 验证码信息
        Map<String,Object> imgResult = new HashMap<String,Object>(2){{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @ApiOperation("退出登录")
    @AnonymousAccess
    @DeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request){
        onlineUserService.logout(tokenProvider.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}