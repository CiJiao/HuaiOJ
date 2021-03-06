package cn.kastner.oj.service;

import cn.kastner.oj.domain.User;
import cn.kastner.oj.dto.AuthLogDTO;
import cn.kastner.oj.dto.UserDTO;
import cn.kastner.oj.exception.AuthenticationException;
import cn.kastner.oj.exception.UserException;

public interface AuthenticationService {

  User register(UserDTO userDTO) throws UserException;

  User forgotPassword(String password);

  String login(String username, String password) throws AuthenticationException;

  String refresh(String token);

  void counter(AuthLogDTO authLogDTO);
}
