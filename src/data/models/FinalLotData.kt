package data.models

import com.google.gson.annotations.SerializedName

/**
 * Lot entity.
 *
 * This is used to save final output.
 */
data class FinalLotData(
    val platformLotId: String = "", //номер лота внутри платформы
    var lotTitle: String = "", //название лота
    val lotCategory: String = "", //категория лота (если есть) ---- тут при обработке подставляется категория из файлика отдельного
    val lotTags: String = "", //ключевые слова (если есть)
    val minEstimate: Double = 0.0, //минимальный эстимейт
    val maxEstimate: Double = 0.0, //максимальный эстимейт
    val openingPrice: Double = 0.0, //стартовая цена
    val lotStatus: String = "", //статус лота ( sold unsold passed)
    var saleDate: String = "", //дата в формате DD.MM.YYYY
    val priceRealised: Double = 0.0, //цена продажи
    val houseName: String = "", //название аукциона
    val saleId: String = "", //номер торгов внутри источника
    val saleName: String = "", //название торгов
    val auctionCountry: String = "", //страна аукциона US, RU...
    val auctionCity: String = "", //город аукциона
    val lotNumber: String = "", //номер лота внутри торгов
    val lotCurrency: String = "", //валюта GBP USD EUR...
    var lotDescription: String = "", //описание лота
    val originalUrl: String = "", //оригинальный линк на лот
    val imageCount : Int = 0,
    @SerializedName("images")
    val imageList : List<String> = mutableListOf(), //original images urls
    var houseCode: String = "", //номер дома в источнике
    var globalHouseCode: Int = 0, //наш номер дома; первый источник SR=10****, -"- IN=11****, ...
    var premium: Double = 0.0, //премия
    val platformCode: String = "", //название платформы откуда брали SR или DO FR и тд
)