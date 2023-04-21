package data.models

import com.google.gson.annotations.SerializedName

/**
 * Lot entity.
 *
 * This is used to read from vendor-provided data.
 */
data class FinalLotSafeData(
    val platformLotId: String = "", //номер лота внутри платформы
    val lotTitle: String = "", //название лота
    val lotCategory: String = "", //категория лота (если есть) ---- тут при обработке подставляется категория из файлика отдельного
    val lotTags: String = "", //ключевые слова (если есть)
    val minEstimate: String = "", //минимальный эстимейт
    val maxEstimate: String = "", //максимальный эстимейт
    val openingPrice: String = "", //стартовая цена
    val lotStatus: String = "", //статус лота ( sold unsold passed)
    val saleDate: String = "", //дата в формате DD.MM.YYYY
    val priceRealised: String = "", //цена продажи
    val houseName: String = "", //название аукциона
    val saleId: String = "", //номер торгов внутри источника
    val saleName: String = "", //название торгов
    val auctionCountry: String = "", //страна аукциона US, RU...
    val auctionCity: String = "", //город аукциона
    val lotNumber: String = "", //номер лота внутри торгов
    val lotCurrency: String = "", //валюта GBP USD EUR...
    val lotDescription: String = "", //описание лота
    val originalUrl: String = "", //оригинальный линк на лот
    val imageCount : Int = 0,
    @SerializedName("images")
    val imageList : List<String> = mutableListOf(), //original images urls
    var houseCode: String = "", //номер дома в источнике
    var globalHouseCode: String = "", //наш номер дома; первый источник SR=10****, -"- IN=11****, ...
    val premium: String = "", //премия
    val platformCode: String = "", //название платформы откуда брали SR или DO FR и тд
)