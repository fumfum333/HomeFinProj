$(document).ready(function () {
    fetchTransWs(lastMonthYear, lastMonth);

    //poplate year dropdown
    var $selectYear = $("#yearLov");
    for (var i = 0; i < 10; i++) {
        var yr = todayYear - i;
        $selectYear.append($("<option />").val(yr).text(yr));
    }
    //set default year/month
    var $selectMonth = $("#monthLov");
    $selectYear.val(lastMonthYear);
    $selectMonth.val(lastMonth);

    $("#searchBtn").click(function () {

        fetchTransWs($("#yearLov").val(), $("#monthLov").val());
    });

});

function fetchTransWs(year, month) {
    var urlStr = "http://localhost:8080/HomeFinServices/rest/Trans/list/month/" + year + "/" + month;
    var arrayReturn = [];
    $.ajax({
        type: 'GET',
        url: urlStr,
        dataType: "json", // data type of response
        error: function (data) {
            alert("errort");
        },
        success: function (data) {
            for (var i = 0, len = data.length; i < len; i++) {
                arrayReturn.push([data[i].transactionId, data[i].sourceId,
                    data[i].transactionDate, data[i].description, data[i].transAmount,
                    data[i].categoryId]);
            }
            inittable(arrayReturn);
        }
    });
}

function inittable(data) {
    //console.log(data);
    $("#example").DataTable({
        "destroy": true,
        "aaData": data
    });

}


