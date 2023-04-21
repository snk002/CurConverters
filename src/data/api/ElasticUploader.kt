package data.api

import domain.FinalTools
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import data.models.ElasticLot
import data.models.FinalLotData
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class ElasticUploader(srcDir: String, target: String) : FinalTools(srcDir, target) {

    private val gson = Gson()

    private val gsonPretty: Gson = GsonBuilder().disableHtmlEscaping().create()

    private val client = HttpClient.newBuilder().build()

    fun run() {
        var x = 0
        println("Add from $srcDir to elastic server $target")
        print("Continue? [Y/n]")
        if (readLine().toString().contentEquals("n", true)) return
        File(srcDir).walk().forEach {
            if (!it.isFile) return@forEach
            x++
            logProcessed(x)
            if ( x % 10 == 0) return // logProcessed(x)
            if (!readAndCopy(it)) {
                println("Unexpected error on the $x iteration")
                return
            }
        }
    }
    //{"platform_lot_id":"C4E450B888","title":"Baluchi Camel Bag","category":"","tags":"","low_estimate":80.0,"high_estimate":120.0,"starting_price":0.0,"status":"","sale_date":"2019-07-20","hammer_price":20.0,"auction_name":"Bremo Auctions","sale_id":"IZX45FZSRW","sale_name":"Gallery Auction","country":"US","city":"Charlottesville","number":"94","currency":"USD","description":"Baluchi camel bag, measures approximately 18-1/4\" wide by 37-1/2\" long. \n<br> \n<br>All sales are subject to Bremo Auctions Terms &amp; Conditions. Please review before bidding.","original_url":"https://api.invaluable.com/catalog/IZX45FZSRW/lots/C4E450B888","img_url":["https://image.invaluable.com/housePhotos/bremo/15/649615/H5901-L177199960.jpg","https://image.invaluable.com/housePhotos/bremo/15/649615/H5901-L177199963.jpg","https://image.invaluable.com/housePhotos/bremo/15/649615/H5901-L177199964.jpg","https://image.invaluable.com/housePhotos/bremo/15/649615/H5901-L177199966.jpg"],"platform_code":"IN","house_code":"5901","global_house_code":110302}
    //'http://5.9.23.168:9200/3owls-auction/_search?pretty=true&q=annibale'
    override fun readAndCopy(srcFile: File): Boolean {
        return try {
            val str = srcFile.readText(Charsets.UTF_8)
            val lot: FinalLotData = gson.fromJson(str, FinalLotData::class.java)
            val s = gsonPretty.toJson(lot.toElasticModel()).toString()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type","application/json")
                .PUT( HttpRequest.BodyPublishers.ofString(s) )
                .build()
            println(s)
            val response = client.send(request, HttpResponse.BodyHandlers.ofString() )
            println(response.body())
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun FinalLotData.toElasticModel() = ElasticLot(
        platformLotId,
        lotTitle,
        lotCategory,
        lotTags,
        minEstimate,
        maxEstimate,
        openingPrice,
        lotStatus,
        saleDate,
        priceRealised,
        houseName,
        saleId,
        saleName,
        auctionCountry,
        auctionCity,
        lotNumber,
        lotCurrency,
        lotDescription,
        originalUrl,
        imageList,
        platformCode,
        houseCode,
        globalHouseCode
    )
}