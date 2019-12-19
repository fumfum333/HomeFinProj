$(document).ready(function () {

    showHideUploadComp(true);
    $("#uploadBtn").click(function () {
        clickUploadBtn();
    });
    $("#genCsvBtn").click(function () {
        clickGenerateCsvBtn();
    });
    $("#updateBtn").click(function () {
        clickUpdateTranBtn();
    });
    $("input[name='radioImport']").change(function () {

        var radioValue = $("input[name='radioImport']:checked").val();
        if (radioValue == "DB") {
            showImportTrans();
        } else if (radioValue == "FILE") {
            showHideUploadComp(true);
        }

    });
    $("#impDates").change(function () {
        var valDate = $('#impDates option:selected').val();
        //alert(valDate);
        //showImportTrans();
        displayImpData(valDate);
    });

    $("#impForm").submit(function () {
        event.preventDefault();
    });

});

var importColumnNames = ["Id", "Trans Date", "Posted Date", "Description", "Category", "Credit", "Debit", "Import Amt", "Memo", "Mark"];

function clickUploadBtn() {
    var fileCmp = $("#fileInput");
    var fileName = fileCmp.val();
    if (fileName.match(/csv$/i)) {
        uploadCsvFile();
    } else {
        alert("NOT CORRECT!!!");
    }

}

function clickGenerateCsvBtn() {
    var valDate = $('#impDates option:selected').val();
    var url = "http://localhost:8080/HomeFinServices/rest/Import/markInd/" + valDate;
    $.ajax({
        type: 'GET',
        url: url,
        dataType: "text", // data type of response
        error: function (err) {
            alert("error "+err);
        },
        success: function (data) {
            alert(data);
        }
    });
}

function clickUpdateTranBtn() {
    var valDate = $('#impDates option:selected').val();
    
    $('input[name="importedDate"]').val(valDate);
    //$( "#impForm" ).submit();

    $.post("http://localhost:8080/HomeFinServices/rest/Pend/updateTrans", $("#updateForm").serialize()).done(function (data) {
        if (data == "Updated") {

            alert("Transactions Updated!");
        }

    });

}

function showHideUploadComp(doShow) {
    if (doShow) {
        $("#fileDiv").show();
        $("#dbDiv").hide();
    } else {
        $("#fileDiv").hide();
        $("#dbDiv").show();
    }

}

function showImportTrans() {
    showHideUploadComp(false);

    var datesLov = $("#impDates");
    var urlDate = "http://localhost:8080/HomeFinServices/rest/Pend/dates";
    var arrayReturn =
            datesLov.empty();
    $.ajax({
        type: 'GET',
        url: urlDate,
        dataType: "json", // data type of response
        error: function (data) {
            alert("errort");
        },
        success: function (data) {
            for (var i = 0, len = data.length; i < len; i++) {
                //alert("data[0]:"+data[0])
                datesLov.append($("<option />").val(data[i]).text(data[i]));
            }

            displayImpData(data[0]);

        }
    });
}

function displayImpData(importDate) {
    var url = "http://localhost:8080/HomeFinServices/rest/Pend/" + importDate;
    var arrayReturn = [];
    var impTranTable = $("#impTranTab");
    impTranTable.empty();
    impTranTable.append("<thead><tr><th>Id</th><th>Tran Date</th><th>Posted Date</th><th>Description</th><th>Category</th><th>Credit</th><th>Debit</th><th>Import Amt</th><th>Memo</th><th>Mark</th><th></th></tr></thead>");
    
    $.ajax({
        type: 'GET',
        url: url,
        dataType: "json", // data type of response
        error: function (data) {
            alert("error");
        },
        success: function (data) {
            for (var i = 0, len = data.length; i < len; i++) {
//                arrayReturn.push([data[i].importSeqId, data[i].transactionDate, data[i].postedDate,
//                    data[i].description, data[i].category, data[i].credit,
//                    data[i].debit, data[i].importAmount, data[i].memo, data[i].markInd, data[i].importDate]);
                var checkStr = "<input type='checkbox' name='markInd' onclick='updateImpData("+data[i].importSeqId+",this)' ";
                if(data[i].markInd == "Y") {
                    checkStr += "checked";
                }
                checkStr += " >"
                
                impTranTable.append("<tr><td>"+data[i].importSeqId+"</td><td>"+data[i].transactionDate+"</td><td>"+data[i].postedDate+"</td><td>"+
                                    data[i].description+"</td><td>"+data[i].category+"</td><td>"+data[i].credit+"</td><td>"+
                                    data[i].debit+"</td><td>"+data[i].importAmount+"</td><td>"+data[i].memo+"</td><td>"+checkStr+"</td><td>"+data[i].importDate+"</td></tr>");
                
            }
            //inittable(arrayReturn);
        }
    });
}

function inittable(data) {
    //console.log(data);
    var table = $("#impTranTab").DataTable({
        "destroy": true,
        "aaData": data,
        "pageLength": 50,
        "columnDefs": [{
                "targets": -1,
                "data": null,
                "defaultContent": "<button type='button'>Modify</button>"
            }]
    });

    $('#impTranTab tbody').on('click', 'button', function () {
        //alert("test");

        var data = table.row($(this).parents('tr')).data();
        var impId = "";
        setTimeout(function () {
            //alert("data:" + data[0]);
            impId = data[0];

            var tbObj = $("#modifyTb");
            tbObj.empty();
            var trStr = "";
            for (var i = 0; i < data.length; i++) {
                //last row
                if (i == (data.length - 1)) {
                    var checked = "checked";
                    if (data[i] == 'N')
                        checked = "";
                    trStr += "<tr><td>" + importColumnNames[i] + "</td><td></td><td><input type='checkbox' id='markCheck' " + checked + " ></td></tr>";
                } else {
                    
                    trStr += "<tr><td>" + importColumnNames[i] + "</td><td>";
                    if(i==0) {
                        trStr += "<input type='hidden' id='modifyTranId' value='"+data[i]+"' >";
                    }
                    trStr +="</td><td>" + data[i] + "</td></tr>";
                }
            }
            tbObj.append(trStr);
            var modal = UIkit.modal("#modal-modify");
            modal.show();
        }, 1000);


        $("#save").click(function () {
            //alert("save "+impId+"id "+ document.getElementById("modifyTranId").value);
            UIkit.modal($("#modal-modify")).hide();
            var tmpMarkInd = "N";
            if (document.getElementById("markCheck").checked) {
                tmpMarkInd = "Y";
            }
            
            if(impId != "" && impId != null && (impId == document.getElementById("modifyTranId").value)) {
                updateImpData(impId, tmpMarkInd);
            }
        });

    });

}

function updateImpData(impSeqId, checkObj) {
    //alert("updateImpData:" + impSeqId + " " + markInd);
    var markInd = "N";
    if(checkObj.checked) {
        markInd = "Y";
    }
    $('input[name="impSeqIdSave"]').val(impSeqId);
    $('input[name="markIndSave"]').val(markInd);
    //$( "#impForm" ).submit();

    $.post("http://localhost:8080/HomeFinServices/rest/Pend/update", $("#impForm").serialize()).done(function (data) {
        if (data == "SUCCESS") {

            //displayImpData($('#impDates option:selected').val());
            
            //alert("saved!")
            
        } else {
            alert("error!!")
        }

    });

}

function uploadCsvFile() {
    var file = $("input[name='fileInput'").get(0).files[0];
    var formData = new FormData();
    formData.append("file", file);
    $.ajax({
        url: "http://localhost:8080/HomeFinServices/rest/Import",
        /*headers: {
         "Content-Type": "application/x-www-form-urlencoded"
         },*/
        type: "POST",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        error: function (data) {
            alert("error");
        },
        success: function (data) {
            alert("success:" + data);
        }
    });
}