package com.andersenlab.etalon.accountservice.controller.impl;

import com.andersenlab.etalon.accountservice.controller.AccountController;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.RequestOptionDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInfoResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.accountservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.accountservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.accountservice.service.AccountService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {

  private final AccountService accountService;
  private final AuthenticationHolder authenticationHolder;

  @PostMapping(ACCOUNT_V1_URL)
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResponseDto addNewAccount(@RequestBody AccountCreationRequestDto accountCreation) {
    log.info(
        "{addNewAccount} -> request for add new account from user with id #{}",
        authenticationHolder.getUserId());

    return accountService.createAccount(accountCreation);
  }

  @PatchMapping(ACCOUNT_V1_ID_PATH_URL)
  public MessageResponseDto updateAccount(
      @PathVariable Long accountId, @RequestBody final AccountRequestDto accountRequestDto) {
    accountService.updateAccount(accountId, accountRequestDto);
    return new MessageResponseDto(MessageResponseDto.ACCOUNT_UPDATED_SUCCESSFULLY);
  }

  @GetMapping(ACCOUNT_V1_URL)
  public List<AccountNumberResponseDto> getAllAccountNumbers(@RequestParam String userId) {
    return accountService.getAllAccountNumbers(userId);
  }

  @GetMapping(ACCOUNT_V2_URL)
  public AccountInfoResponseDto getAllAccountInfoBySelectedOption(RequestOptionDto options) {
    return accountService.getAccountInfoBySelectedOption(options);
  }
}
