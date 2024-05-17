package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Optional;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.values.RentalStatus;
import jp.co.metateam.library.model.RentalManage;
import java.time.LocalDate;


/**
 * 貸出管理DTO
 */
@Getter
@Setter
public class RentalManageDto {

    private Long id;

    @NotEmpty(message="在庫管理番号は必須です")
    private String stockId;

    @NotEmpty(message="社員番号は必須です")
    private String employeeId;

    @NotNull(message="貸出ステータスは必須です")
    private Integer status;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="貸出予定日は必須です")
    private Date expectedRentalOn;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="返却予定日は必須です")
    private Date expectedReturnOn;

    private Timestamp rentaledAt;

    private Timestamp returnedAt;

    private Timestamp canceledAt;

    private Stock stock;

    private Account account;


public Optional<String> StatusJudgement (Integer preStatus, Integer newStatus, Date expectedRentalOn, Date expectedReturnOn) {

Date now = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

    //0>2
    if(preStatus == RentalStatus.RENT_WAIT.getValue() && newStatus == RentalStatus.RETURNED.getValue()){
        return Optional.of ("貸出待ちから返却済みへの変更はできません");

    //1>0
    }else if(preStatus == RentalStatus.RENTAlING.getValue() && newStatus == RentalStatus.RENT_WAIT.getValue()){
        return Optional.of ("貸出中から貸出待ちへの変更はできません");

    //1>3
    }else if(preStatus == RentalStatus.RENTAlING.getValue() && newStatus == RentalStatus.CANCELED.getValue()){
        return Optional.of ("貸出中からキャンセルへの変更はできません");

    //2>0
    }else if(preStatus == RentalStatus.RETURNED.getValue() && newStatus == RentalStatus.RENT_WAIT.getValue()){
        return Optional.of ("返却済みから貸出待ちへの変更はできません");

    //2>1
    }else if(preStatus == RentalStatus.RETURNED.getValue() && newStatus == RentalStatus.RENTAlING.getValue()){
    return Optional.of ("返却済みから貸出中への変更はできません");

    //2>3
    }else if(preStatus == RentalStatus.RETURNED.getValue() && newStatus == RentalStatus.CANCELED.getValue()){
        return Optional.of ("返却済みからキャンセルへの変更はできません");

    //3>0
    }else if(preStatus == RentalStatus.CANCELED.getValue() && newStatus == RentalStatus.RENT_WAIT.getValue()){
        return Optional.of ("キャンセルから貸出待ちへの変更はできません");

    //3>1
    }else if(preStatus == RentalStatus.CANCELED.getValue() && newStatus == RentalStatus.RENTAlING.getValue()){
        return Optional.of ("キャンセルから貸出中への変更はできません");

    //3>2
    }else if(preStatus == RentalStatus.CANCELED.getValue() && newStatus == RentalStatus.RETURNED.getValue()){
        return Optional.of ("キャンセルから返却済みへの変更はできません");

    }else if(preStatus == RentalStatus.RENT_WAIT.getValue() && newStatus == RentalStatus.RENTAlING.getValue() && !(expectedRentalOn.compareTo(now) == 0)){
        return Optional.of ("貸出予定日を今日の日付に変更してください");

    }else if(preStatus == RentalStatus.RENTAlING.getValue() && newStatus == RentalStatus.RETURNED.getValue() && !(expectedReturnOn.compareTo(now) == 0)){
        return Optional.of ("返却予定日を今日の日付に変更してください");
    }
        return Optional.empty();
}

public Optional<String> StatusJudgementAdd (Integer newStatus, Date expectedRentalOn) {
Date date;
    date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    if(status == RentalStatus.RENTAlING.getValue() && !(expectedRentalOn.compareTo(date) == 0)){
        return Optional.of ("貸出予定日を今日の日付に変更してください");

    }else if(status == RentalStatus.RETURNED.getValue() ||
             status == RentalStatus.CANCELED.getValue()){
return Optional.of("そのステータスでは登録できません");
    }
        
    return Optional.empty();





}

}
        




// // public Optional<String> StatusJudgement(Integer formerStatus, Integer status) throws Exception{
   
// //         Date date = new Date();

//         switch(formerStatus){
//             case 0:
//                 if(status == 1){
//                     this.setExpectedRentalOn(this.getExpectedRentalOn());
//                     if(this.expectedRentalOn == date){
//                         return null;

//                     }else{
//                     return "日付が不正です。"
//                     }
//                 } else if(status == 3) {
//                     return null;

//                 } else {
//                     return "そのステータスへの変更はできません。"
//                 }
//                 break;

//             case 1:
//             if(status == 2){
//                 this.setExpectedRentalOn(this.getExpectedRentalOn());
//                 if(this.expectedRentalOn == date){
//                     return null;

//                 }else{
//                     return "日付が不正です。"
//                 }

//             } else {
//                 return "そのステータスへの変更はできません。"
//             }
//             break;

//                 case 2:
//                 case 3:
//                 return "そのステータスへの変更はできません。"

//         }
// }






// public class StatusJudgement(rentalManage.getStatus(), rentalManageDto.getStatus()){

//     RentalManage rentalManage = new RentalManage;

//     //貸出待ち→貸出中、キャンセル
//     if((status == 0) && (status == 1)) || ((status == 0) && (status == 3)){
//         return null;
//     }

//     //貸出待ち→返却済み
//     if(status == 0) && (status == 2){
//         return String "そのステータスへの変更はできません。";
//     }


//      //貸出中→貸出待ち、キャンセル
//      if((status == 1) && (status == 0)) || ((status == 1) && (status == 3)){
//         return String "そのステータスへの変更はできません。";
//      }
     
//      //貸出中→返却済み
//      if(status == 1) && (status == 2){
//         return null;
//      }

//      //返却済み→他
//      if((status == 2) && (status == 0)) || ((status == 2) && (status == 1)) || ((status == 2) && (status == 3)) {
//         return String "そのステータスへの変更はできません。";
//      }

//      //キャンセル→他
//      if((status == 3) && (status == 0)) || ((status == 3) && (status == 1)) || ((status == 3) && (status == 2)) {
//         return String "そのステータスへの変更はできません。";
//     }
// }
    
    
