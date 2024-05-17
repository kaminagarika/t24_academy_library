package jp.co.metateam.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.RentalManage;
import java.util.Date;


@Repository
public interface RentalManageRepository extends JpaRepository<RentalManage, Long> {
    List<RentalManage> findAll();

	Optional<RentalManage> findById(Long id);



//Query:データベースからデータを取り出したり修正したりするための命令文
//?1=渡した値が入る

@Query("SELECT COUNT(*) FROM Stock WHERE id = ?1 AND status = 0")
    Integer count(String id);

@Query("SELECT COUNT (*) FROM RentalManage WHERE stock.id = ?1 AND id != ?2 AND status IN (0,1) AND (expectedReturnOn < ?3 OR ?4 < expectedRentalOn)")
    Integer whetherDay(String stockId, Long Id, Date expected_rental_on, Date expected_return_on);

@Query("SELECT COUNT (*) FROM RentalManage WHERE stock.id = ?1 AND id != ?2 AND status IN (0,1)")
    Integer test(String id, Long Id);

@Query("SELECT COUNT (*) FROM RentalManage WHERE stock.id = ?1 AND status IN (0,1) AND (expectedReturnOn < ?2 OR ?3 < expectedRentalOn)")
    Integer addwhetherDay(String stockId, Date expected_rental_on, Date expected_return_on);

@Query("SELECT COUNT (*) FROM RentalManage WHERE stock.id = ?1 AND status IN (0,1)")
    Integer addtest(String id);


}
