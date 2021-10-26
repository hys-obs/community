/**
 * 提交回复
 */
function post() {
    let questionId = $("#question_id").val();
    let content = $("#comment_content").val();

    comment2Target(questionId, content, 1);
}

function comment2Target(targetId, content, type) {
    if (!content) {
        alert("回复的内容不能为空！");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    let isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=18dfdba7c294af8e102a&redirect_uri=http://localhost:8888/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }

            }
        },
        dataType: "json"
    });
}

function comment(e) {
    let commentId = e.getAttribute("data-id");
    let content = $("#input-" + commentId).val();
    comment2Target(commentId, content, 2);
}


/**
 * 展开二级评论
 */

function collapseComments(e) {
    let id = e.getAttribute("data-id");
    let comments = $("#comment-" + id);
    // comments.toggleClass("in");
    let collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        let subCommentContainer = $("#comment-" + id);
        // 展开二级评论
        comments.addClass("in");
        e.setAttribute("data-collapse", "in");
        e.classList.add("active");
        $.getJSON('/comment/' + id, function (data) {
            $.each(data.data.reverse(), function (index, comment) {

                let mediaLeftElement = $('<div/>', {
                    'class': 'media-left'
                }).append($('<img/>', {
                    'class': 'media-object img-rounded',
                    'src': comment.user.avatarUrl
                }));

                let mediaBodyElement = $('<div/>', {
                    'class': 'media-body'
                }).append($('<h5/>', {
                    'class': 'media-heading',
                    'html': comment.user.name
                })).append($('<div/>', {
                    'html': comment.content
                })).append($('<div/>', {
                    'class': 'comment-menu'
                })).append($('<span/>', {
                    'class': 'pull-right',
                    'html': moment(comment.gmtCreate).format('YYYY-MM-DD')
                }));

                let mediaElement = $('<div/>', {
                    'class': 'media'
                }).append(mediaLeftElement)
                    .append(mediaBodyElement);

                let commentElement = $('<div/>', {
                    'class': 'col-lg-12 col-md-12 col-sm-12 col-xs-12 comments'
                }).append(mediaElement);

                subCommentContainer.prepend(commentElement);

                // 展开二级评论
                comments.addClass("in");
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        });
    }
}

function showSelectTag() {
    $("#select-tag").show();
}

function selectTag(e) {
    let value = e.getAttribute("data-tag");
    let previous = $("#tag").val();
    if (previous.indexOf(value) == -1) {
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }

}

