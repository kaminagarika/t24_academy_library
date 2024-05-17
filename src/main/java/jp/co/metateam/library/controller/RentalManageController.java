package jp.co.metateam.library.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.service.RentalManageService;
import jp.co.metateam.library.service.StockService;
import lombok.extern.log4j.Log4j2;
import java.util.Date;
import jp.co.metateam.library.model.RentalManage;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.ModelAttribute;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.StockDto;
import java.time.LocalDate;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.values.RentalStatus;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.repository.AccountRepository;
import jp.co.metateam.library.model.RentalManageDto;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jp.co.metateam.library.service.BookMstService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;










/**
 * 貸出管理関連クラスß
 */
@Log4j2
@Controller
public class RentalManageController {

    private final AccountService accountService;
    private final RentalManageService rentalManageService;
    private final StockService stockService;

    @Autowired
    public RentalManageController(
        AccountService accountService, 
        RentalManageService rentalManageService, 
        StockService stockService
    ) {
        this.accountService = accountService;
        this.rentalManageService = rentalManageService;
        this.stockService = stockService;
    }

    /**
     * 貸出一覧画面初期表示
     * @param model
     * @return
     */
    @GetMapping("/rental/index")
    public String index(Model model) {
        // 貸出管理テーブルから全件取得
            List<RentalManage> rentalManageList = this.rentalManageService.findAll();
        // 貸出一覧画面に渡すデータをmodelに追加  
            model.addAttribute("rentalManageList", rentalManageList);
        // 貸出一覧画面に遷移
            return "rental/index";
    }
        
        @GetMapping("/rental/add")
        public String add(Model model) {
            List<Account> accounts = this.accountService.findAll();
            List<Stock> stockList = this.stockService.findStockAvailableAll();

    
            model.addAttribute("accounts", accounts);
            model.addAttribute("stockList", stockList);
            model.addAttribute("rentalStatus", RentalStatus.values());
    
            if (!model.containsAttribute("rentalManageDto")) {
                model.addAttribute("rentalManageDto", new RentalManageDto());
            }
    
            return "rental/add";
        }
    
        @PostMapping("/rental/add")
        public String save(@Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra) {
            try {
                if (result.hasErrors()) {
                    throw new Exception("Validation error.");
                }
                

                Optional<String> validErrorOptional = rentalManageDto.StatusJudgementAdd(rentalManageDto.getStatus(), rentalManageDto.getExpectedRentalOn());
                if(validErrorOptional.isPresent()){
                    FieldError fieldError = new FieldError("rentalManageDto", "status", validErrorOptional.get());
                    result.addError(fieldError);
                    throw new Exception("Validation error");
                    }


                Optional<String> a = rentalManageService.whetherRentalAdd(rentalManageDto.getStockId(), new java.sql.Date(rentalManageDto.getExpectedRentalOn().getTime()), new java.sql.Date(rentalManageDto.getExpectedReturnOn().getTime()));
                if(a.isPresent()){
                FieldError fieldError = new FieldError("rentalManageDto", "status", a.get());
                result.addError(fieldError);
                throw new Exception("Validation error");
                }
                

                // 登録処理
                this.rentalManageService.save(rentalManageDto);


    
                return "redirect:/rental/index";
            } catch (Exception e) {
                log.error(e.getMessage());
    
                ra.addFlashAttribute("rentalManageDto", rentalManageDto);
                ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);
               return "redirect:/rental/add";   
            } 
        } 

        // @PostMapping("/rental/add")
        // public String update(@Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra) {
        //     try {
        //         if (result.hasErrors()) {
        //             throw new Exception("Validation error.");
        //         }
        //         // 登録処理
        //         this.rentalManageService.uodate(rentalManageDto);
    
        //         return "redirect:/rental/index";
        //     } catch (Exception e) {
        //         log.error(e.getMessage());
    
        //         ra.addFlashAttribute("rentalManageDto", rentalManageDto);
        //         ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);
        //        return "redirect:/rental/add";   
        //     } 
        // } 

        

//↓の｛id｝は主キーの意味　@GetMappingは全部の表示内容を持ってくる
// @GetMapping("/rental/{id}/edit")
//     public String edit(@PathVariable("id") Long id, Model model) {
//         //@GetMapping("/rental/add")で利用した情報を取ってくる

//         //プルダウンの中身
//         //Account.javaのAccountsテーブルに、AccountService内の情報をすべて取得
//         List<Account> accounts = this.accountService.findAll();
//         //Stock.javaのStockList（html上の58）に、StockService内の利用可能な在庫管理番号の情報をすべて取得（40）
//         List<Stock> stockList = this.stockService.findStockAvailableAll();

//         //model.(=表示する)、
//         //addAttribute(Attribute名,setするキーの名前)（＝htmlから指定したキーの値を受け取ることができる）
//         //Attribute名は、html上のth:eachの＄の名前を使用
//         model.addAttribute("accounts", accounts);
//         model.addAttribute("stockList", stockList);
//         model.addAttribute("rentalStatus", RentalStatus.values());

//         RentalManage rentalManage = this.rentalManageService.findById(id);
//         //"model.containsAttribute" は、特定の属性がモデルに含まれているかどうかを確認するためのメソッド
//         if (!model.containsAttribute("rentalManageDto")) {

//             //新しいRentalManageDtoの箱を作る。もともとのRentalManageDtoの中身が空であれば代入。
//            RentalManageDto rentalManageDto = new RentalManageDto();

//         rentalManageDto.setId(rentalManage.getId());
//         rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
//         rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
//         rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());
//         rentalManageDto.setStockId(rentalManage.getStock().getId());
//         rentalManageDto.setStatus(rentalManage.getStatus());


//         model.addAttribute("rentalManageDto", rentalManageDto);
//         }
//         return "rental/edit";
//         }
//     }

@GetMapping("/rental/{id}/edit")
//{id}で数字を持ってくる（主キーの貸出管理番号）＝初期表示があるから

//@PathVariable=
//@GetMappingアノテーションによって、/rental/{Id}/editというURIに対してGETリクエストを処理するメソッドが定義されている。
//@PathVariableを使用して、Idというパス変数を受け取り、その値を使ってユーザー情報を取得している。

//外から使いたいものを中に持ってくる
public String edit(@PathVariable("id") Long id, Model model) {

    //プルダウンの内容を持ってくる
    //STOCKの主キーとRENTAL_MANAGEの外部キーがつながっているから？（貸出ステータスも内容を持ってこれる？）
    List<Account> accounts = this.accountService.findAll();
    List<Stock> stockList = this.stockService.findStockAvailableAll();

    //model=表示させる内容は一度モデルに入れる
    //htmlのドルの中身と一致させる（箱の名前, 任意の箱の名前）
    model.addAttribute("accounts", accounts);
    model.addAttribute("stockList", stockList);
    model.addAttribute("rentalStatus", RentalStatus.values());

    //RentalmanageをrentalManageと定義する
    //右のやつを左に入れる！！！！！！！
    //個人単位の必要な情報を取り込む（rentalManageに）
    //貸出管理番号は必ず一回につき1個。それを持ってくる
    //idを利用してRENTAL_MANAGEテーブルの項目すべてを抜き出す

    RentalManage rentalManage = this.rentalManageService.findById(id);


    
    //rentalManageDto新しいのを作って、青いrentalManageDtoに入れてる（右から左）
    if (!model.containsAttribute("rentalManageDto")) {
        RentalManageDto rentalManageDto = new RentalManageDto();

        //rentalManageDtoで入るべき小箱を指定している（どこに何を入れればよいかわからないから）↓
        //RENTAL_MANAGEテーブルの項目から、必要な5項目だけを取り出している↓

        //rentalManageDtoは入れ物。rentalManageDtoの中にrentalManageの中のgetId()をセット
        //Id（在庫管理番号がわからないと誰の持ってくるかわからないから）
        //rentalManageDto=弁当箱,setId=弁当箱に卵をセットする。
        //(rentalManage.getId())=(冷蔵庫の中,卵)
        rentalManageDto.setId(rentalManage.getId());

        //弁当箱に卵の黄身を入れる（その黄身は冷蔵庫の中の卵の中の黄身）
        rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
        rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
        rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());
        rentalManageDto.setStockId(rentalManage.getStock().getId());
        rentalManageDto.setStatus(rentalManage.getStatus());

        //弁当箱をモデルに入れて表示する
        model.addAttribute("rentalManageDto", rentalManageDto);
    }

    //編集画面の初期表示にぶち込む
    return "rental/edit";
}



@PostMapping("/rental/{id}/edit")

        //updateの指示
        //"id"は主キー
        //update()の中身は使いたいものをもってきている
        public String update(@PathVariable("id") String id, @Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra, Model model) {
            try {
                if (result.hasErrors()) {
                    throw new Exception("Validation error.");
            }

           RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
            Optional<String> s = rentalManageDto.StatusJudgement(rentalManage.getStatus(), rentalManageDto.getStatus(),rentalManageDto.getExpectedRentalOn(),rentalManageDto.getExpectedReturnOn());
            if (s.isPresent()){
                FieldError fieldError = new FieldError("rentalManageDto", "status", s.get());
                result.addError(fieldError);
                throw new Exception("Validation error");
            }


            // new java.sql.Date(new java.util.Date().getTime())
             Optional<String> a = rentalManageService.whetherRental(rentalManageDto.getStockId(), rentalManageDto.getId(), new java.sql.Date(rentalManageDto.getExpectedRentalOn().getTime()), new java.sql.Date(rentalManageDto.getExpectedReturnOn().getTime()));
                if(a.isPresent()){
                FieldError fieldError = new FieldError("rentalManageDto", "status", a.get());
                result.addError(fieldError);
                throw new Exception("Validation error");
                }
                



                // 登録処理
                //引数は代入する値のこと
                //もし大丈夫だったら↓
                //updateメソッドの中のIDと rentalManageDtoの情報をもってきて、rentalManageServiceにぶち込む
                rentalManageService.update(Long.valueOf(id), rentalManageDto);
    
                return "redirect:/rental/index";

            //
            } catch (Exception e) {
                List<Account> accounts = this.accountService.findAll();
                List<Stock> stockList = this.stockService.findAll();

                model.addAttribute("accounts", accounts);
                model.addAttribute("stockList", stockList);
                model.addAttribute("rentalStatus", RentalStatus.values());

                log.error(e.getMessage());
    
                ra.addFlashAttribute("rentalManageDto", rentalManageDto);
                ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);
               return String.format("redirect:/rental/%s/edit", id); 
            } 
        } 
}


    // @PostMapping("/rental/{id}/edit")
    // public String update(@Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra) {
    //     //例外が発生する可能性のある処理（try‐catch構文）


    //     //try=例外が発生する可能性のあるコードを含むブロック
    //     try {
    //         if (result.hasErrors()) {
    //             throw new Exception("Validation error.");
    //         }
    //         // 更新処理
    //         this.rentalManageService.update(Long.valueOf(id), rentalManageDto);

    //         return "rental/index";

    //     //tryブロックで投げられた例外をキャッチする
    //     //異なる種類の例外を処理するために複数のcatchブロックを持つことができる。
    //     //例外が発生すると、例外の型に応じて適切なcatchブロックが実行される。
    //     } catch (Exception e) {
    //         log.error(e.getMessage());

    //         // //addFlashAttributeはフラッシュ属性を追加するコード。
    //         // //フラッシュ属性とは、リダイレクト（ページ移動）後にフラッシュメッセージを表示する・
    //         // //フォームの入力エラーメッセージを保持して再表示する
    //         // ra.addFlashAttribute("rentalManageDto", rentalManageDto);
    //         // ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);
    //        return "rental/edit";   
    //     } 
    // }
