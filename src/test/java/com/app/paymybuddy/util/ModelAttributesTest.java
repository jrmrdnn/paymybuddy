package com.app.paymybuddy.util;

import static org.mockito.Mockito.*;

import com.app.paymybuddy.dto.response.UserRelationDto;
import com.app.paymybuddy.model.Transaction;
import com.app.paymybuddy.repository.TransactionRepository;
import com.app.paymybuddy.security.CustomUserDetails;
import com.app.paymybuddy.service.RelationService;
import com.app.paymybuddy.service.TransactionService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
public class ModelAttributesTest {

  @Mock
  private RelationService relationService;

  @Mock
  private TransactionService transactionService;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private Model model;

  @Mock
  private CustomUserDetails customUserDetails;

  @InjectMocks
  private ModelAttributes modelAttributes;

  private Set<UserRelationDto> mockRelations;
  private List<Transaction> mockTransactions;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    mockRelations = new HashSet<>();
    mockTransactions = List.of();
    pageable = PageRequest.of(0, 5);
  }

  @Test
  void testAddAttributeTransfer() {
    when(relationService.findUserRelations(authentication)).thenReturn(
      mockRelations
    );
    when(
      transactionService.getTransactionHistory(
        authentication,
        PageRequest.of(0, 5)
      )
    ).thenReturn(mockTransactions);

    modelAttributes.addAttributeTransfer(authentication, model);

    verify(relationService).findUserRelations(authentication);
    verify(transactionService).getTransactionHistory(
      authentication,
      PageRequest.of(0, 5)
    );
    verify(model).addAttribute("relations", mockRelations);
    verify(model).addAttribute("transactions", mockTransactions);
  }

  @Test
  void testAddAttributeTransferWithPageable() {
    when(relationService.findUserRelations(authentication)).thenReturn(
      mockRelations
    );
    when(
      transactionService.getTransactionHistory(authentication, pageable)
    ).thenReturn(mockTransactions);

    modelAttributes.addAttributeTransfer(authentication, model, pageable);

    verify(relationService).findUserRelations(authentication);
    verify(transactionService).getTransactionHistory(authentication, pageable);
    verify(model).addAttribute("relations", mockRelations);
    verify(model).addAttribute("transactions", mockTransactions);
  }
}
