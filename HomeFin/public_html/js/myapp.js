$(document).ready(function () {
    const url = "http://localhost:8080/HomeFinServices/rest/Trans/" + lastYear;
    var avgPerMonth = 0;
    var biWeek = 0;
    var biWeekPerson = 0;
    var ttl = 0;

    $.get(url, function (data, status) {
        $("#yearTotalAmt").text(data);
        ttl = parseFloat(data);
        avgPerMonth = ttl / 12;
        biWeek = ttl / 26;
        biWeekPerson = ttl / 26 / 2;
        $("#avgMonth").text(avgPerMonth.toFixed(2));
        $("#biWek").text(biWeek.toFixed(2));
        $("#biWekPerson").text(biWeekPerson.toFixed(2));
    });

    $("#totalYear").text(lastYear);
    $("#currYear").text(todayYear);
    
    const url2 = "http://localhost:8080/HomeFinServices/rest/Trans/" + todayYear;
    $.get(url2, function (data, status) {
        $("#currYearTotalAmt").text(data);
        ttl = parseFloat(data);
        avgPerMonth = ttl / 12;
        biWeek = ttl / 26;
        biWeekPerson = ttl / 26 / 2;
        $("#avgMonthCurrYear").text(avgPerMonth.toFixed(2));
        $("#biWekCurrYear").text(biWeek.toFixed(2));
        $("#biWekPersonCurrYear").text(biWeekPerson.toFixed(2));
    });



});

