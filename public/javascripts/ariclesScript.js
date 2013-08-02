$(document).ready(openDefaultTab());

function send_request(url, errorContainerName, processSuccessData, type, params){
    console.log("Url: " + url + ", errorContainerName" + errorContainerName);
    $.ajax({
        url: url,
        data: params,
        type: type,
        beforeSend: function(){
            $("#requestProgressBar").addClass("progress-striped active");
        },
        success: function( json ) {
            $("#requestProgressBar").removeClass("progress-striped active");

            if (json.success){
                if (json.data !== undefined)
                    processSuccessData(json.data);
            } else {
                var errorText = "<p class='text-error'> <b> An error occurred</b> </p> ";
                appendAlert(errorContainerName, "error", errorText);
            }
        },
        error: function(request, status, error){
            $("#requestProgressBar").removeClass("progress-striped active");
            appendAlert(errorContainerName, "error", request.responseText);
        }
    });
}

function appendAlert(container, messageClass, text){
    $("#" + container).append("<div class=\"alert alert-" + messageClass + "\">" +
        "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" +
        text +
        "</div>")
}

function openDefaultTab(){
    getGroups();
}

function getGroups(){
    send_request("/groups", 'mainContainer', function(data){
        var groupsContainer = $("#groupsContainer");
        groupsContainer.html("");
        data.forEach(function(pre_groups){pre_groups.forEach(function(group){
            groupsContainer.append(
                "<li id=\"li_" + group.id + "\">" +
                    "<button type=\"button\" class=\"close close-edit btn-large\" data-toggle=\"tooltip\" title=\"Refresh\" onclick=\"getArticlesForGroup('" + group.name + "');\"><i class=\"icon-refresh\"></i></button>" +
                    "<a onclick=\"getArticlesForGroup('" + group.name + "');\" data-toggle=\"tab\">" + group.name +  "</a></li>"
            );
        })});
    }, "GET");
}

function createArticle(){
    $("#form_last_update_container").hide();

    $("#form_id").val('');
    $("#form_caption").val('');
    $("#form_short_description").val('');
    $("#form_body").text('');
    $("#form_group").val('');

    @if(isAdmin){
        $("#form_approve").hide();
    }
}

function editArticle(id){
    $("#formModal").modal("show");
    console.log("Editing article id: " + id);
    send_request("/articles/" + id, 'form_error_container', function(data){
        $("#form_id").val(data.id);
        $("#form_caption").val(data.caption);
        $("#form_short_description").val(data.short_description);
        $("#form_body").text(data.body);
        $("#form_group").val(data.group);
        $("#form_last_update").html(data.last_update);
        $("#form_last_update_container").show();
        @if(isAdmin){
            if (data.approved != 'false'){
                $("#form_approve").show();
                $("#form_approve").attr("onclick", "approveArticle(\""  + data.id  + "\", \"" + data.group  + "\"); $(\"#formModal\").modal(\"hide\");");
            }
        }
    }, "GET");
}

function showArticle(id){
    send_request("/articles/" + id, 'form_error_container', function(data){
        var oneArticleContainer = $("#oneArticleContainer");
        oneArticleContainer.html("<div><h4 aligh=\"center\">" + data.caption + "</h4>" +
            "<p><b>Short Description:</b> " + data.short_description + "</p>" +
            "<p><b>Body</b></p><p>" + data.body + "</p>" +
            "<p><b>Group:</b> " + data.group + "</p>" +
            "<p><b>Last changed:</b> " + data.last_update + "</p>");
    }, "GET");
}

function deleteArticle(id, group){
    console.log("Editing article id: " + id);
    send_request("/articles/" + id + "/delete", 'mainContainer', function(data){
        console.log("delete article: group: " + group);
        $("#li_" + group).addClass("active");
        getArticlesForGroup(group);
    }, "POST");
}

function saveArticle(){
    var group = $("#form_group").val();
    var id = $("#form_id").val();
    var address = "/articles";
    if (id){
        address += "/" + id + "/edit";
    }
    send_request(address, "form_error_container", function(){
        console.log("Save article: group: " + group);
        $("#formModal").modal("hide");
        $("#li_" + group).addClass("active");
        getArticlesForGroup(group);
    }, "POST", $("#article_form").serialize());
}