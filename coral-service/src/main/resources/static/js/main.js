$(document).ready(function () {
    $("#creation-sql-form").submit(function (event) {
        event.preventDefault();
        run_creation_sql();
    });

    $("#translation-sql-form").submit(function (event) {
        event.preventDefault();
        run_translation_sql();
    });
});

function run_creation_sql() {
    var statement = $("#statement").val();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/api/catalog-ops/execute",
        data: statement,
        dataType: 'json',
        cache: false,
        timeout: 600000,
        complete: function (e) {
            let feedback;
            if (e.status === 200) {
                feedback = "<div class=\"alert alert-success\" role=\"alert\">" + statement + " : " + e.responseText + "</div>";
            } else {
                feedback = "<div class=\"alert alert-danger\" role=\"alert\">" + statement + " : " + e.responseText + "</div>";
            }
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
            const feedback = `<div class="form-group"><label for="query">Translation Result</label>
            <textarea disabled rows="5" class="form-control" id="translation-translation-result">${e.responseText}</textarea></div>`;
            $('#translation-result').html(feedback);
        }
    });
}