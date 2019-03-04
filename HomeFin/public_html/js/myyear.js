$(document).ready(function () {
    fetchTransWs(lastMonthYear);
    var $selectYear = $("#yearLov");
    for (var i = 0; i < 10; i++) {
        var yr = todayYear - i;
        $selectYear.append($("<option />").val(yr).text(yr));
    }

    $("#searchBtn").click(function () {
        fetchTransWs($("#yearLov").val());
    });
});

function fetchTransWs(year) {
    var urlStr = "http://localhost:8080/HomeFinServices/rest/Trans/sum/" + year;
    //var arrayReturn = [];
    var tbYrObj = $("#sumYrTab");
    tbYrObj.empty();
    $.ajax({
        type: 'GET',
        url: urlStr,
        dataType: "json", // data type of response
        error: function (data) {
            alert("errort");
        },
        success: function (data) {
            tbYrObj.append("<tr><th>Category</th><th>Total Amount</th></tr>");
            for (var i = 0, len = data.length; i < len; i++) {
                var cat = data[i].categoryId;
                var amt = data[i].amount;
                var catDesc = data[i].categoryDescription;
                //arrayReturn.push([data[i].categoryId, data[i].amount, ""]);
                tbYrObj.append("<tr><td>" + cat + " - " + catDesc + "</td><td><a href='#modal-example' uk-toggle id='anc_" + cat + "' onclick='clickAmt(this)'>" + amt + "</a></td>");

            }
            //inittable(arrayReturn);
        }
    });

}

function clickAmt(link) {
    
    var linkId = link.id;
    var categoryId = linkId.split("_")[1];
    var totalAmt = link.innerHTML;
    showSummary(categoryId, $("#yearLov").val(), totalAmt);
}

function showSummary(categoryId, year, sumAmt) {

    var sumUrl = "http://localhost:8080/HomeFinServices/rest/Trans/sum/" + year + "/" + categoryId;
    var tbObj = $("#catSumYrTab");
    tbObj.empty();
    $.ajax({
        type: 'GET',
        url: sumUrl,
        dataType: "json", // data type of response
        error: function (data) {
            alert("errort");
        },
        success: function (data) {
            var tdStrLeft = [];
            var tdStrRight = [];
            for (var i = 0, len = data.length; i < len; i++) {

                if (i < 6)
                    tdStrLeft.push("<td>" + data[i].month + " " + getMonthName(data[i].month) + "</td><td class='uk-text-right'>" + data[i].amount + "</td>");
                else
                    tdStrRight.push("<td>" + data[i].month + " " + getMonthName(data[i].month) + "</td><td class='uk-text-right'>" + data[i].amount + "</td>");

            }

            for (var i = 0; i < 6; i++) {
                tbObj.append("<tr>" + tdStrLeft[i] + tdStrRight[i] + "</tr>");
            }

        }
    });

    $("#popTitle").text(categoryId + " (" + year + ") " + sumAmt);
}