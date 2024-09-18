package com.myOnlineShoppingService.accountsService.services;

import com.myOnlineShoppingService.accountsService.exception.AccountNotBelongToOwnerException;
import com.myOnlineShoppingService.accountsService.exception.AccountNotFoundException;
import com.myOnlineShoppingService.accountsService.models.Account;
import com.myOnlineShoppingService.accountsService.models.AccountDTO;
import com.myOnlineShoppingService.accountsService.models.Customer;
import com.myOnlineShoppingService.accountsService.persistence.IAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.*;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
class AccountServiceImplTest {

    @Mock
    private IAccountRepository accountRepository;

    @Mock
    private IAccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Validator validator;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    @DisplayName("Prueba positiva de findAccountByIdAndOwnerId")
    void findAccountByIdAndOwnerId_PositiveTest() {
        Long accountId = 5L;
        Long ownerId = 2L;

        Customer owner = new Customer(ownerId);

        Account account =
                new Account(accountId, "Checking", 1500, owner);

        AccountDTO accountDTO =
                new AccountDTO(accountId, "Checking", 1500, ownerId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.mapToAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.findAccountByIdAndOwnerId(accountId, ownerId);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(accountId));
        assertThat(result.getOwner_id(), is(ownerId));
        assertThat(result.getType(), is("Checking"));
        assertThat(result.getBalance(), is(1500));

        verify(accountRepository).findById(accountId);
        verify(accountMapper).mapToAccountDTO(account);
        verifyNoMoreInteractions(accountRepository);
        verifyNoMoreInteractions(accountMapper);
    }


    @Test
    @DisplayName("Prueba negativa de findAccountByIdAndOwnerId")
    void findAccountByIdAndOwnerId_NegativeTest() {
        Long accountId = 5L;
        Long incorrectOwnerId = 3L;

        Customer owner = new Customer(2L);

        Account account =
                new Account(accountId, "Checking", 1500, owner);


        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountNotBelongToOwnerException exception = assertThrows(AccountNotBelongToOwnerException.class, () -> {
            accountService.findAccountByIdAndOwnerId(accountId, incorrectOwnerId);
        });

        assertThat(exception.getMessage(), containsString("does not belong to owner with ID: " + incorrectOwnerId));

        verify(accountRepository).findById(accountId);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(accountMapper);
    }

    @Test
    @DisplayName("Prueba positiva de createAccount()")
    void createAccount_PositiveTest() {
        Long ownerId = 3L;

        AccountDTO newAccountDTO = new AccountDTO(null, "Personal", 0, ownerId);
        Customer owner = new Customer(ownerId, "Alfredo Castañas", "alfredo.castañas@yopmail.com");
        Account newAccount = new Account(null, "Personal", 0, owner);
        Account savedAccount = new Account(10L, "Personal", 0, owner);
        AccountDTO savedAccountDTO = new AccountDTO(10L, "Personal", 0, ownerId);

        when(accountMapper.mapToAccount(newAccountDTO)).thenReturn(newAccount);
        when(accountRepository.save(newAccount)).thenReturn(savedAccount);
        when(accountMapper.mapToAccountDTO(savedAccount)).thenReturn(savedAccountDTO);

        AccountDTO result = accountService.createAccount(newAccountDTO);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(10L));
        assertThat(result.getType(), is("Personal"));
        assertThat(result.getBalance(), is(0));
        assertThat(result.getOwner_id(), is(ownerId));

        verify(accountMapper).mapToAccount(newAccountDTO);
        verify(accountRepository).save(newAccount);
        verify(accountMapper).mapToAccountDTO(savedAccount);
        verifyNoMoreInteractions(accountMapper, accountRepository);
    }

    @Test
    @DisplayName("Prueba negativa de createAccount()")
    void createAccount_NegativeTest() {
        AccountDTO invalidAccountDTO = new AccountDTO(null, "InvalidType", -102, null);

        Set<ConstraintViolation<AccountDTO>> violations = validator.validate(invalidAccountDTO);

        assertThrows(ConstraintViolationException.class, () -> {
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            accountService.createAccount(invalidAccountDTO);
        });
    }


    @Test
    @DisplayName("Prueba positiva de deleteAccount()")
    void deleteAccount_PositiveTest() {
        Long accountId = 7L;
        Account existingAccount = new Account(accountId, "Personal", 1000, null);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        doNothing().when(accountRepository).deleteById(accountId);

        boolean result = accountService.deleteAccount(accountId);

        assertThat(result, is(true));

        verify(accountRepository).findById(accountId);
        verify(accountRepository).deleteById(accountId);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    @DisplayName("Prueba negativa de deleteAccount()")
    void deleteAccount_NegativeTest() {
        Long accountId = 7L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertFalse(accountService.deleteAccount(accountId));
        verify(accountRepository).findById(accountId);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    @DisplayName("Prueba positiva de updateAccount()")
    void updateAccount_PositiveTest() {
        Long accountId = 6L;
        AccountDTO updateAccountDTO = new AccountDTO(accountId, "Personal", 1500, 2L);
        Customer owner = new Customer(2L, "John Doe", "john.doe@example.com");
        Account existingAccount = new Account(accountId, "Personal", 1000, owner);
        Account updatedAccount = new Account(accountId, "Personal", 1500, owner);
        AccountDTO updatedAccountDTO = new AccountDTO(accountId, "Personal", 1500, 2L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(ArgumentMatchers.any(Account.class))).thenReturn(updatedAccount);
        when(accountMapper.mapToAccountDTO(updatedAccount)).thenReturn(updatedAccountDTO);

        AccountDTO result = accountService.updateAccount(updateAccountDTO);

        assertThat(result, is(notNullValue()));
        assertThat(result.getId(), is(accountId));
        assertThat(result.getBalance(), is(1500));
        assertThat(result.getType(), is("Personal"));
        assertThat(result.getOwner_id(), is(2L));

        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(ArgumentMatchers.<Account>any());
        verify(accountMapper).mapToAccountDTO(updatedAccount);
        verifyNoMoreInteractions(accountRepository, accountMapper);
    }

    @Test
    @DisplayName("Prueba negativa de updateAccount()")
    void updateAccount_NegativeTest() {
        Long accountId = 1L;
        AccountDTO updateAccountDTO = new AccountDTO(accountId, "Personal", 1500, 2L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.updateAccount(updateAccountDTO);
        });

        assertThat(exception.getMessage(), containsString("Account not exits."));

        verify(accountRepository).findById(accountId);
        verifyNoMoreInteractions(accountRepository, accountMapper);
    }
}