import legacy.BaseLot

//Legacy!

/*
0057AGT4UN
{
  "itemView": {
    "ref": "0057AGT4UN",
    "catalogRef": "00043KPKGD",
    "title": "A Set Of Six George III Old English Pattern Tea Spoons, by Peter & Ann Bateman, London 1815, 6",
    "pieceTitle": "",
    "description": "A Set Of Six George III Old English Pattern Tea Spoons, by Peter & Ann Bateman, London 1815, 6",
    "currency": "GBP",
    "itemType": "AUCTION_LOT",
    "photos": [],
    "lotNumberExtension": "",
    "lotNumber": 165,
    "purchaseDate": 0,
    "postedDate": 1230907500000,
    "isSold": false,
    "isPassed": true,
    "artmyns": [],
    "estimateLow": 40,
    "estimateHigh": 60,
    "inProgress": false,
    "closed": false,
    "extended": false,
    "published": true,
    "medium": "",
    "circa": "",
    "condition": "",
    "dimensions": "",
    "weight": "",
    "literature": "",
    "exhibited": "",
    "provenance": "",
    "notes": ""
  },
  "userItemProperties": {
    "displayPrices": false,
    "bidderCatalogStatus": null,
    "hasBid": false,
    "maxBidAmount": null,
    "bidStatus": null,
    "userKycLimited": null,
    "highBidder": false,
    "winningBidder": false,
    "pendingBid": false,
    "watched": false
  },
  "ref": "0057AGT4UN",
  "_links": {
    "catalog": {
      "href": "https://api.invaluable.com/catalog/00043KPKGD"
    },
    "self": {
      "href": "https://api.invaluable.com/catalog/00043KPKGD/lots/0057AGT4UN"
    }
  },
  "img": []
}

*/
data class INLotData(
    override val platformCode: String = "IN",
    val ref: String,
    val catalogRef: String,


) : BaseLot {
    override fun save(file: String) {
        TODO("Not yet implemented")
    }
    override fun fix() {
        TODO("Not yet implemented")
    }
}