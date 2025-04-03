package com.app.paymybuddy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.request.TransferDto;
import com.app.paymybuddy.exception.InsufficientBalanceException;
import com.app.paymybuddy.exception.UserNotFoundException;
import com.app.paymybuddy.model.BankAccount;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.model.User;
import com.app.paymybuddy.repository.BankAccountRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private BankAccountRepository bankAccountRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private CustomUserDetails customUserDetails;

  @Mock
  private Pageable pageable;

  @InjectMocks
  private TransactionService transactionService;

  private User currentUser;
  private User friend;
  private BankAccount bankAccount;
  private final Integer CURRENT_USER_ID = 1;
  private final Integer FRIEND_ID = 2;

  @BeforeEach
  public void setup() {
    currentUser = new User();
    currentUser.setId(CURRENT_USER_ID);
    currentUser.setEmail("user@test.com");

    friend = new User();
    friend.setId(FRIEND_ID);
    friend.setEmail("friend@test.com");

    bankAccount = new BankAccount();
    bankAccount.setUserId(CURRENT_USER_ID);
    bankAccount.setBalance(1000.0);

    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(customUserDetails.getUserId()).thenReturn(CURRENT_USER_ID);
  }

  @Test
  public void getTransactionHistory_ShouldReturnTransformTransactions() {
    List<Transaction> transactions = createSampleTransactions();
    Page<Transaction> transactionPage = new PageImpl<>(transactions);

    when(
      transactionRepository.findTransactionsByUserId(CURRENT_USER_ID, pageable)
    ).thenReturn(transactionPage);

    List<Transaction> result = transactionService.getTransactionHistory(
      authentication,
      pageable
    );

    assertEquals(2, result.size());
    assertEquals(-100.0, result.get(0).getAmount());
    assertEquals(200.0, result.get(1).getAmount());
  }

  @Test
  public void saveTransaction_ShouldSaveSuccessfully() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("friend@test.com");
    transferDto.setAmount("100.00");
    transferDto.setDescription("Test transfer");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("friend@test.com")
    ).thenReturn(Optional.of(friend));
    when(
      transactionRepository.calculateNetTransactionAmountByUserId(
        CURRENT_USER_ID
      )
    ).thenReturn(0.0);
    when(bankAccountRepository.findByUserId(CURRENT_USER_ID)).thenReturn(
      Optional.of(bankAccount)
    );

    assertDoesNotThrow(() ->
      transactionService.saveTransaction(authentication, transferDto)
    );

    // Then
    verify(transactionRepository).save(
      eq(100.0),
      eq("Test transfer"),
      eq(CURRENT_USER_ID),
      eq(FRIEND_ID)
    );
  }

  @Test
  public void saveTransaction_WithInvalidAmount_ShouldThrowException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setAmount("0.00");

    assertThrows(InsufficientBalanceException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );
  }

  @Test
  public void saveTransaction_WithNegativeAmount_ShouldThrowException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setAmount("-100.00");

    assertThrows(InsufficientBalanceException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );
  }

  @Test
  public void saveTransaction_WithNonExistentUser_ShouldThrowException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setAmount("100.00");
    transferDto.setEmail("nonexistent@test.com");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("nonexistent@test.com")
    ).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );
  }

  @Test
  public void saveTransaction_WithInsufficientBalance_ShouldThrowException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("friend@test.com");
    transferDto.setAmount("1500.00");
    transferDto.setDescription("Test transfer");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("friend@test.com")
    ).thenReturn(Optional.of(friend));
    when(
      transactionRepository.calculateNetTransactionAmountByUserId(
        CURRENT_USER_ID
      )
    ).thenReturn(0.0);
    when(bankAccountRepository.findByUserId(CURRENT_USER_ID)).thenReturn(
      Optional.of(bankAccount)
    );

    assertThrows(InsufficientBalanceException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );
  }

  @Test
  public void saveTransaction_WithCommaInAmount_ShouldParseCorrectly() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("friend@test.com");
    transferDto.setAmount("100,50");
    transferDto.setDescription("Test transfer");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("friend@test.com")
    ).thenReturn(Optional.of(friend));
    when(
      transactionRepository.calculateNetTransactionAmountByUserId(
        CURRENT_USER_ID
      )
    ).thenReturn(0.0);
    when(bankAccountRepository.findByUserId(CURRENT_USER_ID)).thenReturn(
      Optional.of(bankAccount)
    );

    assertDoesNotThrow(() ->
      transactionService.saveTransaction(authentication, transferDto)
    );

    verify(transactionRepository).save(
      eq(100.50),
      eq("Test transfer"),
      eq(CURRENT_USER_ID),
      eq(FRIEND_ID)
    );
  }

  private List<Transaction> createSampleTransactions() {
    List<Transaction> transactions = new ArrayList<>();

    Transaction outgoing = new Transaction();
    outgoing.setId(1);
    outgoing.setAmount(100.0);
    outgoing.setDescription("Outgoing");
    outgoing.setSender(currentUser);
    outgoing.setReceiver(friend);
    outgoing.setCreatedAt(LocalDateTime.now());

    Transaction incoming = new Transaction();
    incoming.setId(2);
    incoming.setAmount(200.0);
    incoming.setDescription("Incoming");
    incoming.setSender(friend);
    incoming.setReceiver(currentUser);
    incoming.setCreatedAt(LocalDateTime.now());

    transactions.add(outgoing);
    transactions.add(incoming);

    return transactions;
  }

  @Test
  public void saveTransaction_WithNoBankAccount_ShouldThrowUserNotFoundException() {
    TransferDto transferDto = new TransferDto();
    transferDto.setEmail("friend@test.com");
    transferDto.setAmount("100.00");
    transferDto.setDescription("Test transfer");

    when(
      userRepository.findByEmailAndDeletedAtIsNull("friend@test.com")
    ).thenReturn(Optional.of(friend));
    when(
      transactionRepository.calculateNetTransactionAmountByUserId(
        CURRENT_USER_ID
      )
    ).thenReturn(0.0);
    when(bankAccountRepository.findByUserId(CURRENT_USER_ID)).thenReturn(
      Optional.empty()
    );

    assertThrows(UserNotFoundException.class, () ->
      transactionService.saveTransaction(authentication, transferDto)
    );

    verify(bankAccountRepository).findByUserId(CURRENT_USER_ID);
  }
}
