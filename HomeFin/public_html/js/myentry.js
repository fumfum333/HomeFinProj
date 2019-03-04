/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 */

$(document).ready(function () {
    populateCategoryLov();

    var $dateTran = $("#transactionDate").datepicker({ dateFormat: appDateFormat });
    
    $("#submitBtn").click(function(){
        $("#entryForm").valid();
        
    });

    $("#entryForm").validate({
        rules: {
            categoryId: 'required',
            transactionDate: 'required',
            description: 'required',
            transAmount: {
                required: true,
                number: true
            }
        },
        messages: {
            categoryId: 'This field is required',
            transactionDate: 'This field is required',
            transAmount: 'Enter a valid amount'
        },
        submitHandler: function (form) {
            createTranEntry();
            return false;
        }
    });
    
//    $("#entryForm").submit(function(){
//        event.preventDefault();
//        //createTranEntry();
//
//    });

});

function populateCategoryLov() {
    var dropdown = $('#categoryId');
    var url = "http://localhost:8080/HomeFinServices/rest/Cat/list";
    $.getJSON(url, function (data) {
        $.each(data, function (key, entry) {
            dropdown.append($('<option></option>').attr('value', entry.categoryId).text(entry.description));
        });
    });
}

function createTranEntry() {
    alert("updateImpData:");
    
    $.ajax({
              url: "http://localhost:8080/HomeFinServices/rest/Trans/create",
              type: 'POST',
              data: $("#entryForm").serialize(),         
              success: function(data) {
                  alert("syccess "+data);
              },
              error: function(data) {
                  alert("error:"+data.error);
              }
          });

    return false;      

}


