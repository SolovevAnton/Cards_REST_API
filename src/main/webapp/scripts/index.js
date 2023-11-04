let user_id = 1;

function fillTableCardsForUser(){
    $("#cardsTableBody").html("");
    $.ajax({
        type: "GET",
        url: `cards?userId=${user_id}`,
        success: [function (result) {
            let cards = result.data;
            for (let i = 0; i < cards.length; i++) {
                let markup = "<tr>" +
                    "<td>" + cards[i].question + "</td>" +
                    "<td>" + cards[i].answer + "</td>" +
                    "<td>" + cards[i].category.name + "</td>" +
                    "<td>" + cards[i].creationDate + "</td>" +
                    "</tr>";

                $("#cardsTableBody").append(markup);
            }
        }],
        error: [function (e) {
            alert("error");
            alert(JSON.stringify(e));
        }]
    });
}

