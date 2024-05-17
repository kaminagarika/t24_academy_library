package jp.co.metateam.library.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.repository.AccountRepository;
import jp.co.metateam.library.repository.RentalManageRepository;
import jp.co.metateam.library.repository.StockRepository;
import jp.co.metateam.library.values.RentalStatus;
import org.springframework.data.jpa.repository.Query;


@Service
public class RentalManageService {

    private final AccountRepository accountRepository;
    private final RentalManageRepository rentalManageRepository;
    private final StockRepository stockRepository;

     @Autowired
    public RentalManageService(
        AccountRepository accountRepository,
        RentalManageRepository rentalManageRepository,
        StockRepository stockRepository
    ) {
        this.accountRepository = accountRepository;
        this.rentalManageRepository = rentalManageRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public List <RentalManage> findAll() {
        List <RentalManage> rentalManageList = this.rentalManageRepository.findAll();

        return rentalManageList;
    }

    @Transactional
    public RentalManage findById(Long id) {
        return this.rentalManageRepository.findById(id).orElse(null);
    }

    @Transactional 
    public void save(RentalManageDto rentalManageDto) throws Exception {
        try {
            Account account = this.accountRepository.findByEmployeeId(rentalManageDto.getEmployeeId()).orElse(null);
            if (account == null) {
                throw new Exception("Account not found.");
            }

            Stock stock = this.stockRepository.findById(rentalManageDto.getStockId()).orElse(null);
            if (stock == null) {
                throw new Exception("Stock not found.");
            }

            RentalManage rentalManage = new RentalManage();
            rentalManage = setRentalStatusDate(rentalManage, rentalManageDto.getStatus());

            rentalManageDto.setId(rentalManage.getId());

            rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
            //rentalManage.setAccount(account);

            rentalManage.setExpectedRentalOn(rentalManageDto.getExpectedRentalOn());
            rentalManage.setExpectedReturnOn(rentalManageDto.getExpectedReturnOn());
            rentalManage.setStatus(rentalManageDto.getStatus());

            rentalManageDto.setStockId(rentalManage.getStock().getId());
            // rentalManage.setStock(stock);


            // データベースへの保存
            this.rentalManageRepository.save(rentalManage);
        } catch (Exception e) {
            throw e;
        }
    }

    private RentalManage setRentalStatusDate(RentalManage rentalManage, Integer status) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        if (status == RentalStatus.RENTAlING.getValue()) {
            rentalManage.setRentaledAt(timestamp);
        } else if (status == RentalStatus.RETURNED.getValue()) {
            rentalManage.setReturnedAt(timestamp);
        } else if (status == RentalStatus.CANCELED.getValue()) {
            rentalManage.setCanceledAt(timestamp);
        }

        return rentalManage;
    }


    @Transactional
    public void update(Long id, RentalManageDto rentalManageDto) throws Exception {
        try {
            //正式にEmployeeIdを手に入れてます
            //手に入れてなかったら"Account not found."を表示する
            Account account = this.accountRepository.findByEmployeeId(rentalManageDto.getEmployeeId()).orElse(null);
            if (account == null) {
                throw new Exception("Account not found.");
            }
 
            Stock stock = this.stockRepository.findById(rentalManageDto.getStockId()).orElse(null);
            if (stock == null) {
                throw new Exception("Stock not found.");
            }
 
            //新しい箱を作る
            //初期表示が終わった段階だから
            RentalManage rentalManage = new RentalManage();
            rentalManage = setRentalStatusDate(rentalManage, rentalManageDto.getStatus());
 
            rentalManage.setId(rentalManageDto.getId());
            rentalManage.setAccount(account);
            rentalManage.setExpectedRentalOn(rentalManageDto.getExpectedRentalOn());
            rentalManage.setExpectedReturnOn(rentalManageDto.getExpectedReturnOn());
            rentalManage.setStatus(rentalManageDto.getStatus());
            rentalManage.setStock(stock);
 
            // データベースへの保存
            this.rentalManageRepository.save(rentalManage);
        } catch (Exception e) {
            throw e;
        }
    }


    // @Transactional
    // public Optional<String> rentalAble(String PassStockId, Date expected_rental_on, Date expected_return_on){
      
    //         if(rentalManageRepository.count(PassStockId) == 0){
    //             return Optional.of("この本は利用不可です。");
    //         }
           
    //         if(rentalManageRepository.whetherDate(PassStockId, expected_rental_on, expected_return_on)!= 0){
    //             return Optional.of("この期間での利用はできません。");
    //         }
    // }

@Transactional
    public Optional<String> whetherRental(String stockId, Long passId, Date returnDay, Date rentalDay) {
        if (rentalManageRepository.count(stockId) == 0) {
            return Optional.of("この本は利用できません");
        }

        // int s = rentalManageRepository.datecount(stockId, returnDay,rentalDay)

        //日付がかぶっていないやつの数
        if(rentalManageRepository.whetherDay(stockId, passId, returnDay, rentalDay) != rentalManageRepository.test(stockId, passId)) {
            return Optional.of("この期間は別の貸出と重複しているため貸出できません");
        }
        return Optional.empty(); //空を返す
        
    }

    @Transactional
    public Optional<String> whetherRentalAdd(String stockId, Date returnDay, Date rentalDay) {
        if (rentalManageRepository.count(stockId) == 0) {
            return Optional.of("この本は利用できません");
        }

        // int s = rentalManageRepository.datecount(stockId, returnDay,rentalDay)

        //日付がかぶっていないやつの数
        if(rentalManageRepository.addwhetherDay(stockId, returnDay, rentalDay) != rentalManageRepository.addtest(stockId)) {
            return Optional.of("この期間は別の貸出と重複しているため貸出できません");
        }
        return Optional.empty(); //空を返す
        
    }
}