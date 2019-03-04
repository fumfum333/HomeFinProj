var todayDate = new Date();
var todayYear = todayDate.getFullYear();
var lastYear = todayDate.getFullYear()-1;
var todayMonth = todayDate.getMonth() + 1;
var lastMonth = null;
var lastMonthYear = todayYear;
if(todayMonth == 1) {
    lastMonth = 12;
    lastMonthYear = todayYear - 1;
} 
else
    lastMonth = todayMonth - 1;

var monthName = [ "January", "February", "March", "April", "May", "June", 
           "July", "August", "September", "October", "November", "December" ];
       
function getMonthName(num) {
    return monthName[num-1];
}

var appDateFormat="mm/dd/yy";

