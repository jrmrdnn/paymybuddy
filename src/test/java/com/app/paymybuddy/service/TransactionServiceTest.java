package com.app.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.exception.InsufficientBalanceException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.TransactionRepository;
import com.app.paymybuddy.repository.UserRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private TransactionService transactionService;

  private CustomUserDetails userDetails;
  private User sender;
  private User receiver;

  @BeforeEach
  void setUp() {
    userDetails = mock(CustomUserDetails.class);
    when(userDetails.getUserId()).thenReturn(1);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    sender = new User();
    sender.setId(1);
    sender.setEmail("sender@test.com");

    receiver = new User();
    receiver.setId(2);
    receiver.setEmail("receiver@test.com");
  }

  @Test
  void getTransactionHistory_shouldReturnTransformList() {
    List<Transaction> transactions = new ArrayList<>();

    Transaction transaction1 = new Transaction();
    transaction1.setId(1);
    transaction1.setDescription("Transaction 1");
    transaction1.setAmount(100.0);
    transaction1.setSender(sender);
    transaction1.setReceiver(receiver);
    transaction1.setCreatedAt(LocalDateTime.now());

    Transaction transaction2 = new Transaction();
    transaction2.setId(2);
    transaction2.setDescription("Transaction 2");
    transaction2.setAmount(50.0);
    transaction2.setSender(receiver);
    transaction2.setReceiver(sender);
    transaction2.setCreatedAt(LocalDateTime.now());

    transactions.add(transaction1);
    transactions.add(transaction2);

    Pageable pageable = PageRequest.of(0, 5);

    when(
      transactionRepository.findTransactionsByUserId(1, pageable)
    ).thenReturn(new org.springframework.data.domain.PageImpl<>(transactions));

    List<Transaction> result = transactionService.getTransactionHistory(
      authentication,
      pageable
    );

    assertEquals(2, result.size());
    assertEquals(-100.0, result.get(0).getAmount());
    assertEquals(50.0, result.get(1).getAmount());
    verify(transactionRepository).findTransactionsByUserId(1, pageable);
  }

  @Test
  void saveTransaction_shouldThrowUserNotFoundException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("no_existent@test.com");
    transferDto.setAmount("100.50");
    transferDto.setDescription("Transfer");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("no_existent@test.com")
    ).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );
    verify(transactionRepository, never()).save(
      anyDouble(),
      anyString(),
      anyInt(),
      anyInt()
    );
  }

  @Test
  void getTransactionHistory_withEmptyTransactions_shouldReturnEmptyList() {
    Pageable pageable = PageRequest.of(0, 10);

    when(
      transactionRepository.findTransactionsByUserId(1, pageable)
    ).thenReturn(
      new org.springframework.data.domain.PageImpl<>(new ArrayList<>())
    );

    List<Transaction> result = transactionService.getTransactionHistory(
      authentication,
      pageable
    );

    assertTrue(result.isEmpty());
  }

  @Test
  void saveTransaction_withZeroAmount_shouldThrowInsufficientBalanceException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("receiver@test.com");
    transferDto.setAmount("0.0");
    transferDto.setDescription("Invalid Transfer");

    assertThrows(InsufficientBalanceException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );

    verify(transactionRepository, never()).save(
      anyDouble(),
      anyString(),
      anyInt(),
      anyInt()
    );
  }

  @Test
  void saveTransaction_withNegativeAmount_shouldThrowInsufficientBalanceException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("receiver@test.com");
    transferDto.setAmount("-50.0");
    transferDto.setDescription("Invalid Transfer");

    assertThrows(InsufficientBalanceException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );

    verify(transactionRepository, never()).save(
      anyDouble(),
      anyString(),
      anyInt(),
      anyInt()
    );
  }

  @Test
  void saveTransaction_withInsufficientBalance_shouldThrowInsufficientBalanceException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("receiver@test.com");
    transferDto.setAmount("100.50");
    transferDto.setDescription("Transfer");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("receiver@test.com")
    ).thenReturn(Optional.of(receiver));

    when(
      transactionRepository.calculateNetTransactionAmountByUserId(1)
    ).thenReturn(50.0);

    assertThrows(InsufficientBalanceException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );

    verify(transactionRepository, never()).save(
      anyDouble(),
      anyString(),
      anyInt(),
      anyInt()
    );
  }

  @Test
  void saveTransaction_withCommaDecimalSeparator_shouldParseAmountCorrectly() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("receiver@test.com");
    transferDto.setAmount("100,50");
    transferDto.setDescription("Transfer with comma");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("receiver@test.com")
    ).thenReturn(Optional.of(receiver));

    when(
      transactionRepository.calculateNetTransactionAmountByUserId(1)
    ).thenReturn(200.0);

    assertDoesNotThrow(() ->
      transactionService.saveTransaction(authentication, transferDto)
    );

    verify(transactionRepository).save(100.50, "Transfer with comma", 1, 2);
  }
}
