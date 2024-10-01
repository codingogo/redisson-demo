package io.codingogo.redissondemo.service;

import io.codingogo.redissondemo.entity.Account;
import io.codingogo.redissondemo.exception.AccountNotFoundException;
import io.codingogo.redissondemo.exception.InsufficienFundsException;
import io.codingogo.redissondemo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "lock_account";

    @Transactional
    public Account createAccount(String owner, BigDecimal balance) {
        Account account = new Account(owner, balance);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccount(Long accountId) throws AccountNotFoundException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        String lockKey = LOCK_PREFIX + accountId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("Could not acquire lock for account: " + accountId);
            }

            Account account =   accountRepository.findById(accountId).orElseThrow(()-> new AccountNotFoundException("Account not found: " + accountId));

            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while trying to acquire lock for account " + accountId, e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        String lockKey = LOCK_PREFIX + accountId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(10,5,TimeUnit.SECONDS);
            if (!isLocked) {throw new RuntimeException("Could not acquire lock for account: " + accountId);}

            Account account = accountRepository.findById(accountId)
                    .orElseThrow(()->new AccountNotFoundException("Account not found!" + accountId));
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficienFundsException("Insufficient funds!");
            }

            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while trying to acquire lock for account " + accountId, e);
        } catch (AccountNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
