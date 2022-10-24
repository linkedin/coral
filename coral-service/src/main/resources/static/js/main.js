$(document).ready(function () {
    $("#btn-create").click(function (e) {
        e.preventDefault();
        run_creation_sql();
    });

    $("#btn-translate").click(function (e) {
        e.preventDefault();
        run_translation_sql();
    });

    $("#btn-clear-create").click(function (e) {
        e.preventDefault();
        $("#statement").val("");
        $("#creation-result").html("");
    });

    $("#btn-clear-translate").click(function (e) {
        e.preventDefault();
        $("#query").val("");
        $("#translation-result").html("");
    })
});

function run_creation_sql() {
    var statement = $("#statement").val().trim();
    console.log(statement);
    if (statement === "") {
        const feedback = `<div class="alert alert-danger" role="alert">Creation SQL can't be empty</div>`;
        $('#creation-result').html(feedback);
        return;
    }
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/api/catalog-ops/execute",
        data: statement,
        dataType: 'json',
        cache: false,
        timeout: 600000,
        complete: function (e) {
            const feedback = `<div class="alert alert-${e.status === 200 ? "success" : "danger"}" role="alert">${e.status === 200 ? statement + ": " : ""}${get_message(e.responseText)}</div>`;
            $('#creation-result').html(feedback);
        }
    });
}

function run_translation_sql() {
    var translateRequestBody = {};
    translateRequestBody["fromLanguage"] = $("#from-language").val();
    translateRequestBody["toLanguage"] = $("#to-language").val();
    translateRequestBody["query"] = $("#query").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/api/translations/translate",
        data: JSON.stringify(translateRequestBody),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        complete: function (e) {
            const feedback = `<div class="form-group"><textarea disabled rows="5" class="form-control" id="translated-sql">${get_message(e.responseText)}</textarea></div>`;
            $('#translation-result').html(feedback);
        }
    });
}

function get_message(responseText) {
    try {
        const json = JSON.parse(responseText);
        return "message" in json ? json.message : responseText;
    } catch (e) {
        return responseText;
    }
}